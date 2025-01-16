package br.com.sinerji.comprascrawler.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Util {
	public static final String EXTRACT_NUMBER_REG = "\\b(?!\\d+%)(\\d+(,\\d+)?)\\b";
	
	
	public static String removeAccents(String str) {
	    return Normalizer.normalize(str, Normalizer.Form.NFD)
	    		.replaceAll("[^\\p{ASCII}]", "");
	}
	
	public static String encodeURL(String url) {
		return url.replace(" ", "%20");
	}
	
	public static BigDecimal formattedNumberToBigDecimal(String formattedNumber, BigDecimal defaultValue) {
		if (formattedNumber == null) {
			return defaultValue;
		}
		return formattedNumberToBigDecimal(formattedNumber);
	}

	public static BigDecimal formattedNumberToBigDecimal(String formattedNumber) {
		String decimalStr = formattedNumber.trim().replaceAll("\\.", "").replace(",", ".");
		return new BigDecimal(decimalStr);
	}

	public static Float formattedNumberToFloat(String formattedNumber, Float defaultValue) {
		if (formattedNumber == null) {
			return defaultValue;
		}
		return formattedNumberToFloat(formattedNumber);
	}

	public static Float formattedNumberToFloat(String formattedNumber) {
		return Float.parseFloat(formattedNumber.trim().replaceAll("\\.", "").replace(",", "."));
	}

	public static Long formattedNumberToLong(String formattedNumber) {
		return Long.parseLong(formattedNumber.trim().replaceAll("\\.", ""));
	}
	
	public static String unescapeHtml(String html) {
		return StringEscapeUtils.unescapeHtml4(html);
	}
	
	public static String nullIfBlank(String str) {
		return str == null || str.isBlank() ? null : str;
	}
	
	public static String padLeft(String inputString, int length, char padChar) {
	    if (inputString.length() >= length) {
	        return inputString;
	    }
	    StringBuilder sb = new StringBuilder();
	    while (sb.length() < length - inputString.length()) {
	        sb.append(padChar);
	    }
	    sb.append(inputString);

	    return sb.toString();
	}
	
	public static String getInParenthesesText(String str) {
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
		if (m.find()) {
			return m.group().replaceAll("\\(|\\)", "");
		}

		return null;
	}
	
	public static boolean isNullOrEmpty(final String str) {
		return str == null || str.isEmpty();
	}

	public static boolean isNullOrEmpty(final Collection<?> collention) {
		return collention == null || collention.isEmpty();
	}

	public static String generateRandomNumber(int min, int max) {
		Random r = new Random();
		return String.valueOf(r.nextInt((max - min) + 1) + min);
	}
	
	public static <K, V> Map<K, V> generateMap(List<V> list, Function<V, K> keyExtractor) {
		Map<K, V> map = new HashMap<K, V>();
		list.forEach((V value) -> {
			K key = keyExtractor.apply(value);
			map.put(key, value);
		});
		return map;
	}

	public static <K, V> Map<K, List<V>> generateGroupedMap(List<V> list, Function<V, K> keyExtractor) {
		Map<K, List<V>> map = new LinkedHashMap<>();
		list.forEach((V value) -> {
			K key = keyExtractor.apply(value);
			List<V> values = map.get(key);
			if (values == null) {
				values = new LinkedList<>();
				map.put(key, values);
			}
			values.add(value);
		});
		return map;
	}

	public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
	    return new HashSet<T>(list1).equals(new HashSet<T>(list2));
	}
	
	public static List<Long> stringToLongList(String str, String delimiter) {
		if (isNullOrEmpty(str)) {
			return null;
		}
		List<Long> list = new LinkedList<>();
		for (String value : str.split(delimiter)) {
			list.add(Long.parseLong(value));
		}
		return list;
	}

	public static List<Integer> stringToIntegerList(String str, String delimiter) {
		if (isNullOrEmpty(str)) {
			return null;
		}
		List<Integer> list = new LinkedList<>();
		for (String value : str.split(delimiter)) {
			list.add(Integer.parseInt(value));
		}
		return list;
	}

	public static String longListToString(List<Long> list, String delimiter) {
		return list.stream().map(value -> value.toString()).collect(Collectors.joining(delimiter));
	}
	
	public static String getClassPackageName(Class<?> clazz) {
		return clazz.getPackage().getName();
	}

	public static String capitalizeFirstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
    public static String joinArray(String[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
    
    public static JsonElement parseLenientJsonString(String str) {
    	JsonReader reader = new JsonReader(new StringReader(str));
    	reader.setLenient(true);
    	return JsonParser.parseReader(reader);
    }
}
