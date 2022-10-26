package com.manuel.rb.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
	@Id
	@Column(unique = true)
	private String iban;
	
	private double balance;

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Account(String iban, double balance) {
		this.iban = iban;
		this.balance = balance;
	}

	public Account() {
	}
	


}
