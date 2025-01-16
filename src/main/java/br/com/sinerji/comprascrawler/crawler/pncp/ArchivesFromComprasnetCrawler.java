package br.com.sinerji.comprascrawler.crawler.pncp;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.http.HttpBot;
import br.com.sinerji.comprascrawler.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArchivesFromComprasnetCrawler extends PNCPArchivesCrawler {
	private final File pagesDir;
	
	public ArchivesFromComprasnetCrawler(Config config, HttpBot bot) {
		super(config, bot);
		pagesDir = config.getComprasnetPagesDirFile();
	}

	@Override
	protected Set<PncpArchivesParams> getPncpArchivesParamsSet() {
		log.info("Extracting PNCP archives params from pages...");
		Set<PncpArchivesParams> pncpArchivesParamsList = new HashSet<>();
		List<File> pagesDirFiles = FileUtil.listDirFilesOrdered(pagesDir);
		int counter = 0;
		for (File pageFile : pagesDirFiles) {
			String pageContent = FileUtil.readFile(pageFile);
			Pattern pattern = Pattern.compile("https:\\\\/\\\\/pncp\\.gov\\.br\\\\/app\\\\/contratos\\\\/\\d+\\\\/\\d+\\\\/\\d+");
		    Matcher matcher = pattern.matcher(pageContent);
		    while(matcher.find()) {
		    	String pncpLink = matcher.group();
		    	String[] split = pncpLink.split("contratos\\\\/", 2);
		    	String codes = split[1];
		    	String[] codesSplit = codes.split("\\\\/", 3);
		    	String largeCode = codesSplit[0];
		    	String year = codesSplit[1];
		    	String smallCode = codesSplit[2];
		    	PncpArchivesParams params = PncpArchivesParams.builder()
		    			.largeCode(largeCode)
		    			.smallCode(smallCode)
		    			.year(year)
		    			.build();
		    	pncpArchivesParamsList.add(params);
		    }
		    counter += 1;
		    if (counter % 50 == 0) {
		    	log.info(String.format("%d/%d of pages processed", counter, pagesDirFiles.size()));
		    }
		}
		return pncpArchivesParamsList;
	}
}
