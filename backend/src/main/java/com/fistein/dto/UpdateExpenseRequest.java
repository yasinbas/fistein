package com.fistein.dto;

import com.fistein.entity.Expense;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateExpenseRequest {
    private String description;
    private BigDecimal amount;
    private LocalDateTime expenseDate;
    private Expense.SplitType splitType;
    private List<CreateExpenseRequest.ExpenseShareRequest> shares;
    private String notes;
}