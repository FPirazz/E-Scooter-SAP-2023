package sap.escooters.ports.input;

import io.vertx.core.json.JsonObject;

public interface UserUseCases {
    void registerNewUser(String id, String name, String surname) throws Exception;
    JsonObject getUserInfo(String id) throws Exception;
}