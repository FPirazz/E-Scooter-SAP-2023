package sap.escooters.domain.services;

import sap.escooters.adapters.mappers.RideSerializer;
import sap.escooters.domain.entities.EScooter;
import sap.escooters.domain.entities.Ride;
import sap.escooters.domain.entities.User;
import sap.escooters.domain.repositories.EScooterRepository;
import sap.escooters.domain.repositories.RideRepository;
import sap.escooters.domain.repositories.UserRepository;
import sap.escooters.ports.input.RideUseCases;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class RideService implements RideUseCases {
    private static final Logger LOGGER = Logger.getLogger(RideService.class.getName());
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
                Ride ride = new Ride(UUID.randomUUID().toString(), user.get(), escooter.get());
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
    public String getRideInfo(String id) throws Exception {
        Optional<Ride> ride = rideRepository.findById(id);
        if (ride.isPresent()) {
            return rideSerializer.serialize(ride.get());
        } else {
            throw new Exception("Ride not found");
        }
    }

    @Override
    public void endRide(String rideId) throws Exception {
        LOGGER.info("Attempting to end ride with ID: " + rideId);
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isPresent()) {
            Ride ri = ride.get();
            System.out.println("Ride ID: " + ri.getId());
            System.out.println("IS going: " + ri.isOngoing());
            if (ri.isOngoing()) {
                ri.end();
                System.out.println("Ride ID ended: " + ri.getId());
                rideRepository.save(ri); // Save the ride using the rideRepository after ending it
                LOGGER.info("Successfully ended ride with ID: " + rideId);
            } else {
                LOGGER.warning("Attempted to end ride with ID: " + rideId + ", but it was already ended");
                throw new Exception("Ride already ended");
            }
        } else {
            LOGGER.warning("Attempted to end ride with ID: " + rideId + ", but it was not found");
            throw new Exception("Ride not found");
        }
    }

    @Override
    public int getNumberOfOngoingRides() {
        return rideRepository.getNumberOfOngoingRides();
    }
}