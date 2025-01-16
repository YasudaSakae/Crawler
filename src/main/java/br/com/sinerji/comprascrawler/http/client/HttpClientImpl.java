package br.com.sinerji.comprascrawler.http.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientImpl implements HttpClient {
	private CloseableHttpClient httpClient;
	
	public HttpClientImpl() {
		httpClient = HttpClients.createDefault();
	}
	
	@Override
	public HttpResponse doPost(String url, HttpPostBody body) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);

		request.setEntity(body.getHttpEntity());
		
		return executeRequest(request);
	}

	@Override
	public HttpResponse doPost(String url, HttpPostBody body, HttpHeader header)
			throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);

		request.setEntity(body.getHttpEntity());
		
		setHeaders(header, request);

		return executeRequest(request);
	}

	@Override
	public HttpResponse doGet(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);

		return executeRequest(request);
	}

	@Override
	public HttpResponse doGet(String url, HttpHeader header)
			throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);

		setHeaders(header, request);
		
		return executeRequest(request);
	}

	@Override
	public HttpResponse downloadFile(String url, String targetPath, HttpHeader header)
			throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);

		setHeaders(header, request);
		
		File outputFile = new File(targetPath);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			var entity = response.getEntity();
			try (InputStream is = entity.getContent();
					OutputStream os = new FileOutputStream(outputFile)) {
				IOUtils.copy(is, os);
			}
			
			return HttpResponse.fromNoContentResponse(response);
		}
	}

	@Override
	public void close() throws IOException {
		httpClient.close();
	}
	
	private HttpResponse executeRequest(HttpUriRequest request) throws IOException, ClientProtocolException {
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			return HttpResponse.fromStringResponse(response);
		}
	}
	
	private void setHeaders(HttpHeader header, HttpUriRequest request) {
		header.getHeaders().forEach(entry -> {
			request.addHeader(entry.getKey(), entry.getValue());
		});
	}
}
