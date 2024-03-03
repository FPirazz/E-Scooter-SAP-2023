package sap.escooters.domain.repositories;

import java.util.Optional;

import sap.escooters.domain.entities.Ride;

public interface RideRepository {
    void save(Ride ride);
    Optional<Ride> findById(String id);
    int getNumberOfOngoingRides();
}