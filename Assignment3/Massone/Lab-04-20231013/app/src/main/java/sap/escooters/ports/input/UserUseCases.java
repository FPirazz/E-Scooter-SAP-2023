package sap.escooters.ports.input;

import io.vertx.core.json.JsonObject;

public interface UserUseCases {
    void registerNewUser(String id, String name, String surname);
    JsonObject getUserInfo(String id);
}