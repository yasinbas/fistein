package com.fistein.repository;

import com.fistein.entitiy.ExpenseShare;
import com.fistein.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    List<ExpenseShare> findByUser(User user);
    List<ExpenseShare> findByUserAndIsPaidFalse(User user);
    
    @Query("SELECT SUM(es.shareAmount) FROM ExpenseShare es WHERE es.user = :user AND es.isPaid = false")
    BigDecimal getTotalUnpaidAmountForUser(@Param("user") User user);
}