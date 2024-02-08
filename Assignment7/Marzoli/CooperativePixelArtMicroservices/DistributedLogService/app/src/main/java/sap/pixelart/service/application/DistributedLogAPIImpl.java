package sap.pixelart.service.application;

import io.vertx.core.json.JsonObject;
import sap.pixelart.service.domain.LogEntry;

import java.util.List;

//Classe che implementa i metodi dell'API utilizzando le classi definite dentro il livello di "domain".
public class DistributedLogAPIImpl implements DistributedLogAPI {
    @Override
    public JsonObject mergeAllLogsIntoJson(List<LogEntry> logEntryList) {
        return null;
    }
}
