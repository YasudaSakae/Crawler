package br.com.sinerji.comprascrawler.crawler.getfiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.crawler.Crawler;
import br.com.sinerji.comprascrawler.crawler.CrawlerException;
import br.com.sinerji.comprascrawler.crawler.FetchChecker;
import br.com.sinerji.comprascrawler.http.HttpBot;
import br.com.sinerji.comprascrawler.util.FileUtil;
import br.com.sinerji.comprascrawler.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContractsCrawler extends Crawler {
	private static final String ARCHIVE_TYPE_ATTR_NAME = "tipoDocumentoNome";
	private static final String ARCHIVE_URL_ATTR_NAME = "url";
	private static final String CONTRACT_FILE_TYPE = "pdf";
	
	private final FetchChecker fetchChecker;
	private final File pncpArchivesDir;
	private final File contractsDir;
	
	public ContractsCrawler(Config config, HttpBot bot) {
		super(config, bot);
		pncpArchivesDir = config.getPncpArchivesDir();
		contractsDir = config.getContractsDir();
		FileUtil.createDirectoryIfNotExists(contractsDir);
		fetchChecker = new FetchChecker(config, "fetched_contracts.txt");
	}

	@Override
	protected void runCrawler() throws ClientProtocolException, IOException {
		List<File> pncpArchives = FileUtil.listDirFilesOrdered(pncpArchivesDir);
		List<ContractParams> contractUrls = getContractParamsList(pncpArchives);
		int counter = fetchChecker.getNumOfFetchedFiles();
		for (var params : contractUrls) {
			String contractId = params.id;
			if (fetchChecker.contains(params.id)) {
				log.info("Contract already fetched: " + contractId);
				continue;
			}
			File targetFile = new File(contractsDir, String.format("%s.%s", contractId, CONTRACT_FILE_TYPE));
			log.info("Downloading contract: " + contractId);
			bot.downloadFile(params.url, targetFile.getAbsolutePath());
			fetchChecker.updateFetchedSetFile(contractId);
			counter += 1;
			if (counter % 20 == 0) {
				log.info(String.format("%d/%d fetched", counter, contractUrls.size()));
			}
		}
	}

	private List<ContractParams> getContractParamsList(List<File> pncpArchives) {
		log.info("Extracting contract URLs from archive jsons...");
		List<ContractParams> contractUrls = new ArrayList<>();
		int counter = 0;
		for (File pncpArchivesFile : pncpArchives) {
			String jsonStr = FileUtil.readFile(pncpArchivesFile);
			JsonElement json = Util.parseLenientJsonString(jsonStr);
			ContractParams url = getParams(pncpArchivesFile, json);
			if (url != null) {
				try {
					contractUrls.add(url);
				} catch (CrawlerException e) {
					log.warn("", e);
				}
			}
			counter += 1;
			if (counter % 500 == 0) {
				log.info(String.format("%d/%d params extracted", counter, pncpArchives.size()));
			}
		}
		return contractUrls;
	}

	private ContractParams getParams(File pncpArchivesFile, JsonElement json) {
		for (JsonElement archiveJson : json.getAsJsonArray()) {
			JsonObject archiveJsonObj = archiveJson.getAsJsonObject();
			if (!archiveJsonObj.has(ARCHIVE_TYPE_ATTR_NAME)) {
				throw new CrawlerException("Archive json has not an attribute: " + ARCHIVE_TYPE_ATTR_NAME);
			}
			JsonElement archiveTypeJson = archiveJsonObj.get(ARCHIVE_TYPE_ATTR_NAME);
			String archiveType = archiveTypeJson.getAsString();
			if (archiveType.equals("Contrato")) {
				if (!archiveJsonObj.has(ARCHIVE_URL_ATTR_NAME)) {
					throw new CrawlerException("Archive json has not an attribute: " + ARCHIVE_URL_ATTR_NAME);
				}
				JsonElement urlJson = archiveJsonObj.get("url");
				String id = FileUtil.getFileNameWithoutExtension(pncpArchivesFile);
				String url = urlJson.getAsString();
				return new ContractParams(id, url);
			}
		}
		return null;
	}
	
	private record ContractParams(String id, String url) {};
}
