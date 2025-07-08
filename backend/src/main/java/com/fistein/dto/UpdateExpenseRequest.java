package com.fistein.dto;

import com.fistein.entity.Expense;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateExpenseRequest {
    @NotBlank(message = "Açıklama boş olamaz")
    @Size(min = 1, max = 500, message = "Açıklama 1-500 karakter arasında olmalıdır")
    private String description;
    
    @NotNull(message = "Tutar boş olamaz")
    @DecimalMin(value = "0.01", message = "Tutar 0'dan büyük olmalıdır")
    private BigDecimal amount;
    
    private LocalDateTime expenseDate;
    
    @Size(max = 1000, message = "Notlar 1000 karakterden uzun olamaz")
    private String notes;
}