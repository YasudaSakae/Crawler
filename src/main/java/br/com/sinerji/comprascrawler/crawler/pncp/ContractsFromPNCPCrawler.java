package br.com.sinerji.comprascrawler.crawler.pncp;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.http.HttpBot;

public class ContractsFromPNCPCrawler extends ArchivesFromPNCPCrawler {

    public ContractsFromPNCPCrawler(Config config, HttpBot bot) {
        super(config, bot);
    }
    
    @Override
    protected String getTypeFilter() {
        return "contrato";
    }
}
