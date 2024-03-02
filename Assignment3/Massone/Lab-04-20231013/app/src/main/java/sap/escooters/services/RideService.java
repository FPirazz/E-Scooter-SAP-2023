package sap.escooters.application.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.application.ports.input.RideUseCases;
import sap.escooters.domain_layer.DomainLayer;
import sap.escooters.domain_layer.EScooter;
import sap.escooters.domain_layer.Ride;
import sap.escooters.domain_layer.User;

import java.util.Optional;

public class RideService implements RideUseCases {
    private DomainLayer domainLayer;

    public RideService(DomainLayer domainLayer) {
        this.domainLayer = domainLayer;
    }

    @Override
    public String startNewRide(String userId, String escooterId) throws RideNotPossibleException {
        Optional<User> user = domainLayer.getUser(userId);
        Optional<EScooter> escooter = domainLayer.getEScooter(escooterId);
        if (user.isPresent() && escooter.isPresent()) {
            EScooter sc = escooter.get();
            if (sc.isAvailable()) {
                return domainLayer.startNewRide(user.get(), escooter.get());
            } else {
                throw new RideNotPossibleException();
            }
        } else {
            throw new RideNotPossibleException();
        }
    }

    @Override
    public JsonObject getRideInfo(String id) throws RideNotFoundException {
        Optional<Ride> ride = domainLayer.getRide(id);
        if (ride.isPresent()) {
            return ride.get().toJson();
        } else {
            throw new RideNotFoundException();
        }
    }

    @Override
    public void endRide(String rideId) throws RideNotFoundException, RideAlreadyEndedException {
        Optional<Ride> ride = domainLayer.getRide(rideId);
        if (ride.isPresent()) {
            Ride ri = ride.get();
            if (ri.isOngoing()) {
                ri.end();
            } else {
                throw new RideAlreadyEndedException();
            }
        } else {
            throw new RideNotFoundException();
        }
    }
}