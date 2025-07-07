package com.fistein.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SettleExpenseRequest {
    
    @NotNull(message = "Paylaşım ID'leri boş olamaz")
    private List<Long> shareIds;
    
    private String notes;
}