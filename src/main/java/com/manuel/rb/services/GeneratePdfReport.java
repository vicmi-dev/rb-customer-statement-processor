package com.manuel.rb.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.manuel.rb.models.entity.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class GeneratePdfReport {

	private static final Logger logger = LoggerFactory.getLogger(GeneratePdfReport.class);

	public static ByteArrayInputStream transactionsReport(List<List<Transaction>> allTransactions) {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int transactionTypeCount = 0;
		
		try {

			for (List<Transaction> transactions : allTransactions) {
				if (!transactions.isEmpty()) {
					
					PdfPTable table = new PdfPTable(6);
//                table.setWidthPercentage(60);
//                table.setWidths(new int[]{1, 3, 3});

					Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

					PdfPCell hcell;
					hcell = new PdfPCell(new Paragraph("Reference" + transactionTypeCount, headFont));
					hcell.setColspan(6);
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Reference", headFont));
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Account Number", headFont));
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Start Balance", headFont));
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Mutation", headFont));
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(hcell);

					hcell = new PdfPCell(new Phrase("Description", headFont));
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(hcell);

					hcell = new PdfPCell(new Phrase("End Balance", headFont));
					hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(hcell);

					for (Transaction transaction : transactions) {

						PdfPCell cell;

						cell = new PdfPCell(new Phrase(String.valueOf(transaction.getReference())));
//                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						cell = new PdfPCell(new Phrase(transaction.getAccountNumber()));
//                    cell.setPaddingLeft(5);
//                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(transaction.getStartBalance())));
//                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(transaction.getMutation())));
//                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						cell = new PdfPCell(new Phrase(transaction.getDescription()));
//                    cell.setPaddingLeft(5);
//                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table.addCell(cell);

						cell = new PdfPCell(new Phrase(String.valueOf(transaction.getEndBalance())));
//                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table.addCell(cell);
					}

					PdfWriter.getInstance(document, out);
					document.open();
					document.add(table);

				}
				transactionTypeCount++;
			}
			document.close();

		} catch (DocumentException ex) {

			logger.error("Error occurred: {0}", ex);
		}

		return new ByteArrayInputStream(out.toByteArray());
	}
}
