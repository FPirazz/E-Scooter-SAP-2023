package sap.escooters.ports.input;

import io.vertx.core.json.JsonObject;

public interface RideUseCases {
    String startNewRide(String userId, String escooterId) throws Exception;
    JsonObject getRideInfo(String id) throws Exception;
    void endRide(String rideId) throws Exception;
}