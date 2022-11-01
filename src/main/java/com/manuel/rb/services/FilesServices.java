package com.manuel.rb.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.models.entity.Account;
import com.manuel.rb.models.entity.Records;
import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.repository.AccountRepository;
import com.manuel.rb.repository.TransactionRepository;
import com.manuel.rb.response.ResponseMessage;

@Service
public class FilesServices {
	@Autowired
	AccountRepository accountRepository;

	@Autowired
	TransactionRepository repository;

	@Autowired
	private HttpServletRequest request;

	// File processing
	public ResponseEntity<ResponseMessage> fileProcessing(MultipartFile[] files) {
		String message = "";
		Map<String, Object> response = new HashMap<>();
		List<List<Transaction>> allTransactions = null;
		List<Transaction> rawTransactions = null;
		for (MultipartFile file : files) {
			String fileContentType = file.getContentType();
			if (fileContentType != null) {
				// If file is CSV:
				if (fileContentType.equals("text/csv")) {
					try {
						// Get list of all transactions:
						rawTransactions = csvToTransactions(file.getInputStream());
					} catch (Exception e) {
						message = "Could not upload the file: " + file.getOriginalFilename() + "!" + e;
						return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
					}
					// If file is XML:
				} else if (fileContentType.equals("application/xml")) {
					try {
						// Getting transactions from xml file
						rawTransactions = xmlToTransactions(file);
					} catch (Exception e) {
						message = "Could not upload the file: " + file.getOriginalFilename() + "!" + e;
						return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
					}
				} else {
					message = "Please upload a csv/xml file!";
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
				}
				allTransactions = transactionsValidation(rawTransactions);

				// Get the response
				response = getResponse(allTransactions, file, response);
			}
		}
		try {
			ResponseEntity responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			return responseEntity;
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
		}
	}

