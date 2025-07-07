package com.fistein.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"groupMemberships", "createdGroups", "paidExpenses", "expenseShares"})
@ToString(exclude = {"groupMemberships", "createdGroups", "paidExpenses", "expenseShares"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GroupMember> groupMemberships = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private Set<Group> createdGroups = new HashSet<>();

    @OneToMany(mappedBy = "paidBy")
    @Builder.Default
    private Set<Expense> paidExpenses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<ExpenseShare> expenseShares = new HashSet<>();
}
