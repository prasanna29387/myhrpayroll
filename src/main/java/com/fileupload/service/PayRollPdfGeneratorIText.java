package com.fileupload.service;

import com.config.Config;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.model.EmployeePayRoll;
import com.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xeccwrj on 9/18/2017.
 */
@Slf4j
@Service
public class PayRollPdfGeneratorIText {


	protected static final String FAX_NAS_BACKUP_FOLDER_KEY = "fax.nas.backup.folder";
	protected static final String PDF_EXTENSION = ".pdf";

	public void createPayRollPDf(List<EmployeePayRoll> employeePayRollList,String fileName)
	{
		List<List<String>> result = new ArrayList<>();
		populateInformation(result,employeePayRollList);
		createPdfFile(result,"_PaySlip", FileHelper.getBaseNameFromFileName(fileName));
	}

	private void populateInformation(List<List<String>> result, List<EmployeePayRoll> employeePayRollList) {

		for(EmployeePayRoll employeePayRoll : employeePayRollList)
		{
			List<String> eachEmployee = new ArrayList<>();
			eachEmployee.add(employeePayRoll.getEmployeeName());


			eachEmployee.add(employeePayRoll.getEmployeeId());
			eachEmployee.add(employeePayRoll.getBasicPay().toString());
			eachEmployee.add(employeePayRoll.getDearnessAllow().toString());
			eachEmployee.add(employeePayRoll.getOverTime().toString());
			eachEmployee.add(employeePayRoll.getEmployeePf().toString());
			eachEmployee.add(employeePayRoll.getNetPay().toString());

			result.add(eachEmployee);
		}
	}

	private void createPdfFile(List<List<String>> result, String paySlip, String baseNameFromFileName) {

		try {

		String fileDestination = Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY).concat("/").
				concat(baseNameFromFileName).concat(PDF_EXTENSION);
		PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileDestination));

		Document payCheckDoc = new Document(pdfDocument);

			Table table = new Table(7);
			addHeaderRows(table);
			for(List<String> eachRow : result )
			{
				for(String empData : eachRow)
				{

					table.addCell(empData);
				}
			}

			payCheckDoc.add(table);
			payCheckDoc.close();


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void addHeaderRows(Table table) {
		table.addHeaderCell("Employee Name");
		table.addHeaderCell("Employee Id");
		table.addHeaderCell("Basic Salary");
		table.addHeaderCell("Dearness Allowance");
		table.addHeaderCell("Over Time Allowance");
		table.addHeaderCell("Employee PF");
		table.addHeaderCell("NET INCOME");
	}


}

