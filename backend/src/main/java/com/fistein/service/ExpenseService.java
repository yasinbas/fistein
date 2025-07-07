package com.fistein.service;

import com.fistein.dto.*;
import com.fistein.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExpenseService {
    
    ExpenseResponse createExpense(Long groupId, CreateExpenseRequest request, User currentUser);
    
    ExpenseResponse getExpenseById(Long expenseId, User currentUser);
    
    Page<ExpenseResponse> getGroupExpenses(Long groupId, User currentUser, Pageable pageable);
    
    ExpenseResponse updateExpense(Long expenseId, UpdateExpenseRequest request, User currentUser);
    
    void deleteExpense(Long expenseId, User currentUser);
    
    List<ExpenseShareResponse> getExpenseShares(Long expenseId, User currentUser);
    
    void settleExpense(Long expenseId, SettleExpenseRequest request, User currentUser);
    
    UserBalanceResponse getUserBalance(Long groupId, User currentUser);
}