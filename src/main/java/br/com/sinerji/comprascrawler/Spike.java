package br.com.sinerji.comprascrawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;

import br.com.sinerji.comprascrawler.crawler.comprasnet.SearchPagesBody;
import br.com.sinerji.comprascrawler.crawler.comprasnet.SearchPagesHeader;
import br.com.sinerji.comprascrawler.http.client.HttpClient;
import br.com.sinerji.comprascrawler.http.client.HttpClientImpl;
import br.com.sinerji.comprascrawler.http.client.HttpPostBody;


public class Spike {
	public Spike(String[] args) {
	}

	public static void main(String[] args) throws Throwable {
//		pages();
//		consultaContratos();
//		consultaContratosEListaPaginas();
		baixarContrato();
	}
	
	public static void baixarContrato() throws Throwable {
		var httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(
				"https://pncp.gov.br/pncp-api/v1/orgaos/26994558000123/contratos/2024/000139/arquivos/4");
		File outputFile = new File("contrato.pdf");
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			var entity = response.getEntity();

			try (InputStream inputStream = entity.getContent();
					FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
				IOUtils.copy(inputStream, fileOutputStream);
//
//				byte[] buffer = new byte[1024];
//				int bytesRead;
//				while ((bytesRead = inputStream.read(buffer)) != -1) {
//					fileOutputStream.write(buffer, 0, bytesRead);
//				}
//
				System.out.println("Arquivo PDF baixado com sucesso: " + outputFile.getAbsolutePath());
			}
		}
	}
	
	public static void contratoArquivos() throws Throwable {
		HttpClient httpClient = new HttpClientImpl();
		var res = httpClient.doGet("https://pncp.gov.br/api/pncp/v1/orgaos/26994558000123/contratos/2024/000139/arquivos?pagina=1&tamanhoPagina=5");
		System.out.println(res.getResult());
		
		httpClient.close();

	}
	
	// nao precisa
	public static void portalNacional() throws Throwable {
		HttpClient httpClient = new HttpClientImpl();
		var res = httpClient.doGet("https://pncp.gov.br/app/contratos/26994558000123/2024/000139");
		System.out.println(res.getResult());
		
		httpClient.close();


		
	}
	
	public static void details() throws Throwable {
		HttpClient httpClient = new HttpClientImpl();
		var res = httpClient.doGet("https://contratos.comprasnet.gov.br/transparencia/contratos/295514");
		System.out.println(res.getResult());
		
		
		httpClient.close();

	}
	
	
	public static void pages() throws Throwable {
		HttpPostBody pb = new SearchPagesBody(1);
		
		String xsrfToken = "eyJpdiI6Ik44N1g2RXpZNlJuRGp4OEFEUkhyQlE9PSIsInZhbHVlIjoiN2lkN3poTzdhZ0NORklcL0VpZklER2hWdmtySm9CcnU1UDRud2kxNHVHR2JZek1jckZ5ZlZXWDgzRU9CWHRHaVciLCJtYWMiOiI4YTgxNThlYmUyMzMyYTkxODAyNTcwODc5MTE1ZDBjOTMzMzViZTlhNWEwNzgyZGVjMjc4OGJhM2RlOWM0MDNlIn0%3D";
		String xCsrfToken = "6q5bEsHUfm3ZG8Z153tKt9XYtkjgSbR1Ehmr5e6N";
		String session = "eyJpdiI6ImN6dFpBYnFsZGc3cEV4YjdcL3Nmb1dnPT0iLCJ2YWx1ZSI6IitIWkZBcjlLcEEybzBySXhFUFM1UnJENjE5NTErSjAwOFVKYkRwSCtjdjhCM1VCS05hRmltRXF3NHBQc1wvS0haIiwibWFjIjoiYWFhMjFlYzA2NDM1Mjg5NDdjZjAyODJkOTgwMmQyMjgzMTA2ZmM2ZTliZGMzYjEyNTFkYjYzYzg5NjAzZWM0NiJ9; TS014e5f26=01fef04d4e175b2e188a4b73665a06ca1630a524a803c07d5fe4e64d21fda1516bebfbff0d6ace34c79801e1896ea42a955cb797bdb959ef12a9120c541987d5e8e67696e02626c9d00a9ab128e14e9437741c47ec";
		String cookie = "_gid=GA1.3.109038384.1734702819; _ga=GA1.1.368731111.1734553974; XSRF-TOKEN=__XSRF-TOKEN__; contratosgovbr_session=__SESSION__; TS014e5f26=01fef04d4e6685f3cce9a8a4d495802c4540640ae373a8fe27165890032650e8f1b14179c668b41777cd823aba03e654c3174c0dd5181b9a8ff74309d773957c1e2a6ca619a87d4a5ee014e50a310e108fcfbeace5; _ga_VYH7P8MBQ7=GS1.1.1734706205.3.1.1734707529.0.0.0";
		
//		var h = new ListPagesHeaders(xsrfToken, xCsrfToken, session);
		
//		HttpHeader h = new HttpHeader();
//		h.addHeader("cookie", cookie.replace("__XSRF-TOKEN__", xsrfToken).replace("__SESSION__", session));
//		h.addHeader("cookie", "_gid=GA1.3.161923804.1735309991; _gat_gtag_UA_177938763_1=1; XSRF-TOKEN=eyJpdiI6Ik44N1g2RXpZNlJuRGp4OEFEUkhyQlE9PSIsInZhbHVlIjoiN2lkN3poTzdhZ0NORklcL0VpZklER2hWdmtySm9CcnU1UDRud2kxNHVHR2JZek1jckZ5ZlZXWDgzRU9CWHRHaVciLCJtYWMiOiI4YTgxNThlYmUyMzMyYTkxODAyNTcwODc5MTE1ZDBjOTMzMzViZTlhNWEwNzgyZGVjMjc4OGJhM2RlOWM0MDNlIn0%3D; contratosgovbr_session=eyJpdiI6ImN6dFpBYnFsZGc3cEV4YjdcL3Nmb1dnPT0iLCJ2YWx1ZSI6IitIWkZBcjlLcEEybzBySXhFUFM1UnJENjE5NTErSjAwOFVKYkRwSCtjdjhCM1VCS05hRmltRXF3NHBQc1wvS0haIiwibWFjIjoiYWFhMjFlYzA2NDM1Mjg5NDdjZjAyODJkOTgwMmQyMjgzMTA2ZmM2ZTliZGMzYjEyNTFkYjYzYzg5NjAzZWM0NiJ9; TS014e5f26=01fef04d4e175b2e188a4b73665a06ca1630a524a803c07d5fe4e64d21fda1516bebfbff0d6ace34c79801e1896ea42a955cb797bdb959ef12a9120c541987d5e8e67696e02626c9d00a9ab128e14e9437741c47ec; _ga=GA1.1.368731111.1734553974; _ga_VYH7P8MBQ7=GS1.1.1735335632.7.1.1735335641.0.0.0");
//		h.addHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
//		h.addHeader("host", "contratos.comprasnet.gov.br");
//		h.addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
//		h.addHeader("x-csrf-token", xCsrfToken);
//		h.addHeader("x-requested-with", "XMLHttpRequest");
		
		HttpClient httpClient = new HttpClientImpl();
//		var res = httpClient.doPost("https://contratos.comprasnet.gov.br/transparencia/contratos/search", pb, h);
//		System.out.println(res.getResult());
				
		httpClient.close();
//		HttpPost request = new HttpPost("https://contratos.comprasnet.gov.br/transparencia/contratos/search");
//
		
//		request.setEntity(body.getHttpEntity());

		
//		HttpClient httpClient = new HttpClientImpl();
//		
//		String date = "04/02/2013";
//		String commodity = "ISP";
//		
//		HttpPostBody body = new HttpPostBody();
//		
//		
//		HttpResponse response = httpClient.doPost(commodity, null);
//		
//		httpClient.close();

	}
	
	public static void consultaContratosEListaPaginas() throws Throwable {
		HttpClient httpClient = new HttpClientImpl();
		var res = httpClient.doGet("https://contratos.comprasnet.gov.br/transparencia/contratos");
		var document = Jsoup.parse(res.getResult());
		document.getElementsByTag("meta").forEach(meta -> {
			if (meta.attr("name").equals("csrf-token")) {
				try {
					var xCsrfToken = meta.attr("content");
					HttpPostBody pb = new SearchPagesBody(1);
					var h = new SearchPagesHeader(res.getHeaders(), xCsrfToken);
					HttpClient httpClient2 = new HttpClientImpl();
					var res2 = httpClient2.doPost("https://contratos.comprasnet.gov.br/transparencia/contratos/search", pb, h);
					System.out.println(res2.getResult());

					httpClient2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		httpClient.close();

	}

	public static void consultaContratos() throws Throwable {
		HttpClient httpClient = new HttpClientImpl();
		var res = httpClient.doGet("https://contratos.comprasnet.gov.br/transparencia/contratos");
		for (var h : res.getHeaders()) {
			System.out.println(h.getName());
		}
		System.out.println(res.getResult());
		
		httpClient.close();

	}
}
