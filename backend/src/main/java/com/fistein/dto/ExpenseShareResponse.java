package com.fistein.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseShareResponse {
    private Long id;
    private UserResponse user;
    private BigDecimal shareAmount;
    private BigDecimal percentage;
    private Boolean isSettled;
}