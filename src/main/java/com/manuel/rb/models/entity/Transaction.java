package com.manuel.rb.models.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "transactions")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private long id;
	
	@XmlAttribute(name = "reference")
	private Long referenceNumb;
	private String accountNumber;
	private double startBalance;
	private double mutation;
	private String description;
	private double endBalance;

	
	public Long getReferenceNumb() {
		return referenceNumb;
	}

	public void setReferenceNumb(Long referenceNumb) {
		this.referenceNumb = referenceNumb;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getStartBalance() {
		return startBalance;
	}

	public void setStartBalance(double startBalance) {
		this.startBalance = startBalance;
	}

	public double getMutation() {
		return mutation;
	}

	public void setMutation(double mutation) {
		this.mutation = mutation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getEndBalance() {
		return endBalance;
	}

	public void setEndBalance(double endBalance) {
		this.endBalance = endBalance;
	}

	
	public Transaction(Long referenceNumb, String accountNumber, double startBalance, double mutation, String description,
			double endBalance) {
		this.referenceNumb = referenceNumb;
		this.accountNumber = accountNumber;
		this.startBalance = startBalance;
		this.mutation = mutation;
		this.description = description;
		this.endBalance = endBalance;
	}

	
	public Transaction() {
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Transaction that = (Transaction) obj;
		return Objects.equals(referenceNumb, that.referenceNumb);
	}

	@Override
	public int hashCode() {
		return referenceNumb != null ? referenceNumb.hashCode() : 0;
	}
}
