package com.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class UniqueIdGenerator {
	private static final String ORIGIN_CODE = "MHR";
	private static int previousValue = 0;
	private static int previousDay = 0;
	private static AtomicInteger offset = new AtomicInteger();

	public static String generateUniqueId() {
		LocalDateTime now = LocalDateTime.now();
		return ORIGIN_CODE + getCurrentDateAsString(now) + generateUniqueNewIdPerDay(now);
	}

	protected static String generateUniqueNewIdPerDay(LocalDateTime now) {
		double milliseconds = now.getNano() / 1000000.0;
		int currentValue = (int) (milliseconds + (100 * now.getSecond()) + (100 * 60 * now.getMinute())
				+ (100 * 60 * 60 * now.getHour()));

		resetPreviousValueOnDayRoll(now);

		if (currentValue > previousValue) {
			offset.set(0);
		} else {
			while (currentValue <= previousValue) {
				currentValue = +offset.incrementAndGet();
			}
		}
		previousValue = currentValue;
		return StringUtils.leftPad(String.valueOf(currentValue), 7, "0");
	}

	private static void resetPreviousValueOnDayRoll(LocalDateTime now) {
		int currentDay = now.getDayOfYear();
		if (previousDay < currentDay) {
			offset.set(0);
			previousDay = currentDay;
		}
	}

	private static String getCurrentDateAsString(LocalDateTime now) {
		return now.format(DateTimeFormatter.ofPattern("yyMMdd"));
	}
}
