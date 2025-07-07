package com.fistein.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"groups", "createdGroups", "paidExpenses", "participatedExpenses", "expenseShares"})
@EqualsAndHashCode(exclude = {"groups", "createdGroups", "paidExpenses", "participatedExpenses", "expenseShares"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String password;

    // Groups where this user is a member
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private Set<Group> groups = new HashSet<>();

    // Groups created by this user
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<Group> createdGroups = new ArrayList<>();

    // Expenses paid by this user
    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<Expense> paidExpenses = new ArrayList<>();

    // Expenses where this user is a participant
    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private Set<Expense> participatedExpenses = new HashSet<>();

    // Expense shares for this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<ExpenseShare> expenseShares = new ArrayList<>();

    // Helper methods
    public void addGroup(Group group) {
        groups.add(group);
        group.getMembers().add(this);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        group.getMembers().remove(this);
    }

    public void addCreatedGroup(Group group) {
        createdGroups.add(group);
        group.setCreatedBy(this);
    }

    public void addPaidExpense(Expense expense) {
        paidExpenses.add(expense);
        expense.setPaidBy(this);
    }

    public void addParticipatedExpense(Expense expense) {
        participatedExpenses.add(expense);
        expense.getParticipants().add(this);
    }

    public void removeParticipatedExpense(Expense expense) {
        participatedExpenses.remove(expense);
        expense.getParticipants().remove(this);
    }

    public void addExpenseShare(ExpenseShare expenseShare) {
        expenseShares.add(expenseShare);
        expenseShare.setUser(this);
    }
}
