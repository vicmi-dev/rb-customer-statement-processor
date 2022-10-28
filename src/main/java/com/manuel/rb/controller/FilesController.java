package com.manuel.rb.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.repository.AccountRepository;
import com.manuel.rb.repository.TransactionRepository;
import com.manuel.rb.models.entity.Account;
import com.manuel.rb.models.entity.Records;
import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.response.ResponseMessage;

@Controller
@RequestMapping("/api")
public class FilesController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	TransactionRepository repository;

	@Autowired
	AccountRepository accountRepository;

	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("files") MultipartFile[] files)
			throws IOException, JAXBException {
		String message = "";
		Map<String, Object> response = new HashMap<>();
		List<List<Transaction>> allTransactions = new ArrayList<List<Transaction>>();
		for (MultipartFile file : files) {
			// If file is CSV:
			if (file.getContentType().equals("text/csv")) {
				try {
					// Get list of all transactions:
					allTransactions = csvToTransactions(file.getInputStream());
					
					// Get the response
					response = getResponse(allTransactions, file, response);
					
					// PDF generation WIP
//			        ByteArrayInputStream bis = GeneratePdfReport.transactionsReport(allTransactions);
//
//			        var headers = new HttpHeaders();
//			        headers.add("Content-Disposition", "inline; filename=transactionsreport.pdf");
//
//			        return ResponseEntity
//			                .ok()
//			                .headers(headers)
//			                .contentType(MediaType.APPLICATION_PDF)
//			                .body(new InputStreamResource(bis));
				} catch (Exception e) {
					message = "Could not upload the file: " + file.getOriginalFilename() + "!" + e;
					return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
				}
				// If file is XML:
			} else if (file.getContentType().equals("application/xml")) {
				try {
					// Getting the path to save the file
					String filePath = request.getServletContext().getRealPath("/");

					// Saving the file
					file.transferTo(new File(filePath));

					// Getting all the records from the XML file
					JAXBContext context = JAXBContext.newInstance(Records.class);
					Records records = (Records) context.createUnmarshaller().unmarshal(new FileReader(filePath));

					// Getting a list of transactions from the records
					List<Transaction> rawTransactions = records.getTransactions();

					// Validating and getting the list of transactions needed
					allTransactions = transactionsValidation(rawTransactions);

					// Get the response
					response = getResponse(allTransactions, file, response);
				} catch (Exception e) {
					message = "Could not upload the file: " + file.getOriginalFilename() + "!" + e;
					return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
				}
			} else {
				message = "Please upload a csv/xml file!";
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
			}
		}

		try {
			ResponseEntity responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			return responseEntity;
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
		}

	}

	// Build response based on List of transactions
	public Map<String, Object> getResponse(List<List<Transaction>> allTransactions, MultipartFile file, Map<String, Object> response) {
		//If file does not have the correct format
		if (allTransactions.get(3).isEmpty()) {
			response.put(
					"Uploaded file does not have the correct fomat. ", file.getOriginalFilename());
			return response;
		} 
		//Returning the duplicated transactions against the current file
		if (!allTransactions.get(0).isEmpty()) {
			response.put(
					"Following transactions in file "  + file.getOriginalFilename() +" are duplicated: ", allTransactions.get(0));
		}
		
		//Returning the duplicated transactions against the DB
		if (!allTransactions.get(1).isEmpty()) {
			response.put(
					"Following transactions from file "  + file.getOriginalFilename() +" have duplicates in DB: ", allTransactions.get(1));
		}

		//Returning the duplicated transactions against the current file
		if (!allTransactions.get(2).isEmpty()) {
			response.put(
					"Following transactions from file "  + file.getOriginalFilename() +" have balance error: ", allTransactions.get(2));
		}
		return response;
	}

	// CSV to List of List of transactions
	public List<List<Transaction>> csvToTransactions(InputStream csvFile) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(csvFile, "ISO-8859-1"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<Transaction> transactions = new ArrayList<Transaction>();

			Iterable<CSVRecord> csvTransactions = csvParser.getRecords();

			// Iterate over the csvTransactions
			for (CSVRecord csvTransaction : csvTransactions) {
				// Create the transaction object
				Transaction transaction = new Transaction(Long.parseLong(csvTransaction.get("Reference")),
						csvTransaction.get("Account Number"), new BigDecimal(csvTransaction.get("Start Balance")),
						new BigDecimal(csvTransaction.get("Mutation")), csvTransaction.get("Description"),
						new BigDecimal(csvTransaction.get("End Balance")));

				// Adding current transaction to transactions List
				transactions.add(transaction);
			}
			List<List<Transaction>> allTransactions = transactionsValidation(transactions);

			return allTransactions;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		}
	}

	
	// Validation of transactions
	// Checking unique reference and balance
	// Saving to the DB
	public List<List<Transaction>> transactionsValidation(List<Transaction> rawTransactions){
		List<List<Transaction>> allTransactions = new ArrayList<List<Transaction>>();

		// Iterate over the transactions to find duplicates in current batch
		List<Transaction> transactions = new ArrayList<Transaction>();
		List<Transaction> duplicatedTransactions = new ArrayList<Transaction>();
		List<Transaction> transactionsBalanceError = new ArrayList<Transaction>();

		// Iterate over the rawTransactions
		for (Transaction transaction : rawTransactions) {
			// Check balance of the transaction and if there are errors, adding them to a
			// List
			if (!transaction.getStartBalance().add(transaction.getMutation()).equals(transaction.getEndBalance())) {
				transactionsBalanceError.add(transaction);
			}
			// Check if transaction reference is duplicated against current List of
			// transactions
			for (Transaction transactionItem : transactions) {
				if (transactionItem.equals(transaction)) {
					// Checking whether the item in the current List of transactions is in
					// duplicatedTransactions
					if (!duplicatedTransactions.contains(transactionItem)) {
						duplicatedTransactions.add(transactionItem);
					}
					// Adding current transactions to duplicated List
					duplicatedTransactions.add(transaction);
					break;
				}
			}
			// Adding current transaction to transactions List
			transactions.add(transaction);
		}
		
		// Iterate over the transactions from the file and check duplicates against the
		// database
		List<Transaction> duplicatesListInDb = new ArrayList<Transaction>();
		for (Transaction transaction : transactions) {
			// Get the transactions from the database with same reference number as the
			// transaction from the file
			List<Transaction> duplicatedTransactionsInDb = getTransactionsByReference(transaction.getReference());
			// If the reference number is duplicated, we add the transaction to the list of
			// duplicates
			if (!duplicatedTransactionsInDb.isEmpty()) {
				duplicatesListInDb.add(transaction);
				for (Transaction duplicatedTransaction : duplicatedTransactionsInDb) {
					// If the list of duplicates does not include the transactions from the DB with
					// duplicate reference number, we add them to the list of duplicates
					if (!duplicatesListInDb.contains(duplicatedTransaction)) {
						duplicatesListInDb.add(duplicatedTransaction);
					}
				}
			}
		}
		
		// Adding the List duplicatedTransactions and the transactions with balance
		// error to the List of allTransactions that is returned
		allTransactions.add(duplicatedTransactions);
		allTransactions.add(duplicatesListInDb);
		allTransactions.add(transactionsBalanceError);
		allTransactions.add(rawTransactions);

		//Creating Account based on IBAN if it does not exist already
		for (Transaction transaction : rawTransactions) {
			if (accountRepository.findByIban(transaction.getAccountNumber()) == null) {
				Account newAccount = new Account(transaction.getAccountNumber(), transaction.getEndBalance());
				accountRepository.save(newAccount);
			}
		}

		
		// Saving the list of transactions from the file to the database
		repository.saveAll(rawTransactions);
		
		return allTransactions;
	}

	// Get repeated transactions from the database
	public List<Transaction> getTransactionsByReference(Long reference) {
		List<Transaction> duplicatesList = new ArrayList<Transaction>();
		duplicatesList = repository.findByReference(reference);
		duplicatesList.addAll(repository.findByReference(reference));
		return duplicatesList;
	}
}