package br.com.sinerji.comprascrawler.http.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpHeader {
	private Map<String, String> headers = new HashMap<>();

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	public Set<Entry<String, String>> getHeaders() {
		return headers.entrySet();
	}
}
