package br.com.sinerji.comprascrawler;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import br.com.sinerji.comprascrawler.util.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Config {
	private static Config CONFIG;
	private static final String UA_FILE_PATH = "user_agents.txt";
	public static final String COMPRASNET_PAGES_DIR = "pages";
	public static final String PNCP_CONTRACTS_PAGES_DIR = "pncp_contacts_hiring_pages";
	public static final String PNCP_EDITAL_PAGES_DIR = "editais";
	public static final String PNCP_ARCHIVE_JSONS_DIR = "pncp_archive_jsons";
	public static final String CONTRACTS_DIR = "contracts";
	public static final String EDITAIS_DIR = "editais";

	private List<String> crawlerPhases;
	private String dataDir;
	
	private int delayToRequest;
	private int maxDelayVariation;
	private boolean restartLogs;
	
	private transient String userAgent;

	private Config() {}

	public static void init(CommandLine cmd) {
		try {
			CONFIG = readConfigFile();
			initDataDir(CONFIG.dataDir);

			initUserAgent();
			
			if (CONFIG.restartLogs) {
				try {
					FileUtil.writeToFile(new File("crawler.log"), "");
				} catch (IOException e) {
					log.warn("crawler.log file not found");
				}
			}
			System.out.println(CONFIG.restartLogs);

			log.info("Config: \n" + CONFIG.toString());
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			log.error("Fail to read config.json file", e);
		}

	}

	private static void initDataDir(String dataDir) {
		FileUtil.createDirectoryIfNotExists(new File(dataDir));
	}

	private static void initUserAgent() {
		CONFIG.userAgent = getRandomUserAgent();
	}

	private static String getRandomUserAgent() {
		List<String> userAgents = FileUtil.readLinesAsList(UA_FILE_PATH);
		Random rand = new Random();
		
		int randIndex = rand.nextInt(userAgents.size());
		return userAgents.get(randIndex);
	}


	private static Config readConfigFile() throws FileNotFoundException {
		Gson gson = new Gson();
		return gson.fromJson(new FileReader("config.json"), Config.class);
	}
	
	public static Config getConfig() {
		if (CONFIG == null) {
			throw new IllegalStateException("Config not initialized");
		}
		
		return CONFIG;
	}
	
	public File getComprasnetPagesDirFile() {
		return new File(dataDir, COMPRASNET_PAGES_DIR);
	}

	public File getPncpArchivesDir() {
		return new File(dataDir, PNCP_ARCHIVE_JSONS_DIR);
	}

	public File getContractsDir() {
		return new File(dataDir, CONTRACTS_DIR);
	}

	public File getEditaisDir() {
		return new File(dataDir, EDITAIS_DIR);
	}
	
	public File getPncpContractsPagesDir() {
		return new File(dataDir, PNCP_CONTRACTS_PAGES_DIR);
	}

	public File getPncpEditalPagesDir() {
		return new File(dataDir, PNCP_EDITAL_PAGES_DIR);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\tphases: ")
				.append(crawlerPhases == null ? "All phases" : crawlerPhases)
				.append("\n\tdataDir: ").append(dataDir);

		sb.append("\n");

		return sb.toString();
	}
}
