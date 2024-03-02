package sap.escooters.ports.output;

import java.util.Optional;

import sap.escooters.domain.entities.EScooter;

public interface EScooterRepository {
    void save(EScooter escooter);
    Optional<EScooter> findById(String id);
}