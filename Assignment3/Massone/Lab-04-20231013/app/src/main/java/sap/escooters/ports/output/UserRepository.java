package sap.escooters.ports.output;

import java.util.Optional;

import sap.escooters.domain.entities.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(String id);
}