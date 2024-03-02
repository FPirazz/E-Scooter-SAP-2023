package sap.escooters.domain.services;

import io.vertx.core.json.JsonObject;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.entities.Ride;
import sap.escooters.domain.entities.User;
import sap.escooters.ports.input.RideUseCases;
import sap.escooters.ports.output.EScooterRepository;
import sap.escooters.ports.output.RideRepository;
import sap.escooters.ports.output.RideSerializer;
import sap.escooters.ports.output.UserRepository;

import java.util.Optional;

public class RideService implements RideUseCases {
    private UserRepository userRepository;
    private EScooterRepository escooterRepository;
    private RideRepository rideRepository;
    private RideSerializer rideSerializer;
    private EScooterService escooterService;

    public RideService(UserRepository userRepository, EScooterRepository escooterRepository,
            RideRepository rideRepository, EScooterService escooterService, RideSerializer rideSerializer) {
        this.userRepository = userRepository;
        this.escooterRepository = escooterRepository;
        this.rideRepository = rideRepository;
        this.escooterService = escooterService;
    }

    @Override
    public String startNewRide(String userId, String escooterId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        Optional<EScooter> escooter = escooterRepository.findById(escooterId);
        if (user.isPresent() && escooter.isPresent()) {
            EScooter sc = escooter.get();
            if (sc.isAvailable()) {
                sc.setState(EScooter.EScooterState.IN_USE); // Set the state of the e-scooter as in use
                escooterService.updateEScooterState(escooterId, EScooter.EScooterState.IN_USE);
                Ride ride = new Ride(user.get(), escooter.get());
                rideRepository.save(ride); // Save the ride using the rideRepository
                return ride.getId();
            } else {
                throw new Exception("Ride not possible");
            }
        } else {
            throw new Exception("Ride not possible");
        }
    }

    @Override
    public JsonObject getRideInfo(String id) throws Exception {
        Optional<Ride> ride = rideRepository.findById(id);
        if (ride.isPresent()) {
            return rideSerializer.toJson(ride.get());
        } else {
            throw new Exception("Ride not found");
        }
    }

    @Override
    public void endRide(String rideId) throws Exception {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isPresent()) {
            Ride ri = ride.get();
            if (ri.isOngoing()) {
                ri.end();
                rideRepository.save(ri); // Save the ride using the rideRepository after ending it
            } else {
                throw new Exception("Ride already ended");
            }
        } else {
            throw new Exception("Ride not found");
        }
    }
}