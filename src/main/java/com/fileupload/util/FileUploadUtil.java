package com.fileupload.util;

import com.fileupload.model.TemplateInfo;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang.StringUtils.EMPTY;

@Slf4j
public class FileUploadUtil {

	public static final String CSV = ".csv";
	public static final String XLS = ".xls";
	public static final String XLSX = ".xlsx";
	public static final String TCEFIELDS_WORKSHEET = "TCE Fields";
	public static final String TCE_PREFIX = "TCE_";
	public static final String TCE_REQUIRED_KEY = "Y";
	private static final String SPECIAL_CHARS = "[-/@#$%^&_+=()]+";
	@Getter
	private static long templateLoadTimeStamp = 0;
	private static DataFormatter fieldFormatter = new DataFormatter();
	@Getter
	private static Map<String, String> tceFields = new LinkedHashMap<>();
	@Getter
	private static Map<String, Map<String, String>> templateConfig = new LinkedHashMap<>();

	private FileUploadUtil() {
	}

	public static TemplateInfo getMatchingTemplate(File sourceFolder, String uploadFileName, File configLocation,
												   String configFileName) throws IOException {
		TemplateInfo templateInfo = new TemplateInfo();
		try {
			if (templateConfig.isEmpty() || templateLoadTimeStamp < configLocation.lastModified()) {
				loadTemplateMappings(configLocation, configFileName);
			}

			List<String> clientColumnNames = getColumnHeader(sourceFolder, uploadFileName).stream()
					.map(String::toLowerCase).collect(Collectors.toList());
			log.debug("Checking match for: {}", clientColumnNames);

			if (clientColumnNames.isEmpty()) {
				return templateInfo;
			}

			List<String> mismatchedFields = null;
			int matchedFieldsCount = 0;
			String closestTemplateMatch = null;
			for (Map.Entry<String, Map<String, String>> templateEntry : templateConfig.entrySet()) {
				List<String> tmpClientList = templateConfig.get(templateEntry.getKey()).keySet().stream()
						.map(String::toLowerCase).collect(Collectors.toList());
				List<String> tmpMismatchedFields = new ArrayList<>();
				tmpMismatchedFields.addAll(tmpClientList);
				tmpClientList.retainAll(clientColumnNames);
				if (matchedFieldsCount < tmpClientList.size()) {
					tmpMismatchedFields.removeAll(clientColumnNames);
					matchedFieldsCount = tmpClientList.size();
					closestTemplateMatch = templateEntry.getKey();
					mismatchedFields = tmpMismatchedFields;
				}
			}
			log.debug("Found match for {}. Template name:{}", uploadFileName, closestTemplateMatch);
			templateInfo.setUnmatchedFields(getUnmatchedRequiredTceFields(closestTemplateMatch, mismatchedFields));
			templateInfo.setTemplateName(closestTemplateMatch);
		} catch (Exception e) {
			log.error("Problems finding template for: {}", uploadFileName, e);
		}
		return templateInfo;
	}

	private static List<String> getUnmatchedRequiredTceFields(String closestTemplateMatch,
	                                                          List<String> unmatchedFields) {
		return FileUploadUtil.getTemplateConfig().get(closestTemplateMatch).entrySet().stream()
				.filter(entry -> unmatchedFields.contains(entry.getKey().toLowerCase()) && TCE_REQUIRED_KEY
						.equalsIgnoreCase(FileUploadUtil.getTceFields().get(entry.getValue().toLowerCase())))
				.map(Map.Entry::getValue).collect(Collectors.toList());
	}

	public static List<List<String>> populateData(File sourceFolder, String filename) throws IOException {
		final List<List<String>> recordList = new ArrayList<>();
		if (filename.toLowerCase().endsWith(CSV)) {
			return populateDataFromCSV(new File(sourceFolder, filename));
		} else {
			createExcelFile(sourceFolder, filename).getSheetAt(0).rowIterator().forEachRemaining(e -> {
				if (!isEmptyRow(e) && (e.getLastCellNum() >= 10)) {
					recordList.add(populateData(e));
				}
			});
		}
		return recordList;
	}

