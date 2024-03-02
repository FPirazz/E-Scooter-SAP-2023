package sap.escooters.ports.output;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;

public interface EScooterSerializer {
    JsonObject toJson(EScooter escooter);

    EScooter fromJson(JsonObject escooterJson);
}