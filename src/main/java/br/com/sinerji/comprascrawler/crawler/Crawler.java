package br.com.sinerji.comprascrawler.crawler;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.http.HttpBot;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Crawler {
	protected final Config config;
	protected final HttpBot bot;
		
	public Crawler(Config config, HttpBot bot) {
		this.config = config;
		this.bot = bot;
	}

	public final void execute() {
		try {
			runCrawler();
		} catch (ClientProtocolException e) {
			log.warn("Fail to request http", e);
		} catch (IOException e) {
			throw new IllegalStateException("Fail to write file", e);
		}
	}
	
	protected abstract void runCrawler() throws ClientProtocolException, IOException;
}
