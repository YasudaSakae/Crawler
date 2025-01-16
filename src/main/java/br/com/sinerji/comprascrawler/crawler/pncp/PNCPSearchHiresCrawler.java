package br.com.sinerji.comprascrawler.crawler.pncp;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.crawler.Crawler;
import br.com.sinerji.comprascrawler.crawler.CrawlerException;
import br.com.sinerji.comprascrawler.crawler.FatalCrawlerException;
import br.com.sinerji.comprascrawler.http.HttpBot;
import br.com.sinerji.comprascrawler.http.client.HttpResponse;
import br.com.sinerji.comprascrawler.util.FileUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PNCPSearchHiresCrawler extends Crawler {
	
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	private enum Order {
		ASC("data"),
		DESC("-data");
		private String value;
	}
	
	private static final String SEARCH_HIRES_URL = "https://pncp.gov.br/api/search";
	private static final int PAGE_SIZE = 100;
	private static final int[] HIRING_MODALITY_IDS = { 4, 5, 12, 8, 9, 1, 13, 10, 6, 7 };
	private static final String PAGES_DATA_TYPE = "json";
	private static final int MAX_RESULT_ITEMS = 10_000;
	private static final int MAX_PAGES = 100_000;

	private File pagesDir;
	
	public PNCPSearchHiresCrawler(Config config, HttpBot bot) {
		super(config, bot);
		pagesDir = config.getPncpEditalPagesDir();
		FileUtil.createDirectoryIfNotExists(pagesDir);
	}

	@Override
	protected void runCrawler() throws ClientProtocolException, IOException {
		List<SearchParams> paramsList = getSearchParamsList();
		int count = 0;
		for (SearchParams params : paramsList) {
			int total = fetchPages(Order.ASC, params);
			if (total == 0) {
				log.warn("Total is equals zero");
			} else if (total > MAX_RESULT_ITEMS) {
				fetchPages(Order.DESC, params);
			}
			count += 1;
			log.info(String.format("%d of %d params processed", count, paramsList.size()));
		}
	}

	private int fetchPages(Order order, SearchParams params) throws IOException {
		boolean hasPages = true;
		int page = 1;
		Integer total = null;
		String baseUrl = getBaseUrl(order, params);
		while (hasPages && page < MAX_PAGES) {
			String url = baseUrl + "&pagina=" + page;
			log.info(String.format("Fetching page url: %s", url));
			HttpResponse response = bot.doGet(url);
			String result = new String(response.getResult().getBytes(), "UTF-8");
			try {
				if (!maxPageAllowedReached(page, result)) {
					JsonObject jsonObj = getResultJsonObject(page, result);
					hasPages = hasMorePages(page, jsonObj);
					if (hasPages) {
						if (total == null) {
							total = getItemsTotal(page, jsonObj);
							if (total == 0) {
								return total;
							}
						}
						File targetFile = createTargetFile(order, params, page);
						FileUtil.writeToFile(targetFile, result);
						page += 1;
					}
				} else {
					return total;
				}
			} catch (CrawlerException e) {
				log.warn("", e);
				page += 1;
			}
		}
		return total != null ? total : 0;
	}

	private List<SearchParams> getSearchParamsList() {
		List<SearchParams> paramsList = new ArrayList<>();
		for (UF uf : UF.values()) {
			for (int hiringModalityId : HIRING_MODALITY_IDS) {
				SearchParams params = new SearchParams(uf.name(), hiringModalityId);
				paramsList.add(params);
			}
		}
		return paramsList;
	}
	
	private String getBaseUrl(Order order, SearchParams params) {
		try {
			URIBuilder uriBuilder = new URIBuilder(SEARCH_HIRES_URL);
			uriBuilder.addParameter("tipos_documento", "edital");
			uriBuilder.addParameter("ordenacao", order.value);
			uriBuilder.addParameter("status", "todos");
			uriBuilder.addParameter("tam_pagina", String.valueOf(PAGE_SIZE));
			uriBuilder.addParameter("ufs", params.uf);
			uriBuilder.addParameter("modalidade_contratacao", String.valueOf(params.hiringModalityId));
			String url = uriBuilder.build().toString();
			return url;
		} catch (Exception e) {
			throw new FatalCrawlerException("Fail to build a pncp hiring search URL");
		}
	}
	
	private boolean maxPageAllowedReached(int page, String result) {
		if (result == null) {
			throw new CrawlerException("Response is null to page: " + page);
		}

		return result.contains("Janela de resultados muito grande");
	}

	private JsonObject getResultJsonObject(int page, String result) {
		if (result == null) {
			throw new CrawlerException("Response is null to page: " + page);
		}

		try (var reader = new StringReader(result);
			var jsonReader = new JsonReader(reader)) {
			jsonReader.setLenient(true); // Permite JSON malformado
			JsonElement json = JsonParser.parseReader(jsonReader);
			if (!json.isJsonObject()) {
				throw new CrawlerException("Response data is not a valid JSON object for page: " + page);
			}
			return json.getAsJsonObject();
		} catch (Exception e) {
			throw new CrawlerException("Failed to parse JSON response for page: " + page);
		}
	}
	
	private boolean hasMorePages(int page, JsonObject resultJsonObj) {
		if (!resultJsonObj.has("items")) {
			throw new CrawlerException("Response data has not itens attribute in page: " + page);
		}
		JsonElement itemsJson = resultJsonObj.get("items");
		if (!itemsJson.isJsonArray()) {
			throw new CrawlerException("Items attribute is not a array in page: " + page);
		}
		return itemsJson.getAsJsonArray().size() > 0;
	}

	private Integer getItemsTotal(int page, JsonObject jsonObj) {
		if (!jsonObj.has("total")) {
			throw new CrawlerException("Response data has not total attribute in page: " + page);
		}
		return jsonObj.get("total").getAsInt();
	}
	
	private File createTargetFile(Order order, SearchParams params, int page) {
		String fileId = String.format("%s-%s-%d_%d", order.name(), params.uf, params.hiringModalityId, page);
		String FileName = String.format("%s.%s", fileId, PAGES_DATA_TYPE);
		return new File(pagesDir, FileName);
	}
	
	private record SearchParams(String uf, int hiringModalityId) {
	}
}
