package com.manuel.rb.models.entity;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "transactions")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	@JsonIgnore
	private long id;
	
	@XmlAttribute(name = "reference")
	private Long reference;
	@JsonIgnore
	private String accountNumber;
	@JsonIgnore
	private BigDecimal startBalance;
	@JsonIgnore
	private BigDecimal mutation;
	private String description;
	@JsonIgnore
	private BigDecimal endBalance;


	
	public Transaction(Long reference, String accountNumber, BigDecimal startBalance, BigDecimal mutation, String description,
			BigDecimal endBalance) {
		this.reference = reference;
		this.accountNumber = accountNumber;
		this.startBalance = startBalance;
		this.mutation = mutation;
		this.description = description;
		this.endBalance = endBalance;
	}

	
	public Transaction() {
	}
	
	public Long getReference() {
		return reference;
	}

	public void setReference(Long reference) {
		this.reference = reference;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getStartBalance() {
		return startBalance;
	}

	public void setStartBalance(BigDecimal startBalance) {
		this.startBalance = startBalance;
	}

	public BigDecimal getMutation() {
		return mutation;
	}

	public void setMutation(BigDecimal mutation) {
		this.mutation = mutation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getEndBalance() {
		return endBalance;
	}

	public void setEndBalance(BigDecimal endBalance) {
		this.endBalance = endBalance;
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Transaction that = (Transaction) obj;
		return Objects.equals(reference, that.reference);
	}

	@Override
	public int hashCode() {
		return reference != null ? reference.hashCode() : 0;
	}
}
