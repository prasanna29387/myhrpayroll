package com.fileupload.service;

import com.config.Config;
import com.itext7.NumbersToWords;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;
import com.itext7.CustomDashedLineSeparator;
import com.model.EmployeePayRoll;
import com.money.MoneyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	public static final String PAY_SLIP = "_PaySlip";

	public static void main(String args[])
	{
		Config.kickOffConfig();
		List<EmployeePayRoll> employeePayRolls = new ArrayList<>();

		EmployeePayRoll employeePayRoll = EmployeePayRoll.builder()
				.clientName("ufs_kone")
				.designation("HR")
				.employeeName("Yamini Shankar")
				.payRollMonth("November 2017")
				.uan("12312313")
				.insuranceNumber("12qeasdasdad")
				.basicPay(MoneyFactory.fromString("1000.19"))
				.dearnessAllow(MoneyFactory.fromString("2000.0"))
				.allowance(MoneyFactory.fromString("100"))
				.numberOfWorkingDays(20)
				.actualWorkingDays(10)
				.earnedBasic(MoneyFactory.fromString("1231"))
				.earnedDearnessAllowance(MoneyFactory.fromString("12312"))
				.earnedAllowance(MoneyFactory.fromString("12312"))
				.earnedGross(MoneyFactory.fromString("1231"))
				.employeePf(MoneyFactory.fromString("123"))
				.employeeEsi(MoneyFactory.fromString("123"))
				.totalDeductions(MoneyFactory.fromString("123123"))
				.netPay(MoneyFactory.fromString("123")).build();

		employeePayRolls.add(employeePayRoll);

		PayRollPdfGeneratorIText payRollPdfGeneratorIText = new PayRollPdfGeneratorIText();
		payRollPdfGeneratorIText.createPayRollPDf(employeePayRolls);

	}


	public void createPayRollPDf(List<EmployeePayRoll> employeePayRollList)
	{
		createPayCheck(employeePayRollList);
	}

	private void createPayCheck(List<EmployeePayRoll> employeePayRollList) {

		for(EmployeePayRoll employeePayRoll : employeePayRollList)
		{
			if(employeePayRoll.getActualWorkingDays()>0)
			{
				String finalFileName = employeePayRoll.getEmployeeName().concat(PAY_SLIP).concat(PDF_EXTENSION);
				createPdfFile(employeePayRoll,finalFileName);
			}
		}

	}

	private void createPdfFile(EmployeePayRoll employeePayRoll,  String finalFileName) {

		PdfDocument pdfDocument;
		Document payCheckDoc = null;
		try {

			String fileDestination = Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY).concat("/").
					concat(finalFileName);

			pdfDocument = new PdfDocument(new PdfWriter(fileDestination));
			pdfDocument.addNewPage();

			PageSize pageSize = PageSize.A4;
			payCheckDoc = new Document(pdfDocument, pageSize);

			addBorder(pdfDocument);
			createCompanyHeaderInfo(payCheckDoc,employeePayRoll);
			addLineSeperator(payCheckDoc);
			addEmployerCopyHeader(payCheckDoc);

			getEmployeeInfoPara(payCheckDoc);
			getEmployeeInfoTable(employeePayRoll, payCheckDoc);
			getEarningsPara(payCheckDoc);
			getEarningsTable(employeePayRoll,payCheckDoc);
			getDeductionsPara(payCheckDoc);
			getDeductionsTable(employeePayRoll,payCheckDoc);

			addBlankPara(payCheckDoc);
			addSignature(payCheckDoc);
			addDashSeperator(payCheckDoc);
			addEmployeeCopyHeader(payCheckDoc);

			getEmployeeInfoPara(payCheckDoc);
			getEmployeeInfoTable(employeePayRoll, payCheckDoc);
			getEarningsPara(payCheckDoc);
			getEarningsTable(employeePayRoll,payCheckDoc);
			getDeductionsPara(payCheckDoc);
			getDeductionsTable(employeePayRoll,payCheckDoc);
			addBlankPara(payCheckDoc);
			createFooter(payCheckDoc);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			payCheckDoc.close();
		}
	}

	private void addSignature(Document payCheckDoc) {

		try {
			Paragraph clientName = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			clientName.setFont(font);
			clientName.setFontSize(9);
			clientName.setItalic();
			clientName.add(new Tab()).add(new Tab()).add(new Tab()).add(new Tab()).add("Date : "+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).add(new Tab()).add("Signature : ");
			payCheckDoc.add(clientName);
//			clientName = new Paragraph();
//			font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
//			clientName.setFont(font);
//			clientName.setFontSize(9);
//			clientName.setItalic();
//			clientName.add(new Tab()).add(new Tab()).add(new Tab()).add(new Tab()).add(new Tab()).add(new Tab()).);
//			payCheckDoc.add(clientName);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void addBlankPara(Document payCheckDoc) {
		Paragraph blankPara = new Paragraph();
		blankPara.setFontSize(9);
		blankPara.add("");
		payCheckDoc.add(blankPara);
		payCheckDoc.add(blankPara);
	}

	private void addEmployerCopyHeader(Document payCheckDoc) {
		try {
			Paragraph employerNamePara = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			employerNamePara.setFont(font);
			employerNamePara.setFontSize(9);
			employerNamePara.add("Employer Copy");
			payCheckDoc.add(employerNamePara);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void addEmployeeCopyHeader(Document payCheckDoc) {
		try {
			Paragraph employerNamePara = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			employerNamePara.setFont(font);
			employerNamePara.setFontSize(9);
			employerNamePara.add("Employee Copy");
			payCheckDoc.add(employerNamePara);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createFooter(Document payCheckDoc) {
		try {
			Paragraph footer = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			footer.setFont(font);
			footer.setFontSize(6);
			footer.setItalic();
			footer.add("*** This is a confidential document and should not be disclosed. If you are not the intented person, you should immediately destory this document along with any copies.");
			payCheckDoc.add(footer);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getEmployeeInfoPara(Document payCheckDoc) {
		try {
			Paragraph personalInfoPara = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			personalInfoPara.setFont(font);
			personalInfoPara.setFontSize(9);
			personalInfoPara.add("Employee Information");
			payCheckDoc.add(personalInfoPara);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getEarningsPara(Document payCheckDoc) {
		try {
			Paragraph earningsPara = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			earningsPara.setFont(font);
			earningsPara.add("Earnings");
			earningsPara.setFontSize(9);
			payCheckDoc.add(earningsPara);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getDeductionsPara(Document payCheckDoc) {
		try {
			Paragraph earningsPara = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			earningsPara.setFont(font);
			earningsPara.add("Deductions");
			earningsPara.setFontSize(9);
			payCheckDoc.add(earningsPara);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private void getEmployeeInfoTable(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
		Table table = new Table(4);
		table.setFontSize(8);
		table.addCell("Employee Name");
		table.addCell(employeePayRoll.getEmployeeName());
		table.addCell("Designation");
		table.addCell(employeePayRoll.getDesignation());
		table.addCell("UAN");
		table.addCell(employeePayRoll.getUan());
		table.addCell("Insurance Number");
		table.addCell(employeePayRoll.getInsuranceNumber());
		payCheckDoc.add(table);
	}

	private void getDeductionsTable(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
		Table table = new Table(2);
		table.setFontSize(8);
		table.addCell("EPF");
		table.addCell(employeePayRoll.getEmployeePf().toString());
		table.addCell("ESI");
		table.addCell(employeePayRoll.getEmployeeEsi().toString());
		table.addCell("IT");
		table.addCell("N/A");
		table.addCell("PT");
		table.addCell("N/A");
		payCheckDoc.add(table);
		table = new Table(1);
		table.setFontSize(10);
		table.addCell("");
		table.addCell("Net Pay:  Rs "+employeePayRoll.getNetPay() +" (Rupees "+ NumbersToWords.convert((long)(employeePayRoll.getNetPay().truncate(0).toDouble()))+" Only )");
		payCheckDoc.add(table);
	}

	private void getEarningsTable(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
		Table table = new Table(2);
		table.setFontSize(8);
		table.addCell("Total No Of Working Days");
		table.addCell(String.valueOf(employeePayRoll.getNumberOfWorkingDays()));
		table.addCell("Actual Working Days");
		table.addCell(String.valueOf(employeePayRoll.getActualWorkingDays()));
		payCheckDoc.add(table);
		table = new Table(4);
		table.setFontSize(8);
		table.addCell("Basic");
		table.addCell(employeePayRoll.getBasicPay().toString());
		table.addCell("Earned Basic");
		table.addCell(employeePayRoll.getEarnedBasic().toString());
		table.addCell("Dearness Allowance");
		table.addCell(employeePayRoll.getDearnessAllow().toString());
		table.addCell("Earned Dearness Allowance");
		table.addCell(employeePayRoll.getEarnedDearnessAllowance().toString());
		table.addCell("Other Allowance");
		table.addCell(employeePayRoll.getAllowance().toString());
		table.addCell("Earned Other Allowance");
		table.addCell(employeePayRoll.getEarnedAllowance().toString());
		payCheckDoc.add(table);
		table = new Table(2);
		table.setFontSize(8);
		table.addCell("Total Earnings");
		table.addCell(employeePayRoll.getEarnedGross().toString());
		payCheckDoc.add(table);
	}

	private void addLineSeperator(Document payCheckDoc) {
		CustomDashedLineSeparator separator = new CustomDashedLineSeparator();
		separator.setDash(10);
		separator.setGap(0);
		separator.setLineWidth(1);
		payCheckDoc.add(new LineSeparator(separator));
	}

	private void addDashSeperator(Document payCheckDoc) {
		CustomDashedLineSeparator separator = new CustomDashedLineSeparator();
		separator.setDash(10);
		separator.setGap(2);
		separator.setLineWidth(1);
		payCheckDoc.add(new LineSeparator(separator));
	}

	private void addBorder(PdfDocument pdfDocument) {
		float width = pdfDocument.getDefaultPageSize().getWidth();
		float height = pdfDocument.getDefaultPageSize().getHeight();
		// Define a PdfCanvas instance
		PdfCanvas canvas = new PdfCanvas(pdfDocument.getFirstPage());
		// Add a rectangle
		canvas.rectangle(20, 20, width - 40, height - 40);
		canvas.setStrokeColor(Color.BLACK);
		canvas.stroke();
	}

	private void createCompanyHeaderInfo(Document document,EmployeePayRoll employeePayRoll) {

		try {
			Paragraph clientName = new Paragraph();
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			clientName.setFont(font);
			clientName.setFontSize(10);
			log.info("Client Name "+employeePayRoll.getClientName());
			log.info("Pay Roll Month "+employeePayRoll.getPayRollMonth());
			clientName.add(Config.getProperty(employeePayRoll.getClientName().toLowerCase()+".name")).add(new Tab()).add(new Tab()).add("PAY SLIP FOR THE MONTH OF "+employeePayRoll.getPayRollMonth().toUpperCase());
			document.add(clientName);
			font = PdfFontFactory.createFont(FontConstants.TIMES_ITALIC);
			clientName = new Paragraph();
			clientName.setFont(font);
			clientName.setFontSize(7);
			clientName.add(Config.getProperty(employeePayRoll.getClientName().toLowerCase()+".address")).add(new Tab());
			document.add(clientName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

