package sap.escooters.launcher;

import sap.escooters.adapters.infrastructure.EScooterSerializerImpl;
import sap.escooters.adapters.infrastructure.RideJsonMapper;
import sap.escooters.adapters.infrastructure.UserSerializerImpl;
import sap.escooters.adapters.infrastructure.db.EScooterRepositoryImpl;
import sap.escooters.adapters.infrastructure.db.MongoRideRepository;
import sap.escooters.adapters.infrastructure.db.RideRepositoryImpl;
import sap.escooters.adapters.infrastructure.db.UserRepositoryImpl;
import sap.escooters.adapters.infrastructure.ui.PresentationAdapter;
import sap.escooters.adapters.mappers.EScooterSerializer;
import sap.escooters.adapters.mappers.RideSerializer;
import sap.escooters.adapters.mappers.UserSerializer;
import sap.escooters.domain.repositories.EScooterRepository;
import sap.escooters.domain.repositories.UserRepository;
import sap.escooters.domain.services.EScooterService;
import sap.escooters.domain.services.RideService;
import sap.escooters.domain.services.UserService;
import sap.escooters.ports.output.PresentationPort;

public class EScooterManagementSystem {
    public static void main(String[] args) {
        String dbaseFolder = "dbase";

        // Initialize serializers
        UserSerializer userSerializer = new UserSerializerImpl();
        UserRepository userRepository = new UserRepositoryImpl(dbaseFolder, userSerializer);
        EScooterSerializer escooterSerializer = new EScooterSerializerImpl();
        EScooterRepository escooterRepository = new EScooterRepositoryImpl(dbaseFolder, escooterSerializer);
        RideSerializer rideSerializer = new RideJsonMapper(userRepository, escooterRepository);

        // Set up database folder

        RideJsonMapper rideJsonMapper = new RideJsonMapper(userRepository, escooterRepository);
        MongoRideRepository rideRepository = new MongoRideRepository("mongodb://localhost:27017", "assignment_2",
                "rides", rideJsonMapper);

        // Initialize services
        UserService userService = new UserService(userRepository, userSerializer);
        EScooterService escooterService = new EScooterService(escooterRepository, escooterSerializer);
        RideService rideService = new RideService(userRepository, escooterRepository, rideRepository, escooterService,
                rideSerializer);

        // Start presentation port
        PresentationPort presentationPort = new PresentationAdapter(8080, userService, escooterService, rideService);
        presentationPort.init();
    }
}