package com.manuel.rb.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.helper.CSVHelper;
import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.repository.TransactionRepository;

@Service
public class CSVService {
  @Autowired
  TransactionRepository repository;

  public void save(MultipartFile file) {
    try {
    	List<List<Transaction>> transactions = CSVHelper.csvToTransactions(file.getInputStream());
      //check
    	
      repository.saveAll(transactions.get(0));
    } catch (IOException e) {
      throw new RuntimeException("fail to store csv data: " + e.getMessage());
    }
  }

  public List<Transaction> getAllTransactions() {
    return repository.findAll();
  }
}