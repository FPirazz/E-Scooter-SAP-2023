package sap.escooters.infrastructure;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.User;
import sap.escooters.ports.output.UserSerializer;

public class UserSerializerImpl implements UserSerializer {
    @Override
    public JsonObject toJson(User user) {
        JsonObject userObj = new JsonObject();
        userObj.put("id", user.getId());
        userObj.put("name", user.getName());
        userObj.put("surname", user.getSurname());
        return userObj;
    }
}