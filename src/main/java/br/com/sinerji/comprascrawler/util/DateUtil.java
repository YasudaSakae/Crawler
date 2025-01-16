package br.com.sinerji.comprascrawler.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtil {

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	public static final LocalDate TODAY = LocalDate.now();
	public static final LocalDateTime MIN_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0);
	
	private static final Map<String, Integer> MONTH_ACRONYMS_MAPPED;
	
	static {
		MONTH_ACRONYMS_MAPPED = new HashMap<>();
		MONTH_ACRONYMS_MAPPED.put("jan", 1);
		MONTH_ACRONYMS_MAPPED.put("feb", 2);
		MONTH_ACRONYMS_MAPPED.put("mar", 3);
		MONTH_ACRONYMS_MAPPED.put("apr", 4);
		MONTH_ACRONYMS_MAPPED.put("may", 5);
		MONTH_ACRONYMS_MAPPED.put("jun", 6);
		MONTH_ACRONYMS_MAPPED.put("jul", 7);
		MONTH_ACRONYMS_MAPPED.put("aug", 8);
		MONTH_ACRONYMS_MAPPED.put("sep", 9);
		MONTH_ACRONYMS_MAPPED.put("oct", 10);
		MONTH_ACRONYMS_MAPPED.put("nov", 11);
		MONTH_ACRONYMS_MAPPED.put("dec", 12);
	}
	
	public static String dateToStr(LocalDate date) {
		return DATE_FORMATTER.format(date);
	}
	
	public static LocalDate strToDate(String str, LocalDate defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		return strToDate(str);
	}

	public static LocalDate strToDate(String str) {
		return LocalDate.parse(str, DATE_FORMATTER);
	}
	
	public static String getNowFormatted() {
		return DATE_TIME_FORMATTER.format(LocalDateTime.now());
	}
	
	public static LocalDate formattedDateStrToDate(String dateStr) throws Exception {
		String[] split = dateStr.split(",");
		String monthAndDay = split[0];
		String year = split[1].trim();
		
		split = monthAndDay.split(" ");
		String month = split[0].trim().toLowerCase();
		String day = split[1].trim();
		
		return LocalDate.of(Integer.parseInt(year), MONTH_ACRONYMS_MAPPED.get(month), Integer.parseInt(day));
	}
	
	public static boolean isEqualsOrBefore(LocalDate d1, LocalDate d2) {
		return d1.isEqual(d2) || d1.isBefore(d2);
	}
	
	public static boolean isEqualsOrAfter(LocalDate d1, LocalDate d2) {
		return d1.isEqual(d2) || d1.isAfter(d2);
	}
	
	public static String parseUsDate(String usDateStr) {
		DateTimeFormatter usFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        LocalDate usDate = LocalDate.parse(usDateStr, usFormatter);
        return usDate.format(DATE_FORMATTER);
	}
	
    public static LocalDate timestampToDate(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDate date = instant.atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
        return date;
    }
}
