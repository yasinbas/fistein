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
    
    List<ExpenseShare> findByExpenseAndIsSettledFalse(Expense expense);
    
    List<ExpenseShare> findByUserAndIsSettledFalse(User user);
    
    @Query("SELECT SUM(es.shareAmount) FROM ExpenseShare es " +
           "WHERE es.user = :user AND es.expense.group.id = :groupId AND es.isSettled = false")
    BigDecimal calculateTotalDebtByUserAndGroup(@Param("user") User user, @Param("groupId") Long groupId);
    
    @Query("SELECT es FROM ExpenseShare es " +
           "JOIN es.expense e " +
           "WHERE e.group.id = :groupId AND es.user = :user AND es.isSettled = false")
    List<ExpenseShare> findUnsettledSharesByGroupAndUser(@Param("groupId") Long groupId, @Param("user") User user);
}