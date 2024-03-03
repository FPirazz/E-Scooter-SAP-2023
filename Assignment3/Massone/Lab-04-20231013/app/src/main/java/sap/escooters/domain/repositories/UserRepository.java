package sap.escooters.domain.repositories;

import java.util.Optional;

import sap.escooters.domain.entities.User;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(String id);
}