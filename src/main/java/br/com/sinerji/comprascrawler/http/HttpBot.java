package br.com.sinerji.comprascrawler.http;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.http.client.HttpClient;
import br.com.sinerji.comprascrawler.http.client.HttpHeader;
import br.com.sinerji.comprascrawler.http.client.HttpPostBody;
import br.com.sinerji.comprascrawler.http.client.HttpResponse;
import lombok.Value;

@Value
public class HttpBot implements Closeable {
	private HttpClient httpClient;
	private String userAgent;
	private int delayToRequest;
	private int maxDelayVariation;
	private Random rand;
	
	public HttpBot(Config config, HttpClient httpClient) {
		this.httpClient = httpClient;
		this.userAgent = config.getUserAgent();
		this.delayToRequest = config.getDelayToRequest();
		this.maxDelayVariation = config.getMaxDelayVariation();
		rand = new Random();
	}

	public HttpResponse doPost(String url, HttpPostBody body, HttpHeader header)
			throws ClientProtocolException, IOException {
		prepareRequest(header);
		return httpClient.doPost(url, body, header);
	}

	public HttpResponse doGet(String url)
			throws ClientProtocolException, IOException {
		HttpHeader header = new HttpHeader();
		prepareRequest(header);
		return httpClient.doGet(url, header);
	}

	public HttpResponse doGet(String url, HttpHeader header)
			throws ClientProtocolException, IOException {
		prepareRequest(header);
		return httpClient.doGet(url, header);
	}
	
	public void downloadFile(String url, String targetPath)
			throws ClientProtocolException, IOException {
		HttpHeader header = new HttpHeader();
		prepareRequest(header);
		httpClient.downloadFile(url, targetPath, header);
	}

	private void prepareRequest(HttpHeader header) {
		header.addHeader("user-agent", userAgent);
		sleep();
	}

	private void sleep() {
		int variation = rand.nextInt(maxDelayVariation + 1);
		try {
			TimeUnit.MILLISECONDS.sleep(delayToRequest + variation);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Fail to delay request", e);
		}
	}
	
	@Override
	public void close() throws IOException {
		httpClient.close();
	}
}
