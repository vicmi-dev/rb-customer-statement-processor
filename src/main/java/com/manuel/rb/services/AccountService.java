package com.manuel.rb.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manuel.rb.models.entity.Account;
import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.repository.AccountRepository;

@Service
public class AccountService {
	@Autowired
	AccountRepository accountRepository;

	public void createNewAccounts(List<Transaction> rawTransactions) {
		// Creating Account based on IBAN if it does not exist already
		rawTransactions.stream().filter(t -> accountRepository.findByIban(t.getAccountNumber()) == null)
				.forEach(transaction -> {
					Account newAccount = new Account(transaction.getAccountNumber(), transaction.getEndBalance());
					accountRepository.save(newAccount);
				});
	}

}
