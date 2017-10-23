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
 * Created by xeccwrj on 9/15/2017.
 */
public class EmployeePayRollMapper {

	protected static final Map<String, String> fields = new HashMap<>();
	public static final char DOT_CHAR = '.';

	static {
		fields.put("templateName", MetaDataKeys.TEMPLATE_VALUE);
		fields.put("clientName", MetaDataKeys.CLIENT_NAME);
		fields.put("employeeName", MetaDataKeys.EMPLOYEE_NAME);
		fields.put("employeeId", MetaDataKeys.EMPLOYEE_ID);
		fields.put("uploadedFile",MetaDataKeys.UPLOADED_FILE);
		fields.put("basic",MetaDataKeys.BASIC_AMT);
		fields.put("da",MetaDataKeys.DEARNESS_ALLOWNACE);
		fields.put("ot",MetaDataKeys.OVER_TIME_ALLOWNACE);
	}

	public static String getTemplateName(List<Record> records)
	{
		return getValue("templateName", records);
	}

	public static String getClientName(List<Record> records)
	{
		return getValue("clientName", records);
	}

	public static String getEmployeeName(List<Record> records)
	{
		return getValue("employeeName", records);
	}

	public static String getEmployeeId(List<Record> records)
	{
		return getValue("employeeId", records);
	}

	public static String getUploadedFile(List<Record> records)
	{
		return getValue("uploadedFile", records);
	}


	public static Money getBasicSalary(List<Record> records) {
		return getAmountFromField("basic", records);
	}
	public static Money getDearnessAllownace(List<Record> records) {
		return getAmountFromField("da", records);
	}
	public static Money getOverTimeAllowance(List<Record> records) {
		return getAmountFromField("ot", records);
	}

	private static Money getAmountFromField(String name, List<Record> records) {
		return getAmountFromField(getValue(name, records));
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
