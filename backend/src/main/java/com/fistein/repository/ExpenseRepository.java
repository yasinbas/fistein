package com.fistein.repository;

import com.fistein.entity.Expense;
import com.fistein.entity.Group;
import com.fistein.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    Page<Expense> findByGroupOrderByCreatedAtDesc(Group group, Pageable pageable);
    
    List<Expense> findByGroupAndCreatedAtBetween(Group group, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT e FROM Expense e " +
           "JOIN FETCH e.shares s " +
           "WHERE e.id = :expenseId")
    Optional<Expense> findByIdWithShares(@Param("expenseId") Long expenseId);
    
    @Query("SELECT e FROM Expense e " +
           "WHERE e.group = :group AND e.paidBy = :user")
    List<Expense> findByGroupAndPaidBy(@Param("group") Group group, @Param("user") User user);
    
    @Query("SELECT e FROM Expense e " +
           "JOIN e.shares s " +
           "WHERE e.group = :group AND s.user = :user AND s.isSettled = false")
    List<Expense> findUnsettledExpensesByGroupAndUser(@Param("group") Group group, @Param("user") User user);
}