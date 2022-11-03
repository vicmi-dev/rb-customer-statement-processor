package com.manuel.rb.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.models.entity.Transaction;

@Service
public class ResponseService {
	
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

}
