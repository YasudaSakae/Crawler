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
public class EditaisCrawler extends Crawler {
    private static final String ARCHIVE_TYPE_ATTR_NAME = "tipoDocumentoNome";
    private static final String ARCHIVE_URL_ATTR_NAME = "url";
    private static final String EDITAL_FILE_TYPE = "pdf";

    private final FetchChecker fetchChecker;
    private final File editaisDir;

    public EditaisCrawler(Config config, HttpBot bot) {
        super(config, bot);
        this.editaisDir = config.getEditaisDir();
        FileUtil.createDirectoryIfNotExists(editaisDir);
        this.fetchChecker = new FetchChecker(config, "fetched_editais.txt");
    }

    @Override
    protected void runCrawler() throws ClientProtocolException, IOException {
        List<File> jsonFiles = FileUtil.listDirFilesOrdered(editaisDir);

        // Log dos arquivos encontrados no diretório
        if (jsonFiles.isEmpty()) {
            log.warn("Nenhum arquivo JSON encontrado no diretório: " + editaisDir.getAbsolutePath());
        } else {
            log.info("Arquivos JSON encontrados no diretório: " + editaisDir.getAbsolutePath());
            for (File file : jsonFiles) {
                log.info("Arquivo encontrado: " + file.getName());
            }
        }

        List<EditalParams> editalUrls = getEditalParamsList(jsonFiles);
        int counter = fetchChecker.getNumOfFetchedFiles();

        for (EditalParams params : editalUrls) {
            String editalId = params.id();
            if (fetchChecker.contains(editalId)) {
                log.info("Edital já baixado: " + editalId);
                continue;
            }

            File targetFile = new File(editaisDir, String.format("%s.%s", editalId, EDITAL_FILE_TYPE));
            log.info("Baixando edital: " + editalId);
            bot.downloadFile(params.url(), targetFile.getAbsolutePath());
            fetchChecker.updateFetchedSetFile(editalId);
            counter++;

            if (counter % 20 == 0) {
                log.info(String.format("%d/%d baixados", counter, editalUrls.size()));
            }
        }
    }

    private List<EditalParams> getEditalParamsList(List<File> jsonFiles) {
        log.info("Extraindo URLs de editais dos arquivos de JSON...");
        List<EditalParams> editalUrls = new ArrayList<>();
        int counter = 0;

        for (File jsonFile : jsonFiles) {
            log.info("Processando arquivo: " + jsonFile.getName());
            String jsonStr = FileUtil.readFile(jsonFile);
            log.debug("Conteúdo do arquivo JSON (" + jsonFile.getName() + "): " + jsonStr);

            JsonElement json = Util.parseLenientJsonString(jsonStr);
            if (json == null) {
                log.error("Falha ao parsear JSON no arquivo: " + jsonFile.getName());
                continue;
            }

            JsonElement items = json.getAsJsonObject().get("items");
            if (items == null || !items.isJsonArray()) {
                log.warn("JSON inválido ou sem a chave 'items': " + jsonFile.getName());
                continue;
            }

            for (JsonElement archiveJson : items.getAsJsonArray()) {
                EditalParams params = extractParams(jsonFile, archiveJson);
                if (params != null) {
                    editalUrls.add(params);
                }
            }

            counter++;
            if (counter % 500 == 0) {
                log.info(String.format("%d/%d parâmetros extraídos", counter, jsonFiles.size()));
            }
        }
        return editalUrls;
    }

    private EditalParams extractParams(File jsonFile, JsonElement archiveJson) {
        JsonObject archiveJsonObj = archiveJson.getAsJsonObject();
        log.debug("JSON processado: " + archiveJsonObj.toString());
    
        String archiveType = null;
        if (archiveJsonObj.has(ARCHIVE_TYPE_ATTR_NAME)) {
            archiveType = archiveJsonObj.get(ARCHIVE_TYPE_ATTR_NAME).getAsString();
        } else if (archiveJsonObj.has("tipo_nome")) { 
            archiveType = archiveJsonObj.get("tipo_nome").getAsString();
        }
    
        if (archiveType == null) {
            log.warn("Atributo 'tipoDocumentoNome' ou 'tipo_nome' ausente no JSON: " + archiveJsonObj);
            return null;
        }
    
        // Verifique se o tipo é "Edital"
        if ("Edital".equalsIgnoreCase(archiveType)) {
            String url = null;

            if (archiveJsonObj.has(ARCHIVE_URL_ATTR_NAME)) {
                url = archiveJsonObj.get(ARCHIVE_URL_ATTR_NAME).getAsString();
            } else if (archiveJsonObj.has("item_url")) {
                // Construa a URL completa a partir do item_url
                String baseUrl = "https://pncp.gov.br/app/editais";
                url = baseUrl + archiveJsonObj.get("item_url").getAsString();
            }
    
            if (url == null) {
                log.warn("Nenhum atributo 'url' ou 'item_url' válido encontrado no JSON: " + archiveJsonObj);
                return null;
            }
    
            String id = FileUtil.getFileNameWithoutExtension(jsonFile);
            log.info("Parâmetro válido encontrado: id=" + id + ", url=" + url);
            return new EditalParams(id, url);
        }
    
        log.debug("Tipo de documento ignorado: " + archiveType);
        return null;
    }    

    private record EditalParams(String id, String url) {}
}
