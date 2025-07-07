package com.fistein.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"expense", "user"})
@EqualsAndHashCode(exclude = {"expense", "user"})
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    @JsonBackReference
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    @Builder.Default
    private Boolean settled = false;

    // Helper methods
    public void markAsSettled() {
        this.settled = true;
    }

    public void markAsUnsettled() {
        this.settled = false;
    }

    public boolean isSettled() {
        return settled != null && settled;
    }
}