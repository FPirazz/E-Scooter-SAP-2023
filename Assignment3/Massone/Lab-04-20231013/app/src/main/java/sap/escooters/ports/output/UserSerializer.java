package sap.escooters.ports.output;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.User;

public interface UserSerializer {
    JsonObject toJson(User user);
}