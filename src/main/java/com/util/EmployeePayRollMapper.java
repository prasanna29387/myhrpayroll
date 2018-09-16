package com.util;

import com.model.Record;
import com.money.Money;
import com.money.MoneyFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by Athul Ravindran  on 9/15/2017.
 */
public class EmployeePayRollMapper {

    protected static final Map<String, String> fields = new HashMap<>();
    public static final char DOT_CHAR = '.';

    static {
        fields.put("templateName", MetaDataKeys.TEMPLATE_VALUE);
        fields.put("clientName", MetaDataKeys.CLIENT_NAME);
        fields.put("employeeName", MetaDataKeys.EMPLOYEE_NAME);
        fields.put("employeeId", MetaDataKeys.EMPLOYEE_ID);
        fields.put("uploadedFile", MetaDataKeys.UPLOADED_FILE);
        fields.put("designation", MetaDataKeys.DESIGNATION);
        fields.put("uan", MetaDataKeys.UAN);
        fields.put("insurance", MetaDataKeys.INSURANCE_NUMBER);
        fields.put("basic", MetaDataKeys.BASIC_AMT);
        fields.put("da", MetaDataKeys.DEARNESS_ALLOWNACE);
        fields.put("allowance", MetaDataKeys.ALLOWANCE);
        fields.put("totalDays", MetaDataKeys.NUMBER_OF_DAYS);
        fields.put("actualDays", MetaDataKeys.ACTUAL_DAYS);
        fields.put("payrollMonth", MetaDataKeys.PAYROLL_MONTH);
        fields.put("wage", MetaDataKeys.WAGES);
        fields.put("hra", MetaDataKeys.HRA);
        fields.put("conveyance", MetaDataKeys.CONVEYANCE);
        fields.put("otHours", MetaDataKeys.OT_HRS);
        fields.put("aadhar", MetaDataKeys.AADHAR);
        fields.put("jobId", MetaDataKeys.JDID);
        fields.put("pmrpy", MetaDataKeys.PMRPY);

    }

    public static String getTemplateName(List<Record> records) {
        return getValue("templateName", records);
    }

    public static String getClientName(List<Record> records) {
        return getValue("clientName", records);
    }

    public static String getEmployeeName(List<Record> records) {
        return getValue("employeeName", records);
    }

    public static String getUAN(List<Record> records) {
        return checkAndReturnNA(getValue("uan", records));
    }

    private static String checkAndReturnNA(String value) {
        return StringUtils.isEmpty(value) ? "N/A" : value;
    }

    public static String getInsurance(List<Record> records) {
        return checkAndReturnNA(getValue("insurance", records));
    }

    public static String getAadhar(List<Record> records) {
        return checkAndReturnNA(getValue("aadhar", records));
    }
    public static String getJobId(List<Record> records) {
        return checkAndReturnNA(getValue("jobId", records));
    }

    public static String getDesignation(List<Record> records) {
        return checkAndReturnNA(getValue("designation", records));
    }

    public static String getPMRPY(List<Record> records) {
        return checkAndReturnNA(getValue("pmrpy", records));
    }

    public static String getEmployeeId(List<Record> records) {
        return getValue("employeeId", records);
    }

    public static String getPayrollMonth(List<Record> records) {
        return getValue("payrollMonth", records);
    }

    public static String getUploadedFile(List<Record> records) {
        return getValue("uploadedFile", records);
    }


    public static Money getBasicSalary(List<Record> records) {
        return getAmountFromField("basic", records);
    }

    public static Money getDearnessAllownace(List<Record> records) {
        return getAmountFromField("da", records);
    }


    public static Money getAllowance(List<Record> records) {
        return getAmountFromField("allowance", records);
    }
    public static Money getWages(List<Record> records) {
        return getAmountFromField("wage", records);
    }

    public static Money getHra(List<Record> records) {
        return getAmountFromField("hra", records);
    }

    public static Money getConveyance(List<Record> records) {
        return getAmountFromField("conveyance", records);
    }

    public static int getOTHours(List<Record> records) {
        return getIntegerValue("otHours", records);
    }

    public static int getTotalNumberOfDays(List<Record> records) {
        return getIntegerValue("totalDays", records);
    }

    public static int getActualNumberOfDays(List<Record> records) {
        return getIntegerValue("actualDays", records);
    }

    private static Money getAmountFromField(String name, List<Record> records) {
        return getAmountFromField(getValue(name, records));
    }

    public static int getIntegerValue(String name, List<Record> records) {
        return getIntegerValue(getValue(name, records));

    }

    public static int getIntegerValue(String value) {
        return isEmpty(value) ? new Integer(0) : Integer.valueOf(value);

    }

    public static Money getAmountFromField(String amount) {
        return isEmpty(amount) ? MoneyFactory.fromUnits(0, 0)
                : MoneyFactory.fromString(removeNonNumericCharactersFromAmount(amount).isEmpty()
                ? MoneyFactory.fromUnits(0, 0).toString() : removeNonNumericCharactersFromAmount(amount));
    }

    private static String removeNonNumericCharactersFromAmount(String source) {
        return handleMultipleDecimalsInAmount(source).replaceAll("[^\\d.-]", "");
    }

    protected static String handleMultipleDecimalsInAmount(final String input) {
        String amount = input;
        while (StringUtils.containsAny(amount, DOT_CHAR)
                && (amount.indexOf(DOT_CHAR) != amount.lastIndexOf(DOT_CHAR))) {
            amount = amount.replaceFirst("\\.", "\\,");
        }
        return amount;
    }

    protected static String getValue(String fieldName, List<Record> records) {
        if (fieldName == null || records == null) {
            return Record.builder().build().getValue();
        }
        return records.stream().filter(s -> fields.get(fieldName).equalsIgnoreCase(s.getKey()))
                .findFirst().orElse(Record.builder().build()).getValue();
    }


}
