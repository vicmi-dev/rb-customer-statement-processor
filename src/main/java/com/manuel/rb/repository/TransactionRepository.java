package com.manuel.rb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manuel.rb.models.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByReference(Long reference);
}