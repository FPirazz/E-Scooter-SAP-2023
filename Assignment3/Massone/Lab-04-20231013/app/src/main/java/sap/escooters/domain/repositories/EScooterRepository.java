package sap.escooters.domain.repositories;

import java.util.Optional;

import sap.escooters.domain.entities.EScooter;

public interface EScooterRepository {
    void save(EScooter escooter);
    Optional<EScooter> findById(String id);
}