package com.util;


import com.money.Money;
import com.money.MoneyFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RulesHelper {

	private RulesHelper() {
	}
	public static boolean compareDatesGreaterThan(LocalDate date1, String date2) {
		try {
			LocalDate mDate = LocalDate.parse(date2);
			return date1.isAfter(mDate);
		} catch (Exception e) {
			log.error("Dates comparison failed ", e);
		}
		return false;
	}

	public static boolean compareDatesLesserThan(LocalDate date1, String date2) {
		try {
			LocalDate mDate = LocalDate.parse(date2);
			return date1.isBefore(mDate);
		} catch (Exception e) {
			log.error("Dates comparison failed ", e);
		}
		return false;
	}


	private static Money getMoneyValue(Money money) {
		return money == null ? MoneyFactory.fromDouble(0D) : money;
	}

	public static boolean isNumeric(String s) {
		return s != null && s.matches("[0-9]+");
	}

	public static boolean isAlphaNumeric(String s) {
		return s.matches("[a-zA-Z0-9]+");
	}

	public static boolean isAlphaNumericWithSpaces(String s) {
		return s.matches("[a-zA-Z0-9\\s]+");
	}

	public static boolean isValidEmail(String email)
	{
		return email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}

	public static boolean isValidSwiftFormat(String input) {
		return input.matches("[a-zA-Z0-9\\s\\.\\,\\-\\(\\)\\/\\'\\+\\:\\?\\r\\n]+");
	}

	public static boolean isValidPhone(String phone)
	{
		return phone.matches("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$");
	}

	public static boolean isAlphaOnly(String s) {
		return s.matches("[a-zA-Z]+");
	}

	public static int noOfDigitsAfterDecimal(String s) {
		int index = s.indexOf('.');
		return index == -1 ? 0 : s.substring(index + 1).length();
	}

	public static int noOfDigitsBeforeDecimal(String s) {
		int index = s.indexOf('.');
		return index == -1 ? s.length() : s.substring(0, index).length();
	}

	public static boolean isValidISOCurrency(String inputCurr) {
		return Currency.getAvailableCurrencies().stream().filter(c -> c.getCurrencyCode().equals(inputCurr))
				.count() == 1;
	}

	public static int getISOCurrencyPrecision(String curr) {
		return Currency.getInstance(curr).getDefaultFractionDigits();
	}

	private static boolean notNullAndHasSize(String input, int size) {
		return input != null && input.length() >= size;
	}




	public static int addNumbers(int... args)
	{
		int out = 0;
		for(int value : args)
		{
			out = out + value;
		}
		return out;
	}



}
