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
	private static final String[] headerColumns = { "Employer Name", "Employee Name", "Pay Roll Month","UAN", "Insurance Number", "Basic", "DA",
			"Allowance", "Total No Of Working Days","Actual Working Days","Earned Basic","Earned DA","Earned Basic Plus DA","Earned Allowance","Gross Pay","EPF Employee Contribution","ESI",
			"Total Deductions", "Net Salary","EPS Employer Contribution","EPF Employer Contribution" };
	private static final String COMMA_DELIMITER = ",";
	private static final String EPF_DELIMITER = "#~#";
	private static final String NEW_LINE = "\n";
	public static final String RESULT_FILE_NAME = "_result";
	public static final String EPF_FILE_NAME = "_epf";
	public static final String CSV = ".csv";
	public static final String TXT = ".txt";

	public void createCsvFile(List<EmployeePayRoll> employeePayRollList,String originalFileName) {
		List<List<String>> finalResultForMaster = new ArrayList<>();
		createHeaderRow(finalResultForMaster);
		populateEmployeeDataForMaster(employeePayRollList, finalResultForMaster);
		writeDataToFile(FileHelper.getBaseNameFromFileName(originalFileName)+ RESULT_FILE_NAME + CSV, finalResultForMaster,COMMA_DELIMITER);
		finalResultForMaster = new ArrayList<>();
		populateEmployeeDataForEPF(employeePayRollList, finalResultForMaster);
		writeDataToFile(FileHelper.getBaseNameFromFileName(originalFileName)+ EPF_FILE_NAME + TXT, finalResultForMaster,EPF_DELIMITER);
	}

	public void createHeaderRow(List<List<String>> finalResult) {
		List<String> header = new ArrayList<>();
		header.addAll(Arrays.asList(headerColumns));
		finalResult.add(header);
	}

	private void populateEmployeeDataForMaster(List<EmployeePayRoll> employeePayRollList, List<List<String>> finalResult) {
		for (EmployeePayRoll employeePayRoll : employeePayRollList) {
			if(employeePayRoll.getActualWorkingDays()>0)
			{
				List<String> rowData = new ArrayList<>(Collections.nCopies(21, EMPTY));
				rowData.set(0, employeePayRoll.getClientName());
				rowData.set(1, employeePayRoll.getEmployeeName());
				rowData.set(2, employeePayRoll.getPayRollMonth());
				rowData.set(3, employeePayRoll.getUan());
				rowData.set(4, employeePayRoll.getInsuranceNumber());
				rowData.set(5, employeePayRoll.getBasicPay().toString());
				rowData.set(6, employeePayRoll.getDearnessAllow().toString());
				rowData.set(7, employeePayRoll.getAllowance().toString());
				rowData.set(8, String.valueOf(employeePayRoll.getNumberOfWorkingDays()));
				rowData.set(9, String.valueOf(employeePayRoll.getActualWorkingDays()));
				rowData.set(10, employeePayRoll.getEarnedBasic().toString());
				rowData.set(11, employeePayRoll.getEarnedDearnessAllowance().toString());
				rowData.set(12, employeePayRoll.getEarnedBasicPlusDa().toString());
				rowData.set(13, employeePayRoll.getEarnedAllowance().toString());
				rowData.set(14, employeePayRoll.getEarnedGross().toString());
				rowData.set(15, employeePayRoll.getEmployeePf().toString());
				rowData.set(16, employeePayRoll.getEmployeeEsi().toString());
				rowData.set(17, employeePayRoll.getTotalDeductions().toString());
				rowData.set(18, employeePayRoll.getNetPay().toString());
				rowData.set(19, employeePayRoll.getEmployerEps().toString());
				rowData.set(20, employeePayRoll.getEmployerEpf().toString());
				finalResult.add(rowData);
			}
		}
	}


	private void populateEmployeeDataForEPF(List<EmployeePayRoll> employeePayRollList, List<List<String>> finalResult) {
		for (EmployeePayRoll employeePayRoll : employeePayRollList) {
			if(employeePayRoll.getActualWorkingDays()>0)
			{
				List<String> rowData = new ArrayList<>(Collections.nCopies(11, EMPTY));
				rowData.set(0, employeePayRoll.getUan());
				rowData.set(1, employeePayRoll.getEmployeeName());
				rowData.set(2, employeePayRoll.getEarnedBasicPlusDa().truncate(0).toString());
				rowData.set(3, employeePayRoll.getEarnedBasicPlusDa().truncate(0).toString());
				rowData.set(4, employeePayRoll.getEarnedBasicPlusDa().truncate(0).toString());
				rowData.set(5, employeePayRoll.getEarnedBasicPlusDa().truncate(0).toString());
				rowData.set(6, employeePayRoll.getEmployeePf().truncate(0).toString());
				rowData.set(7, employeePayRoll.getEmployerEps().truncate(0).toString());
				rowData.set(8, employeePayRoll.getEmployerEpf().truncate(0).toString());
				rowData.set(9, "0");
				rowData.set(10, "0");
				finalResult.add(rowData);
			}
		}
	}

	protected void writeDataToFile(String fileName, List<List<String>> dataToCSV, String demlimiter) {
		try (FileWriter writer = new FileWriter(new File(Config.getProperty(UPLOAD_FILE_LOCATION)) + "/" + fileName)) {
			log.info("writing report data to csv file ");
			for (List<String> rowToCSV : dataToCSV) {
				writer.write(rowToCSV.stream().collect(Collectors.joining(demlimiter)));
				writer.write(NEW_LINE);
			}
		} catch (Exception e) {
			log.error("Unable to write data to file {} due to Excepton {}", fileName, e);
		}
	}
}
