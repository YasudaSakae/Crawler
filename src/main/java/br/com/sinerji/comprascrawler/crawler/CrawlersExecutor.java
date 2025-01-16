package br.com.sinerji.comprascrawler.crawler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.http.HttpBot;
import br.com.sinerji.comprascrawler.http.client.HttpClient;
import br.com.sinerji.comprascrawler.http.client.HttpClientImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrawlersExecutor {
	
	private List<Crawler> crawlersToExecute = new ArrayList<>();
	private HttpBot bot;
	
	public CrawlersExecutor(Config config) {
		HttpClient httpClient = new HttpClientImpl();
		bot = new HttpBot(config, httpClient);
		validateSelectedPhases(config);
		setPhasesToExecute(config);
	}

	private void validateSelectedPhases(Config config) {
		List<String> selectedPhases = config.getCrawlerPhases();
		List<String> phaseIds = List.of(CrawlerPhase.values()).stream()
				.map(p -> p.getPhaseId())
				.toList();
		for (String phaseId : selectedPhases) {
			if (!phaseIds.contains(phaseId)) {
				throw new IllegalArgumentException("Invalid crawler phase id: " + phaseId);
			}
		}
		log.info("Selected phases: " + selectedPhases);
		log.info("Available phases: " + phaseIds);
	}

	private void setPhasesToExecute(Config config) {
		List<String> selectedPhases = config.getCrawlerPhases();
		var phases = CrawlerPhase.listOrdered();
		for (var phase : phases) {
			String phaseName = phase.name().toLowerCase();
			if (selectedPhases == null || selectedPhases.contains(phaseName)) {
				try {
					Object instance = phase.getCrawlerClass()
							.getDeclaredConstructor(Config.class, HttpBot.class)
							.newInstance(config, bot);
					crawlersToExecute.add((Crawler) instance);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new IllegalStateException("Internal error: fail to instantiate crawler", e);

				}
			}
		}
	}

	public void execute() {
		try {
			for (Crawler crawler : crawlersToExecute) {
				log.info("Running " + crawler.getClass().getSimpleName() + "...");
				crawler.execute();
			}
		} finally {
			try {
				bot.close();
			} catch (IOException e) {
				log.warn("Could not close http bot.", e);
			}
			log.info("Finished!");
		}
	}
}
