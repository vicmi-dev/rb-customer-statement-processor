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

import com.manuel.rb.models.entity.Records;
import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.response.ResponseMessage;

@Service
public class FileService {
	@Autowired
	ValidationService validationService;

	@Autowired
	ResponseService responseService;

	// File processing
	public ResponseEntity<ResponseMessage> fileProcessing(MultipartFile[] files) throws IOException, IllegalStateException, JAXBException {
		String message = "";
		Map<String, Object> response = new HashMap<>();
		List<List<Transaction>> allTransactions = null;
		List<Transaction> rawTransactions = null;
		for (MultipartFile file : files) {
			String fileContentType = file.getContentType();
			if (fileContentType != null) {
				// If file is CSV:
				if (fileContentType.equals("text/csv")) {
					// Get list of all transactions:
					rawTransactions = csvToTransactions(file.getInputStream());
					// If file is XML:
				} else if (fileContentType.equals("application/xml")) {
					// Getting transactions from xml file
					rawTransactions = xmlToTransactions(file);
				} else {
					message = "Please upload a csv/xml file!";
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
				}
				allTransactions = validationService.transactionsValidation(rawTransactions);
				// Get the response
				response = responseService.getResponse(allTransactions, file, response);
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
		String filePath = System.getProperty("java.io.tmpdir") + file.getName();

		// Saving the file
		file.transferTo(new File(filePath));

		// Getting all the records from the XML file
		JAXBContext context = JAXBContext.newInstance(Records.class);
		Records records = (Records) context.createUnmarshaller().unmarshal(new FileReader(filePath));

		// Validating and getting the list of transactions needed
		return records.getTransactions();
	}

}
