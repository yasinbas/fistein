package com.fistein.dto;

import com.fistein.entity.Expense;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal amount;
    private Long groupId;
    private String groupName;
    private UserResponse paidBy;
    private LocalDateTime createdAt;
    private LocalDateTime expenseDate;
    private Expense.SplitType splitType;
    private String notes;
    private List<ExpenseShareResponse> shares;
    private BigDecimal currentUserShare;
    private Boolean isCurrentUserPayer;
}