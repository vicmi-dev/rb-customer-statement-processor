package com.manuel.rb.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.repository.TransactionRepository;

@Service
public class ValidationService {
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	AccountService accountService;
	
	public List<Transaction> validateBalance(List<Transaction> rawTransactions) {
		List<Transaction> transactionsBalanceError = new ArrayList<>();

		// Check balance of the transaction and if there are errors, adding them to a
		// List
		rawTransactions.stream()
				.filter(transaction -> !transaction.getStartBalance().add(transaction.getMutation())
						.equals(transaction.getEndBalance()))
				.forEach(transaction -> transactionsBalanceError.add(transaction));

		return transactionsBalanceError;
	}

	public List<Transaction> findDuplicatedTransactionsInList(List<Transaction> rawTransactions) {
		List<Transaction> duplicatedTransactions = new ArrayList<>();
		List<Transaction> transactions = new ArrayList<>();
		// Iterate over the transactions to find duplicates in current batch
		for (Transaction transaction : rawTransactions) {
			for (Transaction transactionItem : transactions) {
				if (transactionItem.equals(transaction)) {
					// Checking whether the item in the current List of transactions is in
					// duplicatedTransactions
					if (!duplicatedTransactions.contains(transactionItem)) {
						duplicatedTransactions.add(transactionItem);
					}
					// Adding current transactions to duplicated List
					duplicatedTransactions.add(transaction);
					break;
				}
			}
			// Adding current transaction to transactions List
			transactions.add(transaction);
		}

		return duplicatedTransactions;
	}

	public List<Transaction> findDuplicatedTransactionsInDB(List<Transaction> rawTransactions) {
		List<Transaction> duplicatesAgainstDb = new ArrayList<>();
		// Iterate over the transactions from the file and check duplicates against the
		// database

		rawTransactions.forEach(rawTransaction -> {
			List<Transaction> duplicatedTransactionsInDb = transactionService.getTransactionsByReference(rawTransaction.getReference());

			// If the reference number is duplicated, we add the transaction to the list of
			// duplicates
			if (!duplicatedTransactionsInDb.isEmpty()) {
				duplicatesAgainstDb.add(rawTransaction);
				duplicatedTransactionsInDb.stream().filter(t -> !duplicatesAgainstDb.contains(t))
						.forEach(t -> duplicatesAgainstDb.add(t));
			}
		});

		return duplicatesAgainstDb;
	}

	// Validation of transactions
	// Checking unique reference and balance
	// Saving to the DB
	public List<List<Transaction>> transactionsValidation(List<Transaction> rawTransactions) {
		List<Transaction> duplicatedTransactionsInFile = findDuplicatedTransactionsInList(rawTransactions);
		List<Transaction> transactionsBalanceError = validateBalance(rawTransactions);
		List<Transaction> duplicatedTransactionsInDb = findDuplicatedTransactionsInDB(rawTransactions);

		List<List<Transaction>> allTransactions = new ArrayList<>();
		// Adding the List duplicatedTransactions and the transactions with balance
		// error to the List of allTransactions that is returned
		allTransactions.add(duplicatedTransactionsInFile);
		allTransactions.add(duplicatedTransactionsInDb);
		allTransactions.add(transactionsBalanceError);
		allTransactions.add(rawTransactions);

		//If necessary create new accounts
		accountService.createNewAccounts(rawTransactions);

		// Saving the list of transactions from the file to the database
		transactionRepository.saveAll(rawTransactions);

		return allTransactions;
	}

	
}
