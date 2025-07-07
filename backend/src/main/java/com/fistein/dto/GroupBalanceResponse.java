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
public class GroupBalanceResponse {
    private Long groupId;
    private String groupName;
    private BigDecimal totalExpenses;
    private List<UserBalanceInfo> userBalances;
    private List<DebtInfo> debts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBalanceInfo {
        private UserResponse user;
        private BigDecimal totalPaid;
        private BigDecimal totalOwed;
        private BigDecimal balance; // Pozitif ise alacaklı, negatif ise borçlu
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DebtInfo {
        private UserResponse debtor; // Borçlu
        private UserResponse creditor; // Alacaklı
        private BigDecimal amount;
    }
}