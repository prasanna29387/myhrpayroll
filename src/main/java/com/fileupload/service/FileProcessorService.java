package com.fileupload.service;

import com.fileupload.model.FileParserPayLoad;
import com.model.EmployeePayRoll;
import com.model.Record;
import com.util.EmployeePayRollMapper;
import com.util.MetaDataKeys;
import com.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class FileProcessorService {

	@Autowired
	PayRollCsvFileGenerator payRollCsvFileGenerator;

	@Autowired
	PayRollPdfGenerator payRollPdfGenerator;

	public void processFile(ResponseEntity<String> response, String clientName,String originalFileName) {
		List<List<Record>> listOfRecords;
		List<EmployeePayRoll> employeePayRolls = null;
		try {
			FileParserPayLoad payLoad = populatePayLoad(response, clientName);
			removeColumnHeaders(payLoad);
			listOfRecords = createRecordsFromPayload(payLoad);

			if (listOfRecords != null && listOfRecords.size() > 0) {
				employeePayRolls = populateEmployeePayRoll(listOfRecords);
			}

			payRollCsvFileGenerator.createCsvFile(employeePayRolls,originalFileName);
			payRollPdfGenerator.createPayRollPDf(employeePayRolls,originalFileName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private EmployeePayRoll computeMonthlyPayCheck(EmployeePayRoll employeePayRoll) {
		employeePayRoll.setEmployeePf(employeePayRoll.getBasicPay().multiply(0.12));
		employeePayRoll.setNetPay(employeePayRoll.getBasicPay().add(employeePayRoll.getDearnessAllow().add(employeePayRoll.getOverTime())).subtract(employeePayRoll.getEmployeePf()));
		return employeePayRoll;
	}

	private List<List<Record>> createRecordsFromPayload(FileParserPayLoad payLoad) {
		List<List<Record>> listOfRecords = new ArrayList<>();
		for (List<String> row : payLoad.getData()) {
			listOfRecords.add(convertPayLoadToListOfRecords(payLoad, row));
		}
		return listOfRecords;
	}

	private FileParserPayLoad populatePayLoad(ResponseEntity<String> response, String clientName) throws Exception {
		FileParserPayLoad payLoad = FileParserPayLoad.fromJson(response.getBody());
		payLoad.setClientName(clientName);
		if (StringUtils.isEmpty(payLoad.getTemplateName())) {
			payLoad.setColumns(new HashMap<>());
		}
		log.debug("Processing {}, dataRows:{}, columnCount", payLoad, payLoad.getRowsCount(),
				payLoad.getColumns().size());
		return payLoad;
	}

	private void removeColumnHeaders(FileParserPayLoad payLoad) {
		if (payLoad.getData() != null && payLoad.getData().size()>0)
			payLoad.getData().remove(0);
	}

	protected List<Record> convertPayLoadToListOfRecords(FileParserPayLoad payLoad, List<String> record) {
		List<Record> records = new ArrayList<>();
		if (payLoad == null || payLoad.getColumns() == null || payLoad.getHeaders() == null) {
			return records;
		}
		try {
			IntStream.range(0, getUploadedFileHeaderFieldsCount(payLoad))
					.filter(e -> StringUtils.isNotEmpty(payLoad.getColumns().get(payLoad.getHeaders().get(e))))
					.forEachOrdered(e -> records.add(Record.builder().key(payLoad.getColumns().get(payLoad.getHeaders().get(e)))
							.value(getRecordValue(record, e)).build()));

			records.add(Record.builder().key(MetaDataKeys.CLIENT_NAME).value(payLoad.getClientName()).build());
			records.add(Record.builder().key(MetaDataKeys.UPLOADED_FILE).value(payLoad.getClientFileName()).build());
			records.add(Record.builder().key(MetaDataKeys.TEMPLATE_VALUE).value(payLoad.getTemplateName()).build());
			log.debug("{} OCR Fields created: {}", records.size(), records);
		} catch (Exception e) {
			log.warn("Problem populating OcrFields. ", e);
		}
		return records;
	}

	private int getUploadedFileHeaderFieldsCount(FileParserPayLoad payLoad) {
		return payLoad.getHeaders().size();
	}

	private String getRecordValue(List<String> record, final int index) {
		try {
			return record.get(index) != null ? record.get(index).trim().toUpperCase() : record.get(index);
		} catch (IndexOutOfBoundsException ex) {
			log.warn("Uploaded file has invalid data in column :{} exception {}", index + 1, ex);
		}
		return StringUtils.EMPTY;
	}

	private List<EmployeePayRoll> populateEmployeePayRoll(List<List<Record>> listOfRecords) {
		List<EmployeePayRoll> employeePayRolls = new ArrayList<>();
		String uniqueUploadId = UniqueIdGenerator.generateUniqueId();
		for (List<Record> records : listOfRecords) {
			EmployeePayRoll employeePayRoll = createEmployeeRecord(records,uniqueUploadId);
			computeMonthlyPayCheck(employeePayRoll);
			employeePayRolls.add(employeePayRoll);
		}
		log.debug("List of Employee Payroll {}",employeePayRolls.toString());
		return employeePayRolls;
	}

	private EmployeePayRoll createEmployeeRecord(List<Record> records,String uniqueUploadId) {
		EmployeePayRoll employeePayRoll = EmployeePayRoll.builder()
				.clientName(EmployeePayRollMapper.getClientName(records))
				.employeeName(EmployeePayRollMapper.getEmployeeName(records))
				.employeeId(EmployeePayRollMapper.getEmployeeId(records))
				.basicPay(EmployeePayRollMapper.getBasicSalary(records))
				.dearnessAllow(EmployeePayRollMapper.getDearnessAllownace(records))
				.overTime(EmployeePayRollMapper.getOverTimeAllowance(records))
				.transactionId(uniqueUploadId)
				.uploadedFileName(EmployeePayRollMapper.getUploadedFile(records)).build();
		return employeePayRoll;
	}

}



