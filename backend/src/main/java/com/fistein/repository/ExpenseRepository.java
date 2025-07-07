package com.fistein.repository;

import com.fistein.entitiy.Expense;
import com.fistein.entitiy.Group;
import com.fistein.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroup(Group group);
    List<Expense> findByPaidBy(User paidBy);
    List<Expense> findByGroupOrderByExpenseDateDesc(Group group);
}