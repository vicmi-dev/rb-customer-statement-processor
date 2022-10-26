package com.manuel.rb.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.manuel.rb.models.entity.Transaction;

@Repository
public class TransactionDaoImpl implements ITransactionDao {
	
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Transaction> findAll() {
		return em.createQuery("from Transaction").getResultList();
	}

}
