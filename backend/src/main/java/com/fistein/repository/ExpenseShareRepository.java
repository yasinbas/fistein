package com.fistein.repository;

import com.fistein.entity.Expense;
import com.fistein.entity.ExpenseShare;
import com.fistein.entity.Group;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {

    // Find expense shares by expense
    List<ExpenseShare> findByExpense(Expense expense);

    // Find expense shares by user
    List<ExpenseShare> findByUser(User user);

    // Find expense shares by expense and user
    Optional<ExpenseShare> findByExpenseAndUser(Expense expense, User user);

    // Find unsettled expense shares by user
    @Query("SELECT es FROM ExpenseShare es WHERE es.user = :user AND es.settled = false")
    List<ExpenseShare> findUnsettledByUser(@Param("user") User user);

    // Find settled expense shares by user
    @Query("SELECT es FROM ExpenseShare es WHERE es.user = :user AND es.settled = true")
    List<ExpenseShare> findSettledByUser(@Param("user") User user);

    // Find expense shares by user in a specific group
    @Query("SELECT es FROM ExpenseShare es WHERE es.user = :user AND es.expense.group = :group")
    List<ExpenseShare> findByUserAndGroup(@Param("user") User user, @Param("group") Group group);

    // Find unsettled expense shares by user in a specific group
    @Query("SELECT es FROM ExpenseShare es WHERE es.user = :user AND es.expense.group = :group AND es.settled = false")
    List<ExpenseShare> findUnsettledByUserAndGroup(@Param("user") User user, @Param("group") Group group);

    // Calculate total amount owed by user
    @Query("SELECT SUM(es.amount) FROM ExpenseShare es WHERE es.user = :user AND es.settled = false")
    BigDecimal getTotalAmountOwedByUser(@Param("user") User user);

    // Calculate total amount owed by user in a specific group
    @Query("SELECT SUM(es.amount) FROM ExpenseShare es WHERE es.user = :user AND es.expense.group = :group AND es.settled = false")
    BigDecimal getTotalAmountOwedByUserInGroup(@Param("user") User user, @Param("group") Group group);

    // Calculate total amount settled by user
    @Query("SELECT SUM(es.amount) FROM ExpenseShare es WHERE es.user = :user AND es.settled = true")
    BigDecimal getTotalAmountSettledByUser(@Param("user") User user);

    // Calculate total amount settled by user in a specific group
    @Query("SELECT SUM(es.amount) FROM ExpenseShare es WHERE es.user = :user AND es.expense.group = :group AND es.settled = true")
    BigDecimal getTotalAmountSettledByUserInGroup(@Param("user") User user, @Param("group") Group group);

    // Count unsettled expense shares by user
    @Query("SELECT COUNT(es) FROM ExpenseShare es WHERE es.user = :user AND es.settled = false")
    long countUnsettledByUser(@Param("user") User user);

    // Count settled expense shares by user
    @Query("SELECT COUNT(es) FROM ExpenseShare es WHERE es.user = :user AND es.settled = true")
    long countSettledByUser(@Param("user") User user);

    // Find all expense shares for users who owe money to a specific user
    @Query("SELECT es FROM ExpenseShare es WHERE es.expense.paidBy = :paidByUser AND es.user != :paidByUser AND es.settled = false")
    List<ExpenseShare> findUnsettledSharesOwedToUser(@Param("paidByUser") User paidByUser);
}