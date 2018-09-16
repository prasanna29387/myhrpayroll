package com.fileupload.service;

import com.config.Config;
import com.fileupload.model.FileParserPayLoad;
import com.model.EmployeePayRoll;
import com.model.Record;
import com.money.Money;
import com.money.MoneyFactory;
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
	PayRollPdfGeneratorIText payRollPdfGeneratorIText;

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
			payRollPdfGeneratorIText.createPayRollPDf(employeePayRolls,originalFileName);
			if(Config.getProperty(employeePayRolls.get(0).getClientName().toLowerCase()+".pmrpy").equalsIgnoreCase("Y"))
			{
				payRollCsvFileGenerator.createPmrpyFile(employeePayRolls,originalFileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	private EmployeePayRoll computeMonthlyPayCheck(EmployeePayRoll employeePayRoll) {


		if(employeePayRoll.getClientName().equalsIgnoreCase("immanuel_agencies"))
		{

			employeePayRoll.setDearnessAllow(MoneyFactory.fromString("0"));
			employeePayRoll.setEarnedDearnessAllowance(MoneyFactory.fromString("0"));
			employeePayRoll.setBasicPay(employeePayRoll.getWage().multiply(employeePayRoll.getNumberOfWorkingDays()).truncate(0));
			employeePayRoll.setEarnedBasic(employeePayRoll.getWage().multiply(employeePayRoll.getActualWorkingDays()).truncate(0));
			employeePayRoll.setEarnedAllowance(employeePayRoll.getAllowance().multiply(employeePayRoll.getActualWorkingDays()).truncate(0));
			employeePayRoll.setEarnedBasicPlusDa(employeePayRoll.getEarnedBasic());
			employeePayRoll.setEarnedGross(employeePayRoll.getEarnedBasic().add(employeePayRoll.getEarnedAllowance()));
			employeePayRoll.setEmployeePf(employeePayRoll.getEarnedBasic().multiply(0.12).truncate(0));
			employeePayRoll.setEmployeeEsi(employeePayRoll.getEarnedGross().multiply(0.0175).truncate(0));

			employeePayRoll.setTotalDeductions(employeePayRoll.getEmployeePf().add(employeePayRoll.getEmployeeEsi()).truncate(0));
			employeePayRoll.setNetPay(employeePayRoll.getEarnedGross().subtract(employeePayRoll.getTotalDeductions()).truncate(0));

			employeePayRoll.setEmployerEps(employeePayRoll.getEarnedBasic().multiply(0.0833).truncate(0));
			employeePayRoll.setEmployerEpf(employeePayRoll.getEarnedBasic().multiply(0.0367).truncate(0));




		}
		else if(employeePayRoll.getClientName().equalsIgnoreCase("ufs_kone")) {

			employeePayRoll.setEarnedBasic(((employeePayRoll.getBasicPay().divide(employeePayRoll.getNumberOfWorkingDays(), 2))
					.multiply(employeePayRoll.getActualWorkingDays())).truncate(0));

			employeePayRoll.setEarnedDearnessAllowance(((employeePayRoll.getDearnessAllow().divide(employeePayRoll.getNumberOfWorkingDays(), 2)).
					multiply(employeePayRoll.getActualWorkingDays())).truncate(0));

			Money earnedBasicPlusDa = employeePayRoll.getEarnedBasic().add(employeePayRoll.getEarnedDearnessAllowance()).truncate(0);

			employeePayRoll.setEarnedAllowance(((employeePayRoll.getAllowance().divide(employeePayRoll.getNumberOfWorkingDays(), 2)).
					multiply(employeePayRoll.getActualWorkingDays())).truncate(0));

			employeePayRoll.setEarnedBasicPlusDa(earnedBasicPlusDa);

			employeePayRoll.setEarnedGross(earnedBasicPlusDa.add(employeePayRoll.getEarnedAllowance()).truncate(0));

			employeePayRoll.setEmployeePf(earnedBasicPlusDa.multiply(0.12).truncate(0));
			employeePayRoll.setEmployeeEsi(employeePayRoll.getEarnedGross().multiply(0.0175).truncate(0));


			employeePayRoll.setEmployerEps(earnedBasicPlusDa.multiply(0.0833).truncate(0));
			employeePayRoll.setEmployerEpf(earnedBasicPlusDa.multiply(0.0367).truncate(0));


			employeePayRoll.setTotalDeductions(employeePayRoll.getEmployeePf().add(employeePayRoll.getEmployeeEsi()).truncate(0));


			employeePayRoll.setNetPay(employeePayRoll.getEarnedGross().subtract(employeePayRoll.getTotalDeductions()).truncate(0));
		}

		else if(employeePayRoll.getClientName().equalsIgnoreCase("saisri_lables")) {

			employeePayRoll.setEarnedBasic(((employeePayRoll.getBasicPay().divide(employeePayRoll.getNumberOfWorkingDays(), 2))
					.multiply(employeePayRoll.getActualWorkingDays())).truncate(0));

			employeePayRoll.setEarnedDearnessAllowance(((employeePayRoll.getDearnessAllow().divide(employeePayRoll.getNumberOfWorkingDays(), 2)).
					multiply(employeePayRoll.getActualWorkingDays())).truncate(0));

			Money earnedBasicPlusDa = employeePayRoll.getEarnedBasic().add(employeePayRoll.getEarnedDearnessAllowance()).truncate(0);

			Money otMoney = employeePayRoll.getBasicPay().divide(employeePayRoll.getNumberOfWorkingDays(),2).divide(8,2).multiply(2).multiply(employeePayRoll.getOtHours()).truncate(0);

			employeePayRoll.setOtMoney(otMoney);
			employeePayRoll.setEarnedAllowance(((employeePayRoll.getAllowance().add(employeePayRoll.getHra()).add(employeePayRoll.getConveyance()).add(employeePayRoll.getOtMoney()))).truncate(0));

			employeePayRoll.setEarnedBasicPlusDa(earnedBasicPlusDa);

			employeePayRoll.setEarnedGross(earnedBasicPlusDa.add(employeePayRoll.getEarnedAllowance()).truncate(0));

			employeePayRoll.setEmployeePf(earnedBasicPlusDa.multiply(0.12).truncate(0));
			employeePayRoll.setEmployeeEsi(employeePayRoll.getEarnedGross().multiply(0.0175).truncate(0));


			employeePayRoll.setEmployerEps(earnedBasicPlusDa.multiply(0.0833).truncate(0));
			employeePayRoll.setEmployerEpf(earnedBasicPlusDa.multiply(0.0367).truncate(0));


			employeePayRoll.setTotalDeductions(employeePayRoll.getEmployeePf().add(employeePayRoll.getEmployeeEsi()).truncate(0));


			employeePayRoll.setNetPay(employeePayRoll.getEarnedGross().subtract(employeePayRoll.getTotalDeductions()).truncate(0));
		}

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
				.designation(EmployeePayRollMapper.getDesignation(records))
				.uan(EmployeePayRollMapper.getUAN(records))
				.insuranceNumber(EmployeePayRollMapper.getInsurance(records))
				.basicPay(EmployeePayRollMapper.getBasicSalary(records))
				.dearnessAllow(EmployeePayRollMapper.getDearnessAllownace(records))
				.allowance(EmployeePayRollMapper.getAllowance(records))
				.numberOfWorkingDays(EmployeePayRollMapper.getTotalNumberOfDays(records))
				.actualWorkingDays(EmployeePayRollMapper.getActualNumberOfDays(records))
				.wage(EmployeePayRollMapper.getWages(records))
				.hra(EmployeePayRollMapper.getHra(records))
				.conveyance(EmployeePayRollMapper.getConveyance(records))
				.otHours(EmployeePayRollMapper.getOTHours(records))
				.transactionId(uniqueUploadId)
				.payRollMonth(EmployeePayRollMapper.getPayrollMonth(records))
				.aadharNumber(EmployeePayRollMapper.getAadhar(records))
				.jobDescriptionId(EmployeePayRollMapper.getJobId(records))
				.pmrpy(EmployeePayRollMapper.getPMRPY(records))
				.uploadedFileName(EmployeePayRollMapper.getUploadedFile(records)).build();
		return employeePayRoll;
	}

}



