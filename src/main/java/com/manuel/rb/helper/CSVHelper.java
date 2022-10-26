package com.manuel.rb.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.models.entity.Transaction;

public class CSVHelper {
	public static String TYPE = "text/csv";

	public static boolean hasCSVFormat(MultipartFile file) {

		if (!TYPE.equals(file.getContentType())) {
			return false;
		}

		return true;
	}
	
	//CSV to List of List of transactions
	public static List<List<Transaction>> csvToTransactions(InputStream csvFile) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(csvFile, "ISO-8859-1"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<Transaction> transactions = new ArrayList<Transaction>();
			List<Transaction> duplicatedTransactions = new ArrayList<Transaction>();

			Iterable<CSVRecord> csvTransactions = csvParser.getRecords();

			//Iterate over the csvTransactions
			for (CSVRecord csvTransaction : csvTransactions) {
				//Create the transaction object
				Transaction transaction = new Transaction(
						Long.parseLong(csvTransaction.get("Reference")),
						csvTransaction.get("Account Number"), 
						Double.valueOf(csvTransaction.get("Start Balance")),
						Double.valueOf(csvTransaction.get("Mutation")), 
						csvTransaction.get("Description"),
						Double.valueOf(csvTransaction.get("End Balance")));
				
				//Check if transaction reference is duplicated against current List of transactions
				for (Transaction transactionItem : transactions) {
					if(transactionItem.equals(transaction)) {
						//Checking whether the item in the current List of transactions is in duplicatedTransactions
						if(!duplicatedTransactions.contains(transactionItem)) {
							duplicatedTransactions.add(transactionItem);
						}
						//Adding current transactions to duplicated List
						duplicatedTransactions.add(transaction);
						break;
					}
				}
				//Adding current transaction to transactions List
				transactions.add(transaction);
			}
			List<List<Transaction>> allTransactions = new ArrayList<List<Transaction>>();
			allTransactions.add(transactions);
			allTransactions.add(duplicatedTransactions);
			
			// Duplicates against the database
			
			return allTransactions;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		}
	}
	
}