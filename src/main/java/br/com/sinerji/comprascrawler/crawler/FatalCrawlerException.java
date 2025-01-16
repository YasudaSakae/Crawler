package br.com.sinerji.comprascrawler.crawler;

import lombok.Getter;

@Getter
public class FatalCrawlerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	public FatalCrawlerException(String message) {
		this.message = message;
	}
}
