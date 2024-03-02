package sap.escooters.ports.input;

import io.vertx.core.json.JsonObject;

public interface EScooterUseCases {
    void registerNewEScooter(String id);
    JsonObject getEScooterInfo(String id);
}