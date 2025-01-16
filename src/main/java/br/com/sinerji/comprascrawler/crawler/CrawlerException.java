package br.com.sinerji.comprascrawler.crawler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrawlerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	public CrawlerException(String message) {
		this.message = message;
	}
}
