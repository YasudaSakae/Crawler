package br.com.sinerji.comprascrawler.crawler.pncp;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.sinerji.comprascrawler.Config;
import br.com.sinerji.comprascrawler.http.HttpBot;
import br.com.sinerji.comprascrawler.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArchivesFromPNCPCrawler extends PNCPArchivesCrawler {
    protected final File pagesDir;

    public ArchivesFromPNCPCrawler(Config config, HttpBot bot) {
        super(config, bot);
        this.pagesDir = config.getPncpContractsPagesDir();
    }

    @Override
    protected Set<PncpArchivesParams> getPncpArchivesParamsSet() {
        log.info("Extracting PNCP archives params from pages...");
        Set<PncpArchivesParams> pncpArchivesParamsSet = new HashSet<>();
        List<File> pagesDirFiles = FileUtil.listDirFilesOrdered(pagesDir);
        int counter = 0;

        for (File pageFile : pagesDirFiles) {
            String jsonStr = FileUtil.readFile(pageFile);
            JsonElement json = JsonParser.parseString(jsonStr);
            JsonObject dataObj = json.getAsJsonObject();
            JsonArray items = dataObj.get("items").getAsJsonArray();

            for (JsonElement itemJson : items) {
                JsonObject itemObj = itemJson.getAsJsonObject();
                String itemType = itemObj.has("type") ? itemObj.get("type").getAsString() : "";

                if (getTypeFilter().equalsIgnoreCase(itemType)) {
                    String itemUrl = itemObj.has("item_url") ? itemObj.get("item_url").getAsString() : "";
                    if (!itemUrl.isEmpty()) {
                        String[] split = itemUrl.replace("/" + getTypeFilter() + "/", "").split("/");
                        String largeCode = split[0];
                        String year = split[1];
                        String smallCode = split[2];

                        PncpArchivesParams params = PncpArchivesParams.builder()
                                .largeCode(largeCode)
                                .smallCode(smallCode)
                                .year(year)
                                .build();

                        pncpArchivesParamsSet.add(params);
                    }
                }
            }

            counter++;
            if (counter % 50 == 0) {
                log.info(String.format("%d/%d pages processed", counter, pagesDirFiles.size()));
            }
        }

        log.info("Finished extracting PNCP archives.");
        return pncpArchivesParamsSet;
    }

    protected String getTypeFilter() {
        return "contrato"; // Valor padrão para contratos. Subclasses podem sobrescrever este método.
    }
}