	private static boolean isEmptyRow(Row row) {
		return IntStream.range(row.getFirstCellNum(), row.getLastCellNum())
				.filter(cellNum -> isCellNotEmpty(row.getCell(cellNum))).count() < 10;
	}

	private static boolean isCellNotEmpty(Cell cell) {
		return cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())
				&& !cell.toString().matches(SPECIAL_CHARS);
	}

	private static List<String> getColumnHeader(Sheet sheet) {
		Row lastRow, row = null;
		Iterator<Row> rowIt = sheet.rowIterator();
		while (rowIt.hasNext()) {
			int cellCount = 0;
			lastRow = row;
			row = rowIt.next();
			Iterator<Cell> cellIt = row.cellIterator();
			while (cellIt.hasNext()) {
				Cell cell = cellIt.next();
				if (isCellNotEmpty(cell))
					cellCount++;
			}
			if (cellCount >= 10) {
				return populatedListFromRow(row, lastRow);
			}
		}
		return new ArrayList<>();
	}

	public static List<String> getColumnHeader(File sourceFolder, String uploadFileName) throws IOException {
		if (uploadFileName.toLowerCase().endsWith(CSV)) {
			return getColumnHeaderFromCSV(createCSVReader(new File(sourceFolder, uploadFileName)).readNext());
		} else {
			return getColumnHeader(createExcelFile(sourceFolder, uploadFileName).getSheetAt(0));
		}
	}

	protected static void loadTemplateMappings(File configLocation, String configFileName) {
		try {
			templateLoadTimeStamp = configLocation.lastModified();
			log.debug("Loading bulk mappings from {}/{}", configLocation.getAbsoluteFile(), configFileName);
			Workbook workbook = createExcelFile(configLocation, configFileName);
			for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
				Sheet sheetAt = workbook.getSheetAt(index);
				if (!TCEFIELDS_WORKSHEET.equalsIgnoreCase(sheetAt.getSheetName())) {
					Map<String, String> columnMap = populateTemplateCache(sheetAt);
					templateConfig.put(sheetAt.getSheetName(), columnMap);
					log.debug("Loaded Bulk Mapping {}: {}", sheetAt.getSheetName(), columnMap);
				} else {
					tceFields = populateTemplateCache(sheetAt);
				}
			}
		} catch (IOException e) {
			log.error("Exception while creating workbook " + e);
		}
	}

	private static Map<String, String> populateTemplateCache(Sheet sheet) {
		Map<String, String> rowItems = new LinkedHashMap<>();
		sheet.rowIterator().forEachRemaining(e -> {
			String cellValueAsString = getCellValueAsString(e.getCell(0));
			if (StringUtils.isNotEmpty(cellValueAsString)) {
				rowItems.put(cellValueAsString.toLowerCase(), getCellValueAsString(e.getCell(1)));
			}
		});
		return rowItems;
	}

	private static Workbook createExcelFile(File fileLocation, String uploadFileName) throws IOException {
		if (uploadFileName.endsWith(XLS))
			return new HSSFWorkbook(getExcelInputStream(fileLocation, uploadFileName));
		else if (uploadFileName.endsWith(XLSX))
			return new XSSFWorkbook(getExcelInputStream(fileLocation, uploadFileName));
		else
			throw new IOException("The specified file is not Excel file");
	}

	public static void createCsvFile(File inputFileLocation, String uploadedFileName, File csvOutputFile) throws IOException {
		StringBuilder csvData = new StringBuilder();
		Workbook hSSBook;
		if (uploadedFileName.endsWith(XLS)) {
			hSSBook = new HSSFWorkbook(getExcelInputStream(inputFileLocation, uploadedFileName));
			writeToCSV(csvOutputFile, createCsvData(csvData, hSSBook));
		} else if (uploadedFileName.endsWith(XLSX)) {
			hSSBook = new XSSFWorkbook(getExcelInputStream(inputFileLocation, uploadedFileName));
			writeToCSV(csvOutputFile, createCsvData(csvData, hSSBook));
		} else if (uploadedFileName.endsWith(CSV)) {
			File originalFile = new File(inputFileLocation, uploadedFileName);
			Files.copy(originalFile.toPath(), csvOutputFile.toPath());
		}
	}

	private static StringBuilder createCsvData(StringBuilder csvData, Workbook hSSBook) {
		hSSBook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);
		hSSBook.getSheetAt(0).rowIterator().forEachRemaining(row -> {
			for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
				csvData.append(handleCommaFields(getCellValueAsString(row.getCell(i)))).append(",");
			}
			csvData.append("\r\n");
		});
		return csvData;
	}

	private static String handleCommaFields(String cellValueAsString) {
		return cellValueAsString.replace(",", "");
	}

	private static void writeToCSV(File csvOutputFile, StringBuilder csvData) throws IOException {
		if (csvOutputFile != null && csvData == null) {
			log.warn("Information can't be written to file");
			return;
		}
		try (FileOutputStream writer = new FileOutputStream(csvOutputFile)) {
			writer.write(csvData.toString().getBytes());
		}
	}

	private static FileInputStream getExcelInputStream(File fileLocation, String uploadFileName)
			throws FileNotFoundException {
		return new FileInputStream(new File(fileLocation, uploadFileName));
	}

	private static List<String> populateData(Row row) {
		return IntStream.range(row.getFirstCellNum(), row.getLastCellNum())
				.filter(i -> isNotTceCol(row, i)).mapToObj(i -> getCellValueAsString(row.getCell(i)))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	private static List<String> populatedListFromRow(Row h2, Row h1) {
		return IntStream.range(h2.getFirstCellNum(), h2.getLastCellNum()).filter(i -> isNotTceCol(h2, i))
				.mapToObj(i -> getHeaderCellValue(h1, h2, i)).collect(Collectors.toList());
	}

	private static String getHeaderCellValue(Row h1, Row h2, int i) {
		return getHeaderInitialPart(h1, i) + getCellValueAsString(h2.getCell(i)).trim();
	}

	private static String getHeaderInitialPart(Row h1, int i) {
		List partialHeaderList = Arrays.asList("Trade", "Settle");
		String result = EMPTY;
		if (h1 != null) {
			result = getCellValueAsString(CellUtil.getCell(h1, i)).trim();
		}
		return partialHeaderList.contains(result) ? result : EMPTY;
	}

	private static boolean isNotTceCol(Row header, int colIndex) {
		return !getCellValueAsString(header.getCell(colIndex)).toUpperCase().startsWith(TCE_PREFIX);
	}

	private static String getCellValueAsString(Cell cell) {
		if (cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return cell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC
					? String.valueOf(cell.getNumericCellValue()) : cell.getStringCellValue();
		}
		return fieldFormatter.formatCellValue(cell);
	}

	private static List<String> getColumnHeaderFromCSV(String[] headerArray) {
		return IntStream.range(0, headerArray.length).filter(i -> !headerArray[i].toUpperCase().startsWith(TCE_PREFIX))
				.mapToObj(j -> headerArray[j]).collect(Collectors.toList());
	}

	private static CSVReader createCSVReader(File csvFile) throws IOException {
		return new CSVReaderBuilder(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))
				.withCSVParser(new CSVParser()).build();
	}

	protected static List<List<String>> populateDataFromCSV(File csvFile) {
		List<List<String>> recordList = new ArrayList<>();
		try {
			CSVReader reader = createCSVReader(csvFile);
			String[] headerArray = reader.readNext();
			recordList.add(getColumnHeaderFromCSV(headerArray));
			List<Integer> tceCols = getTceColIndexes(headerArray);

			reader.forEach(line -> recordList.add(getRowWithoutTceCols(tceCols, line)));
		} catch (IOException e) {
			log.warn("Exception occured during parsing the Csv file {}", e);
		}
		return recordList;
	}

	private static List<String> getRowWithoutTceCols(List<Integer> tceCols, String[] rowArray) {
		return IntStream.range(0, rowArray.length).filter(j -> !tceCols.contains(j)).mapToObj(j -> rowArray[j])
				.collect(Collectors.toList());
	}

	private static List<Integer> getTceColIndexes(String[] headerArray) {
		return IntStream.range(0, headerArray.length).filter(i -> headerArray[i].toUpperCase().startsWith(TCE_PREFIX))
				.boxed().collect(Collectors.toList());
	}
}
