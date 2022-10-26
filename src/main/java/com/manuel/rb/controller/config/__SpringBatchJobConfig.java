package com.manuel.rb.controller.config;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.manuel.rb.models.entity.Transaction;
 
@Configuration
public class __SpringBatchJobConfig {
 
	  /** The Constant HEADERS. */
	  public static final String[] HEADERS = {"reference", "accountNumber", "description", "startBalance",
				"mutation", "endBalance" };

    @Bean
    public ItemReader<Transaction> itemReader() {
        Jaxb2Marshaller transactionMarshaller = new Jaxb2Marshaller();
        transactionMarshaller.setClassesToBeBound(Transaction.class);
 
        return new StaxEventItemReaderBuilder<Transaction>()
                .name("transactionReader")
                .resource(new ClassPathResource("data/students.xml"))
                .addFragmentRootElements("transaction")
                .unmarshaller(transactionMarshaller)
                .build();
    }
    
    private static final String RABO_BANK_CUSTOMER_VALIDATION = "RaboBankCustomerValidation";

    public static ByteArrayInputStream generate(List<Transaction> transactions) throws IOException {
      try (
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();) {

        Sheet sheet = workbook.createSheet(RABO_BANK_CUSTOMER_VALIDATION);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLUE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Row for Header
        Row headerRow = sheet.createRow(0);

        // Header
        for (int col = 0; col < HEADERS.length; col++) {
          Cell cell = headerRow.createCell(col);
          cell.setCellValue(HEADERS[col].toUpperCase());
          cell.setCellStyle(headerCellStyle);
        }

        AtomicInteger rowIdx = new AtomicInteger(1);
        transactions.stream().forEach(transaction -> {
          Row row = sheet.createRow(rowIdx.getAndIncrement());
          row.createCell(0).setCellValue(transaction.getReferenceNumb());
          row.createCell(1).setCellValue(transaction.getAccountNumber());
          row.createCell(2).setCellValue(transaction.getDescription());
          row.createCell(3).setCellValue(transaction.getStartBalance());
          row.createCell(4).setCellValue(transaction.getMutation());
          row.createCell(5).setCellValue(transaction.getEndBalance());
        });

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
      }
    }
}