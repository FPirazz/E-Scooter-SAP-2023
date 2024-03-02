package sap.escooters.ports.input;

import io.vertx.core.json.JsonObject;

public interface RideUseCases {
    String startNewRide(String userId, String escooterId);
    JsonObject getRideInfo(String id);
    void endRide(String rideId);
}