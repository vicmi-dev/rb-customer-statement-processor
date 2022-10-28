package com.manuel.rb.models.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
	@Id
	@Column(unique = true)
	private String iban;
	
	private BigDecimal balance;
	
    @OneToMany(cascade = CascadeType.ALL)  
    @JoinColumn(name="accountNumber")  
	private List<Transaction> transactions;

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Account(String iban, BigDecimal startBalance) {
		this.iban = iban;
		this.balance = startBalance;
	}

	public Account() {
	}
	


}
