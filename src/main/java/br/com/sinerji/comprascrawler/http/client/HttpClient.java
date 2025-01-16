package br.com.sinerji.comprascrawler.http.client;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public interface HttpClient extends Closeable {
	HttpResponse doPost(String url, HttpPostBody body) throws ClientProtocolException, IOException;

	HttpResponse doPost(String url, HttpPostBody body, HttpHeader header) throws ClientProtocolException, IOException;

	HttpResponse doGet(String url) throws ClientProtocolException, IOException;

	HttpResponse doGet(String url, HttpHeader header) throws ClientProtocolException, IOException;
	
	HttpResponse downloadFile(String url, String targetPath, HttpHeader header) throws ClientProtocolException, IOException;
}
