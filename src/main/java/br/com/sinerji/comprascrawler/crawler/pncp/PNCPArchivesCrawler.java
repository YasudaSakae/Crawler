package br.com.sinerji.comprascrawler.crawler.pncp;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.crawler.Crawler;
import br.com.sinerji.comprascrawler.crawler.CrawlerException;
import br.com.sinerji.comprascrawler.crawler.FetchChecker;
import br.com.sinerji.comprascrawler.http.HttpBot;
import br.com.sinerji.comprascrawler.http.client.HttpResponse;
import br.com.sinerji.comprascrawler.util.FileUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PNCPArchivesCrawler extends Crawler {
	protected static final int SMALL_CODE_LENGTH = 6;

	// Real url
	//	https://pncp.gov.br/api/pncp/v1/orgaos/26994558000123/contratos/2024/000086/arquivos?pagina=1&tamanhoPagina=5
	private static final String PNCP_ARCHIVES_URL_FORMAT = "https://pncp.gov.br/api/pncp/v1/orgaos/%s/edital/%s/%s/arquivos?pagina=1&tamanhoPagina=500";
	private static final String PNCP_ARCHIVES_FILE_TYPE = "json";
	
	private final FetchChecker fetchChecker;
	
	private final File pncpArchivesDir;
	
	public PNCPArchivesCrawler(Config config, HttpBot bot) {
		super(config, bot);
		pncpArchivesDir = config.getPncpArchivesDir();
		FileUtil.createDirectoryIfNotExists(pncpArchivesDir);
		fetchChecker = new FetchChecker(config, "fetched_pncp_archives.txt");
	}

	@Override
	protected void runCrawler() throws ClientProtocolException, IOException {
		Set<PncpArchivesParams> pncpArchivesParamsList = getPncpArchivesParamsSet();
		int counter = 0;
		for (var params : pncpArchivesParamsList) {
			fetchPncpArchive(params);
			counter += 1;
			if (counter % 20 == 0) {
				log.info(String.format("%d/%d fetched", counter, pncpArchivesParamsList.size()));
			}
		}
	}

	protected abstract Set<PncpArchivesParams> getPncpArchivesParamsSet();
	
	private void fetchPncpArchive(PncpArchivesParams params) throws IOException {
		String id = params.getId();
		File pncpArchivesFile = new File(pncpArchivesDir, String.format("%s.%s", id, PNCP_ARCHIVES_FILE_TYPE));
		if (pncpArchivesFile.exists() || fetchChecker.contains(id)) {
			log.info("PNCP files for contract already exists: " + id);
			return;
		}
		log.info("Fetching PNCP archives for contract: " + id);
		HttpResponse response = bot.doGet(params.getPncpArchvesUrl());
		fetchChecker.updateFetchedSetFile(id);
		String result = response.getResult();
		if (result != null) {
			try {
				validateResult(result);
			} catch (CrawlerException e) {
				log.warn("Invalid result: " + e.getMessage());
				return;
			}
			FileUtil.writeToFile(pncpArchivesFile, result);
		}
	}

	private void validateResult(String result) throws CrawlerException {
		JsonElement json = JsonParser.parseString(result);
		if (!json.isJsonArray()) {
			throw new CrawlerException("Response is not an array");
		}
	}
	
	@Builder
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	protected static class PncpArchivesParams {
    	private final String largeCode;
    	private final String year;
    	private String smallCode;
    	
    	public String getPncpArchvesUrl() {
    		String formattedSmallCode = getFormattedSmallCode();
    		return String.format(PNCP_ARCHIVES_URL_FORMAT, largeCode, year, formattedSmallCode);
    	}
    	
    	public String getId() {
    		String formattedSmallCode = getFormattedSmallCode();
    		return String.format("%s-%s-%s", largeCode, formattedSmallCode, year);
    	}
    	
    	private String getFormattedSmallCode() {
    		return StringUtils.leftPad(smallCode, SMALL_CODE_LENGTH, '0');
    	}

		@Override
		public int hashCode() {
			return Objects.hash(getId());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PncpArchivesParams other = (PncpArchivesParams) obj;
			return Objects.equals(getId(), other.getId());
		}
	}
}
