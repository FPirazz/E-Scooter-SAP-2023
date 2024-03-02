package sap.escooters.launcher;

import sap.escooters.domain.services.EScooterService;
import sap.escooters.domain.services.RideService;
import sap.escooters.domain.services.UserService;
import sap.escooters.infrastructure.EScooterSerializerImpl;
import sap.escooters.infrastructure.RideSerializerImpl;
import sap.escooters.infrastructure.UserSerializerImpl;
import sap.escooters.infrastructure.db.EScooterRepositoryImpl;
import sap.escooters.infrastructure.db.RideRepositoryImpl;
import sap.escooters.infrastructure.db.UserRepositoryImpl;
import sap.escooters.infrastructure.ui.PresentationAdapter;
import sap.escooters.ports.output.EScooterSerializer;
import sap.escooters.ports.output.PresentationPort;
import sap.escooters.ports.output.RideSerializer;
import sap.escooters.ports.output.UserSerializer;

public class EScooterManagementSystem {
    public static void main(String[] args) {
        UserSerializer userSerializer = new UserSerializerImpl();
        RideSerializer rideSerializer = new RideSerializerImpl();
        EScooterSerializer escooterSerializer = new EScooterSerializerImpl();

        String dbaseFolder = "dbase";
        UserRepositoryImpl userRepository = new UserRepositoryImpl(dbaseFolder, userSerializer);
        EScooterRepositoryImpl escooterRepository = new EScooterRepositoryImpl(dbaseFolder, escooterSerializer);
        RideRepositoryImpl rideRepository = new RideRepositoryImpl(dbaseFolder, rideSerializer);

        UserService userService = new UserService(userRepository, userSerializer);
        EScooterService escooterService = new EScooterService(escooterRepository, escooterSerializer);
        RideService rideService = new RideService(userRepository, escooterRepository, rideRepository, escooterService, rideSerializer);

        PresentationPort presentationPort = new PresentationAdapter(8080, userService, escooterService, rideService);
        presentationPort.init();
    }
}