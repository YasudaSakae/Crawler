package br.com.sinerji.comprascrawler.crawler;

import java.util.Arrays;

import br.com.sinerji.comprascrawler.crawler.comprasnet.SearchPagesCrawler;
import br.com.sinerji.comprascrawler.crawler.getfiles.ContractsCrawler;
import br.com.sinerji.comprascrawler.crawler.getfiles.EditaisCrawler;
import br.com.sinerji.comprascrawler.crawler.pncp.ArchivesFromComprasnetCrawler;
import br.com.sinerji.comprascrawler.crawler.pncp.ContractsFromPNCPCrawler;
import br.com.sinerji.comprascrawler.crawler.pncp.EditaisFromPNCPCrawler;
import br.com.sinerji.comprascrawler.crawler.pncp.PNCPSearchHiresCrawler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CrawlerPhase {
	COMPRASNET_SEARCH_PAGES(1, SearchPagesCrawler.class, "Salva o conteúdo das tabelas da página: Consulta contratos no comprasnet (https://contratos.comprasnet.gov.br/transparencia/contratos)"),
    PNCP_SEARCH_PAGES(1, PNCPSearchHiresCrawler.class, "Obtém o conteúdo json em páginas disponível no PNCP (https://pncp.gov.br/app)"),
    GET_ARCHIVES_FROM_COMPRASNET(2, ArchivesFromComprasnetCrawler.class, "Obtém os arquivos vinculados ao contrato dos itens das páginas do comprasnet no PNCP com formato json"),
    GET_ARCHIVES_FROM_PNCP(2, ContractsFromPNCPCrawler.class, "Obtém os arquivos vinculados ao contrato dos itens das páginas do PNCP contratações com formato json"),
    GET_EDITAIS_FROM_PNCP(2, EditaisFromPNCPCrawler.class, "Obtém os arquivos vinculados ao edital dos itens das páginas do PNCP com formato json"),
    CONTRACTS(3, ContractsCrawler.class, "Faz o download dos arquivos do tipo 'Contrato' do PNCP"),
    EDITAIS(3, EditaisCrawler.class, "Faz o download dos arquivos do tipo 'Edital' do PNCP");
	
	private Integer order;
	private Class<? extends Crawler> crawlerClass;
	private String description;
	
	public static CrawlerPhase[] listOrdered() {
		CrawlerPhase[] values = CrawlerPhase.values();
		Arrays.sort(values, (v1, v2) -> v1.order.compareTo(v2.order));
		return values;
	}
	
	@Override
	public String toString() {
		return getPhaseId() + "\n\t" + description;
	}
	
	public String getPhaseId() {
		return name().toLowerCase();
	}
}
