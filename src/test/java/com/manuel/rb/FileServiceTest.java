package com.manuel.rb;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.manuel.rb.models.entity.Transaction;
import com.manuel.rb.services.FileService;

@SpringBootTest
class FileServiceTest {

	@Autowired
	FileService fileService;

	@Test
	void csvToTransactionsTest() throws FileNotFoundException {
		File file = new File("src/test/resources/records.csv");
		FileInputStream inputFile = new FileInputStream(file);

		List<Transaction> rawTransactions = fileService.csvToTransactions(inputFile);

		assertFalse(rawTransactions.isEmpty());
		assertEquals(10, rawTransactions.size());
	}

	@Test
	void xmlToTransactionsTest() throws IOException, IllegalStateException, JAXBException {
		File file = new File("src/test/resources/records.xml");
		FileInputStream input = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("files", file.getName(), "text/csv",
				IOUtils.toByteArray(input));

		List<Transaction> rawTransactions = fileService.xmlToTransactions(multipartFile);

		assertFalse(rawTransactions.isEmpty());
		assertEquals(10, rawTransactions.size());
	}

}
