package br.com.sinerji.comprascrawler.http.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class HttpPostBody {

	protected List<NameValuePair> params = new ArrayList<NameValuePair>();

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public HttpEntity getHttpEntity() {
		try {
			return new UrlEncodedFormEntity(params, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
