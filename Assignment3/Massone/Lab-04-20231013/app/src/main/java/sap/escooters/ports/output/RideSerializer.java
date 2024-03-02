package sap.escooters.ports.output;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.Ride;

public interface RideSerializer {
    JsonObject toJson(Ride ride);
}