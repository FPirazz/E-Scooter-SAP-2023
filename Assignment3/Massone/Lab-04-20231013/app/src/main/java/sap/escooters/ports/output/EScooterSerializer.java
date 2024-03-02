package sap.escooters.domain.ports.output;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;

public interface EScooterSerializer {
    JsonObject toJson(EScooter escooter);
}