package com.fistein.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"group", "paidBy", "shares"})
@ToString(exclude = {"group", "paidBy", "shares"})
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private User paidBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expense_date")
    private LocalDateTime expenseDate;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ExpenseShare> shares = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "split_type", nullable = false)
    @Builder.Default
    private SplitType splitType = SplitType.EQUAL;

    private String notes;

    public enum SplitType {
        EQUAL,      // Eşit paylaşım
        EXACT,      // Belirli miktarlar
        PERCENTAGE  // Yüzdelik paylaşım
    }
}