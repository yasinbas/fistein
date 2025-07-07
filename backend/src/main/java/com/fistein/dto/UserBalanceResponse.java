package com.fistein.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponse {
    private UserResponse user;
    private Long groupId;
    private String groupName;
    private BigDecimal totalPaid;
    private BigDecimal totalOwed;
    private BigDecimal balance;
    private List<DebtDetail> debts;
    private List<CreditDetail> credits;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DebtDetail {
        private UserResponse creditor;
        private BigDecimal amount;
        private Integer expenseCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditDetail {
        private UserResponse debtor;
        private BigDecimal amount;
        private Integer expenseCount;
    }
}