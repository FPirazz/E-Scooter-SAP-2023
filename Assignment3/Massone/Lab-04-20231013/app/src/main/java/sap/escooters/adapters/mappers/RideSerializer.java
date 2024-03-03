package sap.escooters.adapters.mappers;

import sap.escooters.domain.entities.Ride;

public interface RideSerializer {
    String serialize(Ride ride);
    Ride deserialize(String rideData);
}