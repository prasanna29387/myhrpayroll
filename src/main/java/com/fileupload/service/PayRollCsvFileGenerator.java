package com.fileupload.service;

import com.config.Config;
import com.model.EmployeePayRoll;
import com.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Slf4j
public class PayRollCsvFileGenerator {

	public static final String UPLOAD_FILE_LOCATION = "fax.nas.backup.folder";
	private static final String[] headerColumns = { "Employer Name", "Employee Name", "Employee Id", "Basic Pay", "DA",
			"Over Time", "Employee PF", "Net Pay" };
	private static final java.lang.CharSequence COMMA_DELIMITER = ",";
	private static final java.lang.CharSequence EPF_DELIMITER = "#~";
	private static final String NEW_LINE = "\n";

	public void createCsvFile(List<EmployeePayRoll> employeePayRollList,String originalFileName) {
		List<List<String>> finalResult = new ArrayList<>();
		createHeaderRow(finalResult);
		populateEmployeeData(employeePayRollList, finalResult);
		writeDataToCSV(FileHelper.getBaseNameFromFileName(originalFileName)+"_result"+".csv", finalResult);
		finalResult.remove(0);
		writeDataToEpf(FileHelper.getBaseNameFromFileName(originalFileName)+"_epf"+".txt", finalResult);
	}

	public void createHeaderRow(List<List<String>> finalResult) {
		List<String> header = new ArrayList<>();
		header.addAll(Arrays.asList(headerColumns));
		finalResult.add(header);
	}

	private void populateEmployeeData(List<EmployeePayRoll> employeePayRollList, List<List<String>> finalResult) {
		for (EmployeePayRoll employeePayRoll : employeePayRollList) {
			List<String> rowData = new ArrayList<>(Collections.nCopies(8, EMPTY));
			rowData.set(0, employeePayRoll.getClientName());
			rowData.set(1, employeePayRoll.getEmployeeName());
			rowData.set(2, employeePayRoll.getEmployeeId());
			rowData.set(3, employeePayRoll.getBasicPay().toString());
			rowData.set(4, employeePayRoll.getDearnessAllow().toString());
			rowData.set(5, employeePayRoll.getOverTime().toString());
			rowData.set(6, employeePayRoll.getEmployeePf().toString());
			rowData.set(7, employeePayRoll.getNetPay().toString());
			finalResult.add(rowData);
		}
	}

	protected void writeDataToCSV(String fileName, List<List<String>> dataToCSV) {
		try (FileWriter writer = new FileWriter(new File(Config.getProperty(UPLOAD_FILE_LOCATION)) + "/" + fileName)) {
			log.info("writing report data to csv file ");
			for (List<String> rowToCSV : dataToCSV) {
				writer.write(rowToCSV.stream().collect(Collectors.joining(COMMA_DELIMITER)));
				writer.write(NEW_LINE);
			}
		} catch (Exception e) {
			log.error("Unable to write data to file {} due to Excepton {}", fileName, e);
		}
	}

	protected void writeDataToEpf(String fileName, List<List<String>> dataToCSV) {
		try (FileWriter writer = new FileWriter(new File(Config.getProperty(UPLOAD_FILE_LOCATION)) + "/" + fileName)) {
			log.info("writing report data to csv file ");
			for (List<String> rowToCSV : dataToCSV) {
				writer.write(rowToCSV.stream().collect(Collectors.joining(EPF_DELIMITER)));
				writer.write(NEW_LINE);
			}
		} catch (Exception e) {
			log.error("Unable to write data to file {} due to Excepton {}", fileName, e);
		}
	}
}
