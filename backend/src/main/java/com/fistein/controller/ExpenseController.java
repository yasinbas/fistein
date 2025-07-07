package com.fistein.controller;

import com.fistein.dto.*;
import com.fistein.security.CustomUserDetailsService;
import com.fistein.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/groups/{groupId}")
    public ResponseEntity<ExpenseResponse> createExpense(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        ExpenseResponse response = expenseService.createExpense(groupId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpense(
            @PathVariable Long expenseId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        ExpenseResponse response = expenseService.getExpenseById(expenseId, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Page<ExpenseResponse>> getGroupExpenses(
            @PathVariable Long groupId,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        Page<ExpenseResponse> expenses = expenseService.getGroupExpenses(groupId, currentUser, pageable);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long expenseId,
            @RequestBody UpdateExpenseRequest request,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        ExpenseResponse response = expenseService.updateExpense(expenseId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long expenseId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        expenseService.deleteExpense(expenseId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{expenseId}/shares")
    public ResponseEntity<List<ExpenseShareResponse>> getExpenseShares(
            @PathVariable Long expenseId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        List<ExpenseShareResponse> shares = expenseService.getExpenseShares(expenseId, currentUser);
        return ResponseEntity.ok(shares);
    }

    @PostMapping("/{expenseId}/settle")
    public ResponseEntity<Void> settleExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody SettleExpenseRequest request,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        expenseService.settleExpense(expenseId, request, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups/{groupId}/balance")
    public ResponseEntity<UserBalanceResponse> getUserBalance(
            @PathVariable Long groupId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        UserBalanceResponse balance = expenseService.getUserBalance(groupId, currentUser);
        return ResponseEntity.ok(balance);
    }
}