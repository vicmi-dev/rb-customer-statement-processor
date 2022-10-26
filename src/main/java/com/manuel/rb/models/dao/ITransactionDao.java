package com.manuel.rb.models.dao;

import java.util.List;

import com.manuel.rb.models.entity.Transaction;

public interface ITransactionDao {

	public List<Transaction> findAll();
}
