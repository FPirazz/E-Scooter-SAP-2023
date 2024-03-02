package sap.escooters.domain.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.entities.Ride;
import sap.escooters.domain.entities.User;
import sap.escooters.ports.input.RideUseCases;
import sap.escooters.ports.output.EScooterRepository;
import sap.escooters.ports.output.RideRepository;
import sap.escooters.ports.output.UserRepository;

import java.util.Optional;

public class RideService implements RideUseCases {
    private UserRepository userRepository;
    private EScooterRepository escooterRepository;
    private RideRepository rideRepository;

    public RideService(UserRepository userRepository, EScooterRepository escooterRepository, RideRepository rideRepository) {
        this.userRepository = userRepository;
        this.escooterRepository = escooterRepository;
        this.rideRepository = rideRepository;
    }

    @Override
    public String startNewRide(String userId, String escooterId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<EScooter> escooter = escooterRepository.findById(escooterId);
        if (user.isPresent() && escooter.isPresent()) {
            EScooter sc = escooter.get();
            if (sc.isAvailable()) {
                Ride ride = new Ride(user.get(), escooter.get());
                rideRepository.save(ride); // Save the ride using the rideRepository
                return ride.getId();
            } else {
                throw new RideNotPossibleException();
            }
        } else {
            throw new RideNotPossibleException();
        }
    }

    @Override
    public JsonObject getRideInfo(String id) throws RideNotFoundException {
        Optional<Ride> ride = rideRepository.findById(id);
        if (ride.isPresent()) {
            return ride.get().toJson();
        } else {
            throw new RideNotFoundException();
        }
    }

    @Override
    public void endRide(String rideId) throws RideNotFoundException, RideAlreadyEndedException {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isPresent()) {
            Ride ri = ride.get();
            if (ri.isOngoing()) {
                ri.end();
                rideRepository.save(ri); // Save the ride using the rideRepository after ending it
            } else {
                throw new RideAlreadyEndedException();
            }
        } else {
            throw new RideNotFoundException();
        }
    }
}