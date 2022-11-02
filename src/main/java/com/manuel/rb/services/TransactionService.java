package com.manuel.rb.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.repository.TransactionRepository;

@Service
public class TransactionService {
	@Autowired
	TransactionRepository transactionRepository;
	
	// Get repeated transactions from the database
	public List<Transaction> getTransactionsByReference(Long reference) {
		List<Transaction> duplicatesList = null;
		duplicatesList = transactionRepository.findByReference(reference);
		duplicatesList.addAll(transactionRepository.findByReference(reference));
		return duplicatesList;
	}

}
