package sap.pixelart.service.application;

import io.vertx.core.json.JsonObject;
import sap.pixelart.service.domain.LogEntry;

import java.util.List;

//Interfaccia che dichiara i metodi che poi andranno ad implementare la business Logic.
public interface DistributedLogAPI {
    JsonObject mergeAllLogsIntoJson(List<LogEntry> logEntryList);
}