	// CSV to List of List of transactions
	public List<Transaction> csvToTransactions(InputStream csvFile) {
		try (BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(csvFile, StandardCharsets.ISO_8859_1));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<Transaction> transactions = new ArrayList<Transaction>();

			Iterable<CSVRecord> csvTransactions = csvParser.getRecords();

			// Iterate over the csvTransactions
			csvTransactions.forEach(csvTransaction -> {
				// Create the transaction object
				Transaction transaction = new Transaction(Long.parseLong(csvTransaction.get("Reference")),
						csvTransaction.get("Account Number"), new BigDecimal(csvTransaction.get("Start Balance")),
						new BigDecimal(csvTransaction.get("Mutation")), csvTransaction.get("Description"),
						new BigDecimal(csvTransaction.get("End Balance")));

				// Adding current transaction to transactions List
				transactions.add(transaction);
			});

			return transactions;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		}
	}

	// XML to List of List of transactions
	public List<Transaction> xmlToTransactions(MultipartFile file)
			throws IllegalStateException, IOException, JAXBException {
		// Getting the path to save the file
		String filePath = System.getProperty("java.io.tmpdir")+file.getName();

		// Saving the file
		file.transferTo(new File(filePath));

		// Getting all the records from the XML file
		JAXBContext context = JAXBContext.newInstance(Records.class);
		Records records = (Records) context.createUnmarshaller().unmarshal(new FileReader(filePath));

		// Getting a list of transactions from the records
		List<Transaction> rawTransactions = records.getTransactions();

		// Validating and getting the list of transactions needed
		return rawTransactions;
	}

	public List<Transaction> validateBalance(List<Transaction> rawTransactions) {
		List<Transaction> transactionsBalanceError = new ArrayList<>();

		// Check balance of the transaction and if there are errors, adding them to a
		// List
		rawTransactions.stream()
				.filter(transaction -> !transaction.getStartBalance().add(transaction.getMutation())
						.equals(transaction.getEndBalance()))
				.forEach(transaction -> transactionsBalanceError.add(transaction));

		return transactionsBalanceError;
	}

	public List<Transaction> findDuplicatedTransactionsInList(List<Transaction> rawTransactions) {
		List<Transaction> duplicatedTransactions = new ArrayList<>();
		List<Transaction> transactions = new ArrayList<>();
		// Iterate over the transactions to find duplicates in current batch
		for (Transaction transaction : rawTransactions) {
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

		return duplicatedTransactions;
	}

	public List<Transaction> findDuplicatedTransactionsInDB(List<Transaction> rawTransactions) {
		List<Transaction> duplicatesAgainstDb = new ArrayList<>();
		// Iterate over the transactions from the file and check duplicates against the
		// database

		rawTransactions.forEach(rawTransaction -> {
			List<Transaction> duplicatedTransactionsInDb = getTransactionsByReference(rawTransaction.getReference());

			// If the reference number is duplicated, we add the transaction to the list of
			// duplicates
			if (!duplicatedTransactionsInDb.isEmpty()) {
				duplicatesAgainstDb.add(rawTransaction);
				duplicatedTransactionsInDb.stream().filter(t -> !duplicatesAgainstDb.contains(t))
						.forEach(t -> duplicatesAgainstDb.add(t));
			}
		});

		return duplicatesAgainstDb;
	}

	// Validation of transactions
	// Checking unique reference and balance
	// Saving to the DB
	public List<List<Transaction>> transactionsValidation(List<Transaction> rawTransactions) {
		List<Transaction> duplicatedTransactionsInFile = findDuplicatedTransactionsInList(rawTransactions);
		List<Transaction> transactionsBalanceError = validateBalance(rawTransactions);
		List<Transaction> duplicatedTransactionsInDb = findDuplicatedTransactionsInDB(rawTransactions);

		List<List<Transaction>> allTransactions = new ArrayList<>();
		// Adding the List duplicatedTransactions and the transactions with balance
		// error to the List of allTransactions that is returned
		allTransactions.add(duplicatedTransactionsInFile);
		allTransactions.add(duplicatedTransactionsInDb);
		allTransactions.add(transactionsBalanceError);
		allTransactions.add(rawTransactions);

		//If necessary create new accounts
		createNewAccounts(rawTransactions);

		// Saving the list of transactions from the file to the database
		repository.saveAll(rawTransactions);

		return allTransactions;
	}

	public void createNewAccounts(List<Transaction> rawTransactions) {
		// Creating Account based on IBAN if it does not exist already
		rawTransactions.stream().filter(t -> accountRepository.findByIban(t.getAccountNumber()) == null)
				.forEach(transaction -> {
					Account newAccount = new Account(transaction.getAccountNumber(), transaction.getEndBalance());
					accountRepository.save(newAccount);
				});
	}

	// Get repeated transactions from the database
	public List<Transaction> getTransactionsByReference(Long reference) {
		List<Transaction> duplicatesList = null;
		duplicatesList = repository.findByReference(reference);
		duplicatesList.addAll(repository.findByReference(reference));
		return duplicatesList;
	}

	// Build response based on List of transactions
	public Map<String, Object> getResponse(List<List<Transaction>> allTransactions, MultipartFile file,
			Map<String, Object> response) {
		// If file does not have the correct format
		if (allTransactions.get(3).isEmpty()) {
			response.put("Uploaded file does not have the correct fomat. ", file.getOriginalFilename());
			return response;
		}
		// Returning the duplicated transactions against the current file
		if (!allTransactions.get(0).isEmpty()) {
			response.put("Following transactions in file " + file.getOriginalFilename() + " are duplicated: ",
					allTransactions.get(0));
		}

		// Returning the duplicated transactions against the DB
		if (!allTransactions.get(1).isEmpty()) {
			response.put("Following transactions from file " + file.getOriginalFilename() + " have duplicates in DB: ",
					allTransactions.get(1));
		}

		// Returning the duplicated transactions against the current file
		if (!allTransactions.get(2).isEmpty()) {
			response.put("Following transactions from file " + file.getOriginalFilename() + " have balance error: ",
					allTransactions.get(2));
		}
		return response;
	}

	public void generatePdfResponse() {
		// PDF generation WIP
//		ByteArrayInputStream bis = GeneratePdfReport.transactionsReport(allTransactions);
//
//		var headers = new HttpHeaders();
//		headers.add("Content-Disposition", "inline; filename=transactionsreport.pdf");
//
//		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
//				.body(new InputStreamResource(bis));
	}
}
