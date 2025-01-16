package br.com.sinerji.comprascrawler.crawler;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.util.FileUtil;

public class FetchChecker {

	private final File fecthedSetFile;
	
	private final Set<String> fetchedSet;
	
	public FetchChecker(Config config, String fecthedListFileName) {
		this.fecthedSetFile = new File(config.getDataDir(), fecthedListFileName);
		FileUtil.createFileIfNotExists(fecthedSetFile);
		List<String> fetchedList = FileUtil.readLinesAsList(fecthedSetFile);
		this.fetchedSet = new HashSet<String>(fetchedList);
	}
	
	public void updateFetchedSetFile(String value) throws IOException {
		FileUtil.appendToFile(fecthedSetFile, value + "\n");
	}
	
	public boolean contains(String value) {
		return this.fetchedSet.contains(value);
	}
	
	public int getNumOfFetchedFiles() {
		return fetchedSet.size();
	}
}
