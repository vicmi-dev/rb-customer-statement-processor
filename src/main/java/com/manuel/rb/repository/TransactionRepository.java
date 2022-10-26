package com.manuel.rb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manuel.rb.models.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}