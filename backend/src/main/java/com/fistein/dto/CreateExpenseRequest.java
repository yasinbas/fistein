package com.fistein.dto;

import com.fistein.entity.Expense;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateExpenseRequest {
    
    @NotBlank(message = "Açıklama boş olamaz")
    private String description;
    
    @NotNull(message = "Tutar boş olamaz")
    @DecimalMin(value = "0.01", message = "Tutar 0'dan büyük olmalıdır")
    private BigDecimal amount;
    
    private LocalDateTime expenseDate;
    
    @NotNull(message = "Paylaşım tipi boş olamaz")
    private Expense.SplitType splitType;
    
    private List<ExpenseShareRequest> shares;
    
    private String notes;
    
    @Data
    public static class ExpenseShareRequest {
        @NotNull(message = "Kullanıcı ID boş olamaz")
        private Long userId;
        
        private BigDecimal amount; // EXACT split için
        private BigDecimal percentage; // PERCENTAGE split için
    }
}