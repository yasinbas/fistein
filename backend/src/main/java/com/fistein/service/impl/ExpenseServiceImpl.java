package com.fistein.service.impl;

import com.fistein.dto.*;
import com.fistein.entity.*;
import com.fistein.repository.*;
import com.fistein.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    public ExpenseResponse createExpense(Long groupId, CreateExpenseRequest request, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserMemberOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu gruba erişim yetkiniz yok");
        }

        Expense expense = Expense.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .group(group)
                .paidBy(currentUser)
                .expenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : LocalDateTime.now())
                .splitType(request.getSplitType())
                .notes(request.getNotes())
                .build();

        expense = expenseRepository.save(expense);

        // Paylaşımları oluştur
        createExpenseShares(expense, request, group);

        return mapToExpenseResponse(expense, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long expenseId, User currentUser) {
        Expense expense = expenseRepository.findByIdWithShares(expenseId)
                .orElseThrow(() -> new RuntimeException("Harcama bulunamadı"));

        if (!isUserMemberOfGroup(expense.getGroup(), currentUser)) {
            throw new RuntimeException("Bu harcamaya erişim yetkiniz yok");
        }

        return mapToExpenseResponse(expense, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getGroupExpenses(Long groupId, User currentUser, Pageable pageable) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserMemberOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu gruba erişim yetkiniz yok");
        }

        Page<Expense> expenses = expenseRepository.findByGroupOrderByCreatedAtDesc(group, pageable);
        return expenses.map(expense -> mapToExpenseResponse(expense, currentUser));
    }

    @Override
    public ExpenseResponse updateExpense(Long expenseId, UpdateExpenseRequest request, User currentUser) {
        Expense expense = expenseRepository.findByIdWithShares(expenseId)
                .orElseThrow(() -> new RuntimeException("Harcama bulunamadı"));

        if (!expense.getPaidBy().getId().equals(currentUser.getId()) && 
            !isUserAdminOfGroup(expense.getGroup(), currentUser)) {
            throw new RuntimeException("Bu harcamayı güncelleme yetkiniz yok");
        }

        // Temel bilgileri güncelle
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription());
        }
        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }
        if (request.getNotes() != null) {
            expense.setNotes(request.getNotes());
        }

        // Paylaşım tipi veya paylaşımlar değiştiyse
        if (request.getSplitType() != null || request.getShares() != null) {
            // Mevcut paylaşımları sil
            expense.getShares().clear();
            expenseShareRepository.deleteAll(expense.getShares());
            
            if (request.getSplitType() != null) {
                expense.setSplitType(request.getSplitType());
            }
            
            // Yeni paylaşımları oluştur
            CreateExpenseRequest createRequest = new CreateExpenseRequest();
            createRequest.setSplitType(expense.getSplitType());
            createRequest.setShares(request.getShares());
            createRequest.setAmount(expense.getAmount());
            
            createExpenseShares(expense, createRequest, expense.getGroup());
        }

        expense = expenseRepository.save(expense);
        return mapToExpenseResponse(expense, currentUser);
    }

    @Override
    public void deleteExpense(Long expenseId, User currentUser) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Harcama bulunamadı"));

        if (!expense.getPaidBy().getId().equals(currentUser.getId()) && 
            !isUserAdminOfGroup(expense.getGroup(), currentUser)) {
            throw new RuntimeException("Bu harcamayı silme yetkiniz yok");
        }

        expenseRepository.delete(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseShareResponse> getExpenseShares(Long expenseId, User currentUser) {
        Expense expense = expenseRepository.findByIdWithShares(expenseId)
                .orElseThrow(() -> new RuntimeException("Harcama bulunamadı"));

        if (!isUserMemberOfGroup(expense.getGroup(), currentUser)) {
            throw new RuntimeException("Bu harcamaya erişim yetkiniz yok");
        }

        return expense.getShares().stream()
                .map(this::mapToExpenseShareResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void settleExpense(Long expenseId, SettleExpenseRequest request, User currentUser) {
        Expense expense = expenseRepository.findByIdWithShares(expenseId)
                .orElseThrow(() -> new RuntimeException("Harcama bulunamadı"));

        if (!isUserMemberOfGroup(expense.getGroup(), currentUser)) {
            throw new RuntimeException("Bu harcamaya erişim yetkiniz yok");
        }

        // Belirtilen paylaşımları ödenmiş olarak işaretle
        for (Long shareId : request.getShareIds()) {
            ExpenseShare share = expense.getShares().stream()
                    .filter(s -> s.getId().equals(shareId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Paylaşım bulunamadı"));

            // Sadece borçlu veya alacaklı ödemeyi onaylayabilir
            if (!share.getUser().getId().equals(currentUser.getId()) && 
                !expense.getPaidBy().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Bu ödemeyi onaylama yetkiniz yok");
            }

            share.setIsSettled(true);
        }

        expenseRepository.save(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBalanceResponse getUserBalance(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserMemberOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu gruba erişim yetkiniz yok");
        }

        // Kullanıcının ödediği toplam
        List<Expense> paidExpenses = expenseRepository.findByGroupAndPaidBy(group, currentUser);
        BigDecimal totalPaid = paidExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Kullanıcının borçlu olduğu toplam
        List<ExpenseShare> owedShares = expenseShareRepository
                .findUnsettledSharesByGroupAndUser(groupId, currentUser);
        BigDecimal totalOwed = owedShares.stream()
                .map(ExpenseShare::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Bakiye hesapla
        BigDecimal balance = totalPaid.subtract(totalOwed);

        // Detaylı borç ve alacak bilgileri
        Map<Long, UserBalanceResponse.DebtDetail> debtMap = new HashMap<>();
        Map<Long, UserBalanceResponse.CreditDetail> creditMap = new HashMap<>();

        // Borçları hesapla
        for (ExpenseShare share : owedShares) {
            if (!share.getExpense().getPaidBy().getId().equals(currentUser.getId())) {
                Long creditorId = share.getExpense().getPaidBy().getId();
                debtMap.computeIfAbsent(creditorId, k -> UserBalanceResponse.DebtDetail.builder()
                        .creditor(mapToUserResponse(share.getExpense().getPaidBy()))
                        .amount(BigDecimal.ZERO)
                        .expenseCount(0)
                        .build());
                
                UserBalanceResponse.DebtDetail debt = debtMap.get(creditorId);
                debt.setAmount(debt.getAmount().add(share.getShareAmount()));
                debt.setExpenseCount(debt.getExpenseCount() + 1);
            }
        }

        // Alacakları hesapla
        for (Expense expense : paidExpenses) {
            for (ExpenseShare share : expense.getShares()) {
                if (!share.getIsSettled() && !share.getUser().getId().equals(currentUser.getId())) {
                    Long debtorId = share.getUser().getId();
                    creditMap.computeIfAbsent(debtorId, k -> UserBalanceResponse.CreditDetail.builder()
                            .debtor(mapToUserResponse(share.getUser()))
                            .amount(BigDecimal.ZERO)
                            .expenseCount(0)
                            .build());
                    
                    UserBalanceResponse.CreditDetail credit = creditMap.get(debtorId);
                    credit.setAmount(credit.getAmount().add(share.getShareAmount()));
                    credit.setExpenseCount(credit.getExpenseCount() + 1);
                }
            }
        }

        return UserBalanceResponse.builder()
                .user(mapToUserResponse(currentUser))
                .groupId(group.getId())
                .groupName(group.getName())
                .totalPaid(totalPaid)
                .totalOwed(totalOwed)
                .balance(balance)
                .debts(new ArrayList<>(debtMap.values()))
                .credits(new ArrayList<>(creditMap.values()))
                .build();
    }

    private void createExpenseShares(Expense expense, CreateExpenseRequest request, Group group) {
        Set<ExpenseShare> shares = new HashSet<>();
        List<GroupMember> activeMembers = groupMemberRepository.findByGroupAndIsActiveTrue(group);

        switch (expense.getSplitType()) {
            case EQUAL:
                createEqualShares(expense, activeMembers, shares);
                break;
            case EXACT:
                createExactShares(expense, request, shares);
                break;
            case PERCENTAGE:
                createPercentageShares(expense, request, shares);
                break;
        }

        expense.setShares(shares);
    }

    private void createEqualShares(Expense expense, List<GroupMember> members, Set<ExpenseShare> shares) {
        BigDecimal shareAmount = expense.getAmount()
                .divide(BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);

        for (GroupMember member : members) {
            ExpenseShare share = ExpenseShare.builder()
                    .expense(expense)
                    .user(member.getUser())
                    .shareAmount(shareAmount)
                    .isSettled(false)
                    .build();
            shares.add(share);
        }
    }

    private void createExactShares(Expense expense, CreateExpenseRequest request, Set<ExpenseShare> shares) {
        if (request.getShares() == null || request.getShares().isEmpty()) {
            throw new RuntimeException("Exact paylaşım için kullanıcı payları belirtilmelidir");
        }

        BigDecimal totalShares = request.getShares().stream()
                .map(s -> s.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalShares.compareTo(expense.getAmount()) != 0) {
            throw new RuntimeException("Paylaşım toplamı harcama tutarına eşit olmalıdır");
        }

        for (CreateExpenseRequest.ExpenseShareRequest shareRequest : request.getShares()) {
            User user = userRepository.findById(shareRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            ExpenseShare share = ExpenseShare.builder()
                    .expense(expense)
                    .user(user)
                    .shareAmount(shareRequest.getAmount())
                    .isSettled(false)
                    .build();
            shares.add(share);
        }
    }

    private void createPercentageShares(Expense expense, CreateExpenseRequest request, Set<ExpenseShare> shares) {
        if (request.getShares() == null || request.getShares().isEmpty()) {
            throw new RuntimeException("Percentage paylaşım için kullanıcı yüzdeleri belirtilmelidir");
        }

        BigDecimal totalPercentage = request.getShares().stream()
                .map(s -> s.getPercentage())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(new BigDecimal("100")) != 0) {
            throw new RuntimeException("Yüzde toplamı 100 olmalıdır");
        }

        for (CreateExpenseRequest.ExpenseShareRequest shareRequest : request.getShares()) {
            User user = userRepository.findById(shareRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            BigDecimal shareAmount = expense.getAmount()
                    .multiply(shareRequest.getPercentage())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            ExpenseShare share = ExpenseShare.builder()
                    .expense(expense)
                    .user(user)
                    .shareAmount(shareAmount)
                    .percentage(shareRequest.getPercentage())
                    .isSettled(false)
                    .build();
            shares.add(share);
        }
    }

    private boolean isUserMemberOfGroup(Group group, User user) {
        return groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user);
    }

    private boolean isUserAdminOfGroup(Group group, User user) {
        return groupMemberRepository.findByGroupAndUser(group, user)
                .map(GroupMember::getIsAdmin)
                .orElse(false);
    }

    private ExpenseResponse mapToExpenseResponse(Expense expense, User currentUser) {
        List<ExpenseShareResponse> shareResponses = expense.getShares().stream()
                .map(this::mapToExpenseShareResponse)
                .collect(Collectors.toList());

        BigDecimal currentUserShare = expense.getShares().stream()
                .filter(s -> s.getUser().getId().equals(currentUser.getId()))
                .map(ExpenseShare::getShareAmount)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .groupId(expense.getGroup().getId())
                .groupName(expense.getGroup().getName())
                .paidBy(mapToUserResponse(expense.getPaidBy()))
                .createdAt(expense.getCreatedAt())
                .expenseDate(expense.getExpenseDate())
                .splitType(expense.getSplitType())
                .notes(expense.getNotes())
                .shares(shareResponses)
                .currentUserShare(currentUserShare)
                .isCurrentUserPayer(expense.getPaidBy().getId().equals(currentUser.getId()))
                .build();
    }

    private ExpenseShareResponse mapToExpenseShareResponse(ExpenseShare share) {
        return ExpenseShareResponse.builder()
                .id(share.getId())
                .user(mapToUserResponse(share.getUser()))
                .shareAmount(share.getShareAmount())
                .percentage(share.getPercentage())
                .isSettled(share.getIsSettled())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}