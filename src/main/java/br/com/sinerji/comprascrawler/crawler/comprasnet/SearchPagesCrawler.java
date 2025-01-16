package br.com.sinerji.comprascrawler.crawler.comprasnet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.crawler.Crawler;
import br.com.sinerji.comprascrawler.crawler.CrawlerException;
import br.com.sinerji.comprascrawler.crawler.FatalCrawlerException;
import br.com.sinerji.comprascrawler.http.HttpBot;
import br.com.sinerji.comprascrawler.http.client.HttpHeader;
import br.com.sinerji.comprascrawler.http.client.HttpPostBody;
import br.com.sinerji.comprascrawler.http.client.HttpResponse;
import br.com.sinerji.comprascrawler.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchPagesCrawler extends Crawler {

	private static final String SEARCH_CONTRACTS_URL = "https://contratos.comprasnet.gov.br/transparencia/contratos";
	private static final String SEARCH_PAGES_URL = "https://contratos.comprasnet.gov.br/transparencia/contratos/search";
	private static final String PAGES_DATA_TYPE = "json";
	private static final int MAX_GET_PARAMS_RETRIES = 7;
	private static final int MAX_PAGE = 10307;
	
	private File pagesDir;
	private List<Integer> savedPages;
	
	public SearchPagesCrawler(Config config, HttpBot bot) {
		super(config, bot);
		pagesDir = config.getComprasnetPagesDirFile();
		FileUtil.createDirectoryIfNotExists(pagesDir);
		savedPages = new ArrayList<>();
		for (String file : pagesDir.list()) {
			int page = Integer.valueOf(file.replace("." + PAGES_DATA_TYPE, ""));
			savedPages.add(page);
		}
	}

	@Override
	public void runCrawler() throws ClientProtocolException, IOException {
		var searchContractsResources = getSearchContractsResources();
		HttpHeader header = new SearchPagesHeader(searchContractsResources.headers, searchContractsResources.xCsrfToken);
		int countRetries = 0;
		for (int page = 1; page <= MAX_PAGE; page++) {
			if (savedPages.contains(page)) {
				log.info("Page " + page + " already saved");
				continue;
			}
			log.info("Fetching page " + page);
			HttpPostBody body = new SearchPagesBody(page);
			var response = bot.doPost(SEARCH_PAGES_URL, body, header);
			String result = response.getResult();
			try {
				validateResult(page, result);
			} catch (CrawlerException e) {
				if (countRetries > MAX_GET_PARAMS_RETRIES) {
					throw new FatalCrawlerException(String.format("Cannot get required params after %s retries", countRetries));
				}
				log.info("Fail: " + e.getMessage() + "\nRetrying to get required params");
				searchContractsResources = getSearchContractsResources();
				header = new SearchPagesHeader(searchContractsResources.headers, searchContractsResources.xCsrfToken);
				countRetries += 1;
			}
			File pageDataFile = new File(pagesDir, String.format("%d.%s", page, PAGES_DATA_TYPE));
			FileUtil.writeToFile(pageDataFile, result);
		}
	}
	
	private SearchContractsResources getSearchContractsResources() {
		HttpResponse searchContractsResponse;
		Document searchContractsHtml;
		try {
			searchContractsResponse = bot.doGet(SEARCH_CONTRACTS_URL);
			searchContractsHtml = Jsoup.parse(searchContractsResponse.getResult());
		} catch (Exception e) {
			throw new FatalCrawlerException("Search contract page request failed");
		}

		String xCsrfToken = null;
		for (var meta : searchContractsHtml.getElementsByTag("meta")) {
			if (meta.attr("name").equals("csrf-token")) {
				xCsrfToken = meta.attr("content");
			}
		}
		
		if (xCsrfToken == null) {
			throw new FatalCrawlerException("Could not found the csrf-token in search contracts page");
		}
		
		return new SearchContractsResources(searchContractsResponse.getHeaders(), xCsrfToken);
	}
	
	private void validateResult(int page, String result) {
		if (result == null) {
			throw new CrawlerException("Response is null to page: " + page);
		}
		JsonElement json = JsonParser.parseString(result);
		if (!json.getAsJsonObject().has("data")) {
			throw new CrawlerException("Data not found in response of page: " + page);
		}
		JsonElement data = json.getAsJsonObject().get("data");
		if (data.isJsonNull()) {
			throw new CrawlerException("Data is null in response of page: " + page);
		}
		if (!data.isJsonArray()) {
			throw new CrawlerException("Data is not an array in response of page: " + page);
		}
		JsonArray jsonArray = data.getAsJsonArray();
		if (jsonArray.size() == 0) {
			throw new CrawlerException("Data is an empty array in response of page: " + page);
		}
	}
	
	private record SearchContractsResources(Header[] headers, String xCsrfToken) {
	}
}
