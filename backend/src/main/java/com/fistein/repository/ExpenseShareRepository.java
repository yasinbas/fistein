package com.fistein.repository;

import com.fistein.entity.Expense;
import com.fistein.entity.ExpenseShare;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    
    List<ExpenseShare> findByExpense(Expense expense);
    
    List<ExpenseShare> findByUser(User user);
    
    List<ExpenseShare> findByExpenseAndUser(Expense expense, User user);
    
    @Query("SELECT SUM(es.amount) FROM ExpenseShare es WHERE es.user.id = :userId AND es.expense.group.id = :groupId")
    BigDecimal getTotalOwedByUserInGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy.id = :userId AND e.group.id = :groupId")
    BigDecimal getTotalPaidByUserInGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    @Query("SELECT es FROM ExpenseShare es WHERE es.user.id = :userId AND es.isSettled = false")
    List<ExpenseShare> findUnsettledSharesByUser(@Param("userId") Long userId);
}