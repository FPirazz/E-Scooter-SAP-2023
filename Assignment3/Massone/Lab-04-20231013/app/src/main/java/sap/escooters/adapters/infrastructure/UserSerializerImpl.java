package sap.escooters.adapters.infrastructure;

import com.google.gson.Gson;
import sap.escooters.adapters.mappers.UserSerializer;
import sap.escooters.domain.entities.User;

public class UserSerializerImpl implements UserSerializer {
    private final Gson gson = new Gson();

    @Override
    public String serialize(User user) {
        return gson.toJson(user);
    }

    @Override
    public User deserialize(String userData) {
        return gson.fromJson(userData, User.class);
    }
}