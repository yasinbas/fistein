package com.fistein.service.impl;

import com.fistein.dto.*;
import com.fistein.entity.Group;
import com.fistein.entity.GroupMember;
import com.fistein.entity.User;
import com.fistein.repository.ExpenseRepository;
import com.fistein.repository.ExpenseShareRepository;
import com.fistein.repository.GroupMemberRepository;
import com.fistein.repository.GroupRepository;
import com.fistein.repository.UserRepository;
import com.fistein.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;

    @Override
    public GroupResponse createGroup(CreateGroupRequest request, User currentUser) {
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(currentUser)
                .isActive(true)
                .build();

        group = groupRepository.save(group);

        // Grubu oluşturan kişiyi admin olarak ekle
        GroupMember creatorMember = GroupMember.builder()
                .group(group)
                .user(currentUser)
                .isAdmin(true)
                .isActive(true)
                .build();

        groupMemberRepository.save(creatorMember);

        return mapToGroupResponse(group, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long groupId, User currentUser) {
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserMemberOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu gruba erişim yetkiniz yok");
        }

        return mapToGroupResponse(group, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getUserGroups(User currentUser) {
        List<Group> groups = groupRepository.findActiveGroupsByUser(currentUser);
        return groups.stream()
                .map(group -> mapToGroupResponse(group, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public GroupResponse addMemberToGroup(Long groupId, AddMemberRequest request, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserAdminOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu işlem için yetkiniz yok");
        }

        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, newMember)) {
            throw new RuntimeException("Kullanıcı zaten bu grubun üyesi");
        }

        GroupMember groupMember = GroupMember.builder()
                .group(group)
                .user(newMember)
                .isAdmin(request.getIsAdmin())
                .isActive(true)
                .build();

        groupMemberRepository.save(groupMember);

        return mapToGroupResponse(group, currentUser);
    }

    @Override
    public void removeMemberFromGroup(Long groupId, Long userId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserAdminOfGroup(group, currentUser) && !currentUser.getId().equals(userId)) {
            throw new RuntimeException("Bu işlem için yetkiniz yok");
        }

        GroupMember member = groupMemberRepository.findByGroupAndUser(group, 
                userRepository.findById(userId).orElseThrow())
                .orElseThrow(() -> new RuntimeException("Üye bulunamadı"));

        member.setIsActive(false);
        groupMemberRepository.save(member);
    }

    @Override
    public GroupResponse updateGroup(Long groupId, UpdateGroupRequest request, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserAdminOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu işlem için yetkiniz yok");
        }

        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            group.setIsActive(request.getIsActive());
        }

        group = groupRepository.save(group);
        return mapToGroupResponse(group, currentUser);
    }

    @Override
    public void deleteGroup(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Sadece grubu oluşturan kişi silebilir");
        }

        group.setIsActive(false);
        groupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserMemberOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu gruba erişim yetkiniz yok");
        }

        List<GroupMember> members = groupMemberRepository.findByGroupAndIsActiveTrue(group);
        return members.stream()
                .map(this::mapToGroupMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupBalanceResponse getGroupBalances(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı"));

        if (!isUserMemberOfGroup(group, currentUser)) {
            throw new RuntimeException("Bu gruba erişim yetkiniz yok");
        }

        // Grup üyelerini al
        List<GroupMember> activeMembers = groupMemberRepository.findByGroupAndIsActiveTrue(group);
        Map<Long, UserResponse> userMap = new HashMap<>();
        Map<Long, BigDecimal> userPaidMap = new HashMap<>();
        Map<Long, BigDecimal> userOwedMap = new HashMap<>();
        
        // Kullanıcı bilgilerini hazırla
        for (GroupMember member : activeMembers) {
            User user = member.getUser();
            userMap.put(user.getId(), mapToUserResponse(user));
            userPaidMap.put(user.getId(), BigDecimal.ZERO);
            userOwedMap.put(user.getId(), BigDecimal.ZERO);
        }

        // Tüm grup harcamalarını al
        List<com.fistein.entity.Expense> expenses = expenseRepository.findByGroupOrderByCreatedAtDesc(group, null).getContent();
        BigDecimal totalExpenses = BigDecimal.ZERO;

        // Her harcama için ödeme ve borç hesapla
        for (com.fistein.entity.Expense expense : expenses) {
            totalExpenses = totalExpenses.add(expense.getAmount());
            
            // Ödeyenin toplam ödemesini güncelle
            Long payerId = expense.getPaidBy().getId();
            userPaidMap.put(payerId, userPaidMap.get(payerId).add(expense.getAmount()));
            
            // Her kullanıcının payını hesapla
            for (com.fistein.entity.ExpenseShare share : expense.getShares()) {
                if (!share.getIsSettled()) {
                    Long userId = share.getUser().getId();
                    userOwedMap.put(userId, userOwedMap.get(userId).add(share.getShareAmount()));
                }
            }
        }

        // Kullanıcı bakiyelerini hesapla
        List<GroupBalanceResponse.UserBalanceInfo> userBalances = new ArrayList<>();
        for (Long userId : userMap.keySet()) {
            BigDecimal paid = userPaidMap.get(userId);
            BigDecimal owed = userOwedMap.get(userId);
            BigDecimal balance = paid.subtract(owed);
            
            userBalances.add(GroupBalanceResponse.UserBalanceInfo.builder()
                    .user(userMap.get(userId))
                    .totalPaid(paid)
                    .totalOwed(owed)
                    .balance(balance)
                    .build());
        }

        // Borç ilişkilerini hesapla (basitleştirilmiş)
        List<GroupBalanceResponse.DebtInfo> debts = calculateDebts(userBalances);

        return GroupBalanceResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .totalExpenses(totalExpenses)
                .userBalances(userBalances)
                .debts(debts)
                .build();
    }

    private List<GroupBalanceResponse.DebtInfo> calculateDebts(List<GroupBalanceResponse.UserBalanceInfo> userBalances) {
        List<GroupBalanceResponse.DebtInfo> debts = new ArrayList<>();
        
        // Alacaklıları ve borçluları ayır
        List<GroupBalanceResponse.UserBalanceInfo> creditors = userBalances.stream()
                .filter(ub -> ub.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.getBalance().compareTo(a.getBalance()))
                .collect(Collectors.toList());
                
        List<GroupBalanceResponse.UserBalanceInfo> debtors = userBalances.stream()
                .filter(ub -> ub.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .map(ub -> {
                    ub.setBalance(ub.getBalance().abs());
                    return ub;
                })
                .sorted((a, b) -> b.getBalance().compareTo(a.getBalance()))
                .collect(Collectors.toList());

        // Basit borç eşleştirme algoritması
        for (GroupBalanceResponse.UserBalanceInfo debtor : debtors) {
            BigDecimal remainingDebt = debtor.getBalance();
            
            for (GroupBalanceResponse.UserBalanceInfo creditor : creditors) {
                if (remainingDebt.compareTo(BigDecimal.ZERO) == 0) break;
                if (creditor.getBalance().compareTo(BigDecimal.ZERO) == 0) continue;
                
                BigDecimal amount = remainingDebt.min(creditor.getBalance());
                
                debts.add(GroupBalanceResponse.DebtInfo.builder()
                        .debtor(debtor.getUser())
                        .creditor(creditor.getUser())
                        .amount(amount)
                        .build());
                
                remainingDebt = remainingDebt.subtract(amount);
                creditor.setBalance(creditor.getBalance().subtract(amount));
            }
        }
        
        return debts;
    }

    private boolean isUserMemberOfGroup(Group group, User user) {
        return groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user);
    }

    private boolean isUserAdminOfGroup(Group group, User user) {
        return groupMemberRepository.findByGroupAndUser(group, user)
                .map(GroupMember::getIsAdmin)
                .orElse(false);
    }

    private GroupResponse mapToGroupResponse(Group group, User currentUser) {
        int memberCount = groupMemberRepository.findByGroupAndIsActiveTrue(group).size();
        boolean isUserAdmin = isUserAdminOfGroup(group, currentUser);

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdBy(mapToUserResponse(group.getCreatedBy()))
                .createdAt(group.getCreatedAt())
                .memberCount(memberCount)
                .isActive(group.getIsActive())
                .isUserAdmin(isUserAdmin)
                .build();
    }

    private GroupMemberResponse mapToGroupMemberResponse(GroupMember member) {
        return GroupMemberResponse.builder()
                .id(member.getId())
                .user(mapToUserResponse(member.getUser()))
                .joinedAt(member.getJoinedAt())
                .isAdmin(member.getIsAdmin())
                .isActive(member.getIsActive())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .build();
    }
}