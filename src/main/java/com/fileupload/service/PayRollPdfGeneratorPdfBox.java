package com.fileupload.service;

import com.config.Config;
import com.fileupload.util.PDFUtil;
import com.model.EmployeePayRoll;
import com.model.ProxyStream;
import com.util.FileHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDMMType1Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by xeccwrj on 9/18/2017.
 */
@Slf4j
@Service
public class PayRollPdfGeneratorPdfBox {

	public static final String MY_HR_PAYROLL_SOLUTIONS = "                                                    MY HR PAYROLL SOLUTIONS";
	@Getter(AccessLevel.PROTECTED)
	private final Map<String, Format> formats;


	@Getter(AccessLevel.PROTECTED)
	private float offSet;

	protected static final String HEADING1 = "HEADING1";
	protected static final String HEADING2 = "HEADING2";
	protected static final String HEADING3 = "HEADING3";
	protected static final String DISCLAIMER1 = "DISCLAIMER1";
	protected static final String DISCLAIMER2 = "DISCLAIMER2";
	protected static final String DISCLAIMER3 = "DISCLAIMER3";
	protected static final String URL = "URL";
	protected static final String BROWN_COLOR_CODE = "#A52A2A";
	protected static final String PLAIN_FORMAT = "Plain";
	protected static final String FORMAT = "FORMAT:";
	protected static final String DIVIDER = "^";
	protected static final String BLANK_LINE = "  ";

	protected static final int PAGE_WIDTH = 80;
	protected static final float ROW_HIGHT = 10f;
	protected static final float LEFT_INDENT = 30f;
	protected static final float PAGE_HIGHT = 780f;

	private PDFUtil pdfUtil = new PDFUtil();
	private ProxyStream stream;
	private PDPage page;
	private PDDocument document;

	protected static final String FAX_NAS_BACKUP_FOLDER_KEY = "fax.nas.backup.folder";
	protected static final String PDF_EXTENSION = ".pdf";

	public PayRollPdfGeneratorPdfBox() {
		formats = new HashMap<>();
		formats.put(HEADING1, new Format(Color.RED, PDType1Font.HELVETICA_BOLD, 12f));
		formats.put(HEADING2,
				new Format(Color.decode(BROWN_COLOR_CODE), PDType1Font.HELVETICA_BOLD, 12f));
		formats.put(HEADING3, new Format(Color.BLUE, PDType1Font.HELVETICA_BOLD, 12f));
		formats.put(DISCLAIMER1, new Format(Color.RED, PDType1Font.COURIER_BOLD, 10f));
		formats.put(DISCLAIMER2,
				new Format(Color.decode(BROWN_COLOR_CODE), PDType1Font.COURIER_BOLD, 10f));
		formats.put(DISCLAIMER3,
				new Format(Color.decode(BROWN_COLOR_CODE), PDType1Font.COURIER_BOLD, 10f));
		formats.put(URL, new Format(Color.BLUE, PDType1Font.HELVETICA_BOLD, 12f));
		formats.put(PLAIN_FORMAT, new Format(Color.BLACK, PDMMType1Font.COURIER_BOLD, 10f));
	}


	public List<String> createPayRollPDf(List<EmployeePayRoll> employeePayRollList,String fileName)
	{
		List<String> result = new ArrayList<>();
		populateCoverPage(result,employeePayRollList);
		populateInformation(result,employeePayRollList);
		createPdfFile(result,"_PaySlip", FileHelper.getBaseNameFromFileName(fileName));
		return result;
	}

	private void populateInformation(List<String> result, List<EmployeePayRoll> employeePayRollList) {

		for(EmployeePayRoll employeePayRoll : employeePayRollList)
		{
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Employee Name :"+employeePayRoll.getEmployeeName()+" \n");
			stringBuilder.append("Employee Id :"+employeePayRoll.getEmployeeId()+" \n");
			stringBuilder.append("Basic Salary :"+employeePayRoll.getBasicPay()+" \n");
			stringBuilder.append("Dearness Allowance :"+employeePayRoll.getDearnessAllow()+" \n");
			stringBuilder.append("Over Time Allowance :"+employeePayRoll.getAllowance()+" \n");
			stringBuilder.append("Employee PF :"+employeePayRoll.getEmployeePf()+" \n");
			stringBuilder.append("NET INCOME :"+employeePayRoll.getNetPay()+" \n");

			result.add(stringBuilder.toString());
		}
	}

	private void populateCoverPage(List<String> result, List<EmployeePayRoll> employeePayRollList) {
		result.add(FORMAT + HEADING1 + DIVIDER + MY_HR_PAYROLL_SOLUTIONS);
	}

	private File createPdfFile(final List<String> reportData, final String suffix,String fileName) {
		File result = null;
		try {
			document = newDocument();
			populatePages(reportData);
			result = writeToFile(fileName, suffix);
		} catch (Exception e) {
			log.error("Failed to create pdf.", e);
		}
		closeDocument();
		return result;
	}

	private void populatePages(final List<String> reportData) {
		page = newPage();
		try {
			stream = newContentStream();
			reportData.forEach(this::writeTo);
		} catch (Exception e) {
			log.error("Failed to write to pdfcontent stream.", e);
		}
		closeStream();
	}


	private File writeToFile(final String docId, final String suffix) throws IOException {
		File file = getFile(docId, suffix);
		document.save(file);
		return file;
	}

