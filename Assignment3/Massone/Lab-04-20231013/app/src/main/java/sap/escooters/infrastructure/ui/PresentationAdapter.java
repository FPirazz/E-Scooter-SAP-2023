package sap.escooters.infrastructure.ui;

import io.vertx.core.Vertx;
import sap.escooters.domain.services.EScooterService;
import sap.escooters.domain.services.RideService;
import sap.escooters.domain.services.UserService;
import sap.escooters.ports.output.PresentationPort;
import sap.escooters.presentation_layer.EScooterManServer;
import sap.layers.Layer;

import java.util.Optional;

public class PresentationAdapter implements PresentationPort {
    private UserService userService;
    private EScooterService escooterService;
    private RideService rideService;
    private int port;

    public PresentationAdapter(int port, UserService userService, EScooterService escooterService, RideService rideService) {
        this.port = port;
        this.userService = userService;
        this.escooterService = escooterService;
        this.rideService = rideService;
    }

    @Override
    public void init(Optional<Layer> layer) {
        Vertx vertx = Vertx.vertx();
        EScooterManServer myVerticle = new EScooterManServer(port, userService, escooterService, rideService);
        vertx.deployVerticle(myVerticle);
    }
}