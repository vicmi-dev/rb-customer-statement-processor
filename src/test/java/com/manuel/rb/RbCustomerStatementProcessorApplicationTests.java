package com.manuel.rb;

import static org.junit.Assert.assertFalse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.services.FilesServices;


@SpringBootTest
class RbCustomerStatementProcessorApplicationTests {

	@Autowired
	FilesServices fileService;
	
	@Test
	void contextLoads() throws FileNotFoundException {
		String path = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\records.csv";
		FileInputStream inputFile = new FileInputStream(path);
		
		List<Transaction> rawTransactions = fileService.csvToTransactions(inputFile);
		
		assertFalse(rawTransactions.isEmpty());	}

}
