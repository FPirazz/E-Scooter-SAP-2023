package sap.escooters.application.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.application.ports.input.UserUseCases;
import sap.escooters.domain_layer.DomainLayer;
import sap.escooters.domain_layer.User;

import java.util.Optional;

public class UserService implements UserUseCases {
    private DomainLayer domainLayer;

    public UserService(DomainLayer domainLayer) {
        this.domainLayer = domainLayer;
    }

    @Override
    public void registerNewUser(String id, String name, String surname) throws UserIdAlreadyExistingException {
        Optional<User> user = domainLayer.getUser(id);
        if (user.isEmpty()) {
            domainLayer.addNewUser(id, name, surname);
        } else {
            throw new UserIdAlreadyExistingException();
        }
    }

    @Override
    public JsonObject getUserInfo(String id) throws UserNotFoundException {
        Optional<User> user = domainLayer.getUser(id);
        if (user.isPresent()) {
            return user.get().toJson();
        } else {
            throw new UserNotFoundException();
        }
    }
}