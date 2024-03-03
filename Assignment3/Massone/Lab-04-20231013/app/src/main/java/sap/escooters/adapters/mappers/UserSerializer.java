package sap.escooters.adapters.mappers;

import sap.escooters.domain.entities.User;

public interface UserSerializer {
    String serialize(User user);
    User deserialize(String userData);
}