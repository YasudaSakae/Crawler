package br.com.sinerji.comprascrawler.http.client;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {
	private String result;
	private Header[] headers;
	
	public static HttpResponse fromStringResponse(CloseableHttpResponse response) throws ParseException, IOException {
		var entity = response.getEntity();
        String result = entity != null ? EntityUtils.toString(entity) : null;
        Header[] headers = response.getAllHeaders();
        return new HttpResponse(result, headers);
	}
	
	public static HttpResponse fromNoContentResponse(CloseableHttpResponse response) throws ParseException, IOException {
        Header[] headers = response.getAllHeaders();
        return new HttpResponse(null, headers);
	}
}

