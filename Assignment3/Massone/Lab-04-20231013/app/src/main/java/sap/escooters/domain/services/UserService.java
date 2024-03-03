package sap.escooters.domain.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.ports.input.UserUseCases;
import sap.escooters.adapters.mappers.UserSerializer;
import sap.escooters.domain.entities.User;
import sap.escooters.domain.repositories.UserRepository;

import java.util.Optional;

public class UserService implements UserUseCases {
    private UserRepository userRepository;
    private UserSerializer userSerializer;

    public UserService(UserRepository userRepository, UserSerializer userSerializer) {
        this.userRepository = userRepository;
        this.userSerializer = userSerializer;
    }

    @Override
    public void registerNewUser(String id, String name, String surname) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            User newUser = new User(name, surname);
            userRepository.save(newUser); // Save the user using the userRepository
        } else {
            throw new Exception("User already exists");
        }
    }

    @Override
    public String getUserInfo(String id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userSerializer.serialize(user.get());
        } else {
            throw new Exception("User not found");
        }
    }

    public boolean userExists(String userId) {
        return userRepository.findById(userId).isPresent();
    }
}