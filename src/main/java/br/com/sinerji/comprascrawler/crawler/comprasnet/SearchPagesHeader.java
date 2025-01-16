package br.com.sinerji.comprascrawler.crawler.comprasnet;

import org.apache.http.Header;

import br.com.sinerji.comprascrawler.http.client.HttpHeader;

public class SearchPagesHeader extends HttpHeader {
	private static final String COOKIE_BASE = "_gid=GA1.3.109038384.1734702819; _ga=GA1.1.368731111.1734553974; _ga_VYH7P8MBQ7=GS1.1.1734706205.3.1.1734707529.0.0.0;";

	public SearchPagesHeader(Header[] contactSearchResponseHeaders, String xCsrfToken) {
		String cookie = createCookie(contactSearchResponseHeaders);
		
		addHeader("cookie", cookie);
		addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		addHeader("host", "contratos.comprasnet.gov.br");
		addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
		addHeader("x-csrf-token", xCsrfToken);
		addHeader("x-requested-with", "XMLHttpRequest");
	}

	private String createCookie(Header[] contactSearchResponseHeaders) {
		String cookie = COOKIE_BASE;
		for (Header header : contactSearchResponseHeaders) {
			System.out.println(header.getName());
			if (header.getName().startsWith("Set-Cookie")) {
				String value = header.getValue().split(";\\s+")[0];
				cookie += " " + value + ";";
			}
		}
		return cookie;
	}
}
