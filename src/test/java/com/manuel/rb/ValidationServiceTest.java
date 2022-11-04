package com.manuel.rb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.services.ValidationService;

@SpringBootTest
class ValidationServiceTest {

	@Autowired
	ValidationService validationService;

	Transaction transaction = new Transaction();
	Transaction transaction2 = new Transaction();
	Transaction transaction3 = new Transaction();
	List<Transaction> transactions = new ArrayList<Transaction>();

	@BeforeEach
	public void init() {
		transaction.setReference(Long.parseLong("4898945"));
		transaction.setAccountNumber("NL87RABO5564587");
		transaction.setDescription("Description Sample");
		transaction.setStartBalance(new BigDecimal("300"));
		transaction.setMutation(new BigDecimal("-100"));
		transaction.setEndBalance(new BigDecimal("400"));

		transaction2.setReference(Long.parseLong("4898946"));
		transaction2.setAccountNumber("NL87RABO5564587");
		transaction2.setDescription("Description Sample");
		transaction2.setStartBalance(new BigDecimal("500"));
		transaction2.setMutation(new BigDecimal("-100"));
		transaction2.setEndBalance(new BigDecimal("400"));

		transaction3.setReference(Long.parseLong("4898945"));
		transaction3.setAccountNumber("NL87RABO5564587");
		transaction3.setDescription("Description Sample");
		transaction3.setStartBalance(new BigDecimal("500"));
		transaction3.setMutation(new BigDecimal("-100"));
		transaction3.setEndBalance(new BigDecimal("400"));

		transactions.add(transaction);
		transactions.add(transaction2);
		transactions.add(transaction3);
	}

	@Test
	void findDuplicatedTransactionsInListTest() {
		List<Transaction> duplicatedTransactions = validationService.findDuplicatedTransactionsInList(transactions);

		assertEquals(2, duplicatedTransactions.size());
	}

	@Test
	void validateBalanceTest() {
		List<Transaction> transactionsBalanceError = validationService.validateBalance(transactions);

		transactionsBalanceError.forEach(transaction -> assertNotEquals(transaction.getEndBalance(),
				transaction.getStartBalance().add(transaction.getMutation())));
		assertEquals(1, transactionsBalanceError.size());

	}

}
