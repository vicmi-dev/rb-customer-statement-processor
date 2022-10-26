package com.manuel.rb.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Document;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.models.entity.XmlTransactions;
import com.manuel.rb.repository.TransactionRepository;
import com.manuel.rb.helper.CSVHelper;
import com.manuel.rb.models.entity.Records;
import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.response.ResponseMessage;
import com.manuel.rb.service.CSVService;

//@CrossOrigin("http://localhost:8081")
@Controller
@RequestMapping("/api/csv")
public class CSVController {

	@Autowired
	CSVService fileService;

    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    TransactionRepository repository;
    
	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("files") MultipartFile[] files) throws IOException, JAXBException, ParserConfigurationException, SAXException {
		String message = "";
		Map<String, Object> response = new HashMap<>();
		for (MultipartFile file : files) {
			if (CSVHelper.hasCSVFormat(file)) {
				try {
					// Saving transactions on db to compare future transactions
					fileService.save(file);
					List<List<Transaction>> transactions = CSVHelper.csvToTransactions(file.getInputStream());

					message = "Uploaded the file successfully: " + file.getOriginalFilename();
					response.put("message"+ file.getOriginalFilename(), "Uploaded the file successfully: " + file.getOriginalFilename());
					response.put("duplicated_transactions"+ file.getOriginalFilename(), transactions.get(1));

				} catch (Exception e) {
					message = "Could not upload the file: " + file.getOriginalFilename() + "!" + e;
					return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
				}
			} 
			else {
//		        InputStream is = file.getInputStream();
//		        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();                 
//		        Document doc = dBuilder.parse(is);
		        
				// Getting the path to save the file
				String filePath = request.getServletContext().getRealPath("/"); 
				//Saving the file
				file.transferTo(new File(filePath));
				
				Records records = null;
				//Getting all the records
			    JAXBContext context = JAXBContext.newInstance(Records.class);
			    records = (Records) context.createUnmarshaller()
			      .unmarshal(new FileReader(filePath));
			    
			    //Getting a list of transactions from the records
				List<Transaction> transactions = records.getTransactions();
				
				//PENDING: Iterate over the transactions to find duplicates - Better if done in combination with the others
				// Duplicates in current batch
				
				// Duplicates against the database
				//Saving the list of transactions to the database
				repository.saveAll(transactions);
				message = "Uploaded the file successfully: " + file.getOriginalFilename();
				response.put("message"+ file.getOriginalFilename(), "Uploaded the file successfully: " + file.getOriginalFilename());
				response.put("duplicated_transactions"+ file.getOriginalFilename(), transactions.get(1));
			}
		}
		try {
			ResponseEntity responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			return responseEntity;
		} catch (Exception e) {
			message = "Please upload a csv file!";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
		}

	}

	@GetMapping("/transactions")
	public ResponseEntity<List<Transaction>> getAllTransactions() {
		try {
			List<Transaction> transactions = fileService.getAllTransactions();

			if (transactions.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(transactions, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}