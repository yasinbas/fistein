package com.fistein.repository;

import com.fistein.entity.Expense;
import com.fistein.entity.Group;
import com.fistein.entity.User;
import com.fistein.enums.SplitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find expenses by group
    List<Expense> findByGroup(Group group);

    // Find expenses by group ordered by creation date desc
    List<Expense> findByGroupOrderByCreatedAtDesc(Group group);

    // Find expenses paid by a specific user
    List<Expense> findByPaidBy(User user);

    // Find expenses where user is a participant
    @Query("SELECT e FROM Expense e JOIN e.participants p WHERE p.id = :userId")
    List<Expense> findExpensesByParticipantId(@Param("userId") Long userId);

    // Find expenses by split type
    List<Expense> findBySplitType(SplitType splitType);

    // Find expenses by group and split type
    List<Expense> findByGroupAndSplitType(Group group, SplitType splitType);

    // Find expense by id with participants loaded
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.participants WHERE e.id = :expenseId")
    Optional<Expense> findByIdWithParticipants(@Param("expenseId") Long expenseId);

    // Find expense by id with expense shares loaded
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.expenseShares WHERE e.id = :expenseId")
    Optional<Expense> findByIdWithExpenseShares(@Param("expenseId") Long expenseId);

    // Find expenses by title containing (case insensitive)
    List<Expense> findByTitleContainingIgnoreCase(String title);

    // Find expenses by group and date range
    @Query("SELECT e FROM Expense e WHERE e.group = :group AND e.createdAt BETWEEN :startDate AND :endDate")
    List<Expense> findByGroupAndDateRange(@Param("group") Group group, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    // Calculate total amount for a group
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.group = :group")
    BigDecimal getTotalAmountForGroup(@Param("group") Group group);

    // Calculate total amount paid by user in a group
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.group = :group AND e.paidBy = :user")
    BigDecimal getTotalAmountPaidByUserInGroup(@Param("group") Group group, @Param("user") User user);

    // Count expenses in group
    long countByGroup(Group group);

    // Count expenses paid by user
    long countByPaidBy(User user);
}