	private File mergeWith(final File file, final String docId, final String suffix) {
		File original = getReportFile(docId,suffix);
		/*if (!original.equals(file))
		{*/
			merge(original, file);

		//}
		return original;
	}

	private void merge(final File original, final File file) {
		pdfUtil.pdfMerger(Arrays.asList(file, original), original);
		if (!file.delete()) {
			log.warn("Could not delete the temporary file: {}", file.getAbsolutePath());
		}
	}

	private File getFile(final String docId, final String suffix) {
		return getReportFile(docId, suffix);
	}
	private File getReportFile(final String docId, final String suffix) {
		return new File(Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY, "."), docId.concat(suffix).concat(PDF_EXTENSION));
	}

	private void writeTo(final String line) {
		writePlainText(line);
		writeUrl(line);
	}

	private void writePlainText(final String line) {
		if (!isPlainText(line))
			return;

		Format format = getFormat(line);
		String text = getUnformattedText(line);
		Arrays.asList(text.split("(?<=\\G.{" + PAGE_WIDTH + "})")).forEach(c -> writeSingleLine(c, format));
	}

	private String getUnformattedText(final String line) {
		return line.contains(DIVIDER) ? StringUtils.substringAfter(line, DIVIDER)
				: line;
	}

	private void writeSingleLine(final String content, final Format format) {
		try {
			checkOffset();
			write2Stream(content, format);
		} catch (Exception e) {
			log.error("Error while writing a line of text.", e);
		}
		newLine();
	}

	private void write2Stream(final String content, final Format format) throws IOException {
		stream.beginText();
		stream.newLineAtOffset(LEFT_INDENT, offSet);
		stream.setNonStrokingColor(format.color);
		stream.setFont(format.fontType, format.fontSize);
		showText(content);
		stream.endText();
	}



	private void showText(final String content) {
		try {
			stream.showText(content.replaceAll("\\r|\\n|\\u00A0|\\u2007|\\u202F", BLANK_LINE));
		} catch (Exception e) {
			log.error("Please remove new line character. PDFBox does not support it.", e);
		}
	}

	private void writeUrl(final String line) {
		if (!isUrlText(line))
			return;

		String[] tokens = line.split("\\" + DIVIDER, 3);
		newLine();
		writeUrlText(tokens[1], tokens[2], getFormat(line));
		newLine();
	}

	private void writeUrlText(final String text, final String url, final Format format) {
		try {
			checkOffset();
			float textWidth = format.fontType.getStringWidth(text) / 1000 * 18;
			stream.beginText();
			stream.newLineAtOffset(35, offSet);
			stream.setNonStrokingColor(format.color);
			stream.setFont(format.fontType, format.fontSize);
			stream.showText(text);
			stream.endText();
			page.getAnnotations().add(getTextLink(url, textWidth));
		} catch (Exception e) {
			log.info("Failed to write url {}", url, e);
		}
	}

	private PDAnnotationLink getTextLink(final String url, final float textWidth) {
		PDAnnotationLink txtLink = new PDAnnotationLink();
		txtLink.setAction(getAction(url));
		txtLink.setRectangle(getRectangle(textWidth));
		return txtLink;
	}

	private PDActionURI getAction(final String url) {
		PDActionURI action = new PDActionURI();
		action.setURI(url);
		return action;
	}

	private PDRectangle getRectangle(float textWidth) {
		PDRectangle rectangle = new PDRectangle();
		rectangle.setLowerLeftX(30f);
		rectangle.setLowerLeftY(offSet + 15f);
		rectangle.setUpperRightX(30f + textWidth);
		rectangle.setUpperRightY(offSet - 5f);
		return rectangle;
	}

	private void checkOffset() throws IOException {
		if (Math.round(offSet) <= 20) {
			stream.close();
			page = newPage();
			stream = newContentStream();
			resetOffSet();
		}
	}

	private void newLine() {
		offSet = offSet - ROW_HIGHT;
	}

	private Format getFormat(String text) {
		String format = text.startsWith(FORMAT)
				? StringUtils.substringBetween(text, FORMAT, DIVIDER)
				: text.startsWith(URL) ? URL : PLAIN_FORMAT;
		return formats.get(format);
	}

	private boolean isPlainText(String line) {
		return !isUrlText(line);
	}

	private boolean isUrlText(String line) {
		return StringUtils.startsWith(line, URL);
	}


	private void closeDocument() {
		try {
			if (document != null)
				document.close();
		} catch (IOException e) {
			log.error("Failed to close document.", e);
		}
	}

	private void closeStream() {
		try {
			stream.close();
		} catch (IOException e) {
			log.error("Failed to close stream.", e);
		}
	}

	protected PDDocument newDocument() {
		return new PDDocument();
	}

	protected PDPage newPage() {
		resetOffSet();
		PDPage newPage = new PDPage();
		if (document != null)
			document.addPage(newPage);
		return newPage;
	}

	protected ProxyStream newContentStream() throws IOException {
		return new ProxyStream(document, page);
	}

	private void resetOffSet() {
		offSet = PAGE_HIGHT;
	}

	protected class Format {
		Color color;
		PDFont fontType;
		float fontSize;

		Format(Color color, PDFont fontType, float fontSize) {
			super();
			this.color = color;
			this.fontType = fontType;
			this.fontSize = fontSize;
		}
	}



}

