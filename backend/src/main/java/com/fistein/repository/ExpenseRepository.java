package com.fistein.repository;

import com.fistein.entity.Expense;
import com.fistein.entity.Group;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByGroup(Group group);
    
    List<Expense> findByPaidBy(User user);
    
    List<Expense> findByGroupOrderByExpenseDateDesc(Group group);
    
    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId ORDER BY e.expenseDate DESC")
    List<Expense> findByGroupIdOrderByExpenseDateDesc(@Param("groupId") Long groupId);
    
    @Query("SELECT e FROM Expense e WHERE e.paidBy.id = :userId OR e.id IN " +
           "(SELECT es.expense.id FROM ExpenseShare es WHERE es.user.id = :userId)")
    List<Expense> findAllUserExpenses(@Param("userId") Long userId);
}