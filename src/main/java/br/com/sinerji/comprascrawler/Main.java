package br.com.sinerji.comprascrawler;

import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import br.com.sinerji.comprascrawler.crawler.CrawlerPhase;
import br.com.sinerji.comprascrawler.crawler.CrawlersExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	public static void main(String[] args) throws ParseException {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(CommandOptions.getOptions(), args);

		if (cmd.hasOption(CommandOptions.HELP)) {
			printHelp();
			return;
		}
		
		executeCrawlers(cmd);
	}

	public static void printHelp() {
		log.info("--------- crawler phases ---------");
		for (var phase : CrawlerPhase.listOrdered()) {
			log.info("- " + phase);
		}
		log.info("\n");
		CommandOptions.printCommandsHelp();
	}
	
	private static void executeCrawlers(CommandLine cmd) {
		Config.init(cmd);
		Config config = Config.getConfig();
		new CrawlersExecutor(config).execute();
	}
}
