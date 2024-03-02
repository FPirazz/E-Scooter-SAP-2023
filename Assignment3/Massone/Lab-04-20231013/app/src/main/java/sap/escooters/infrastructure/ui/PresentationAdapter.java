package sap.escooters.infrastructure.ui;

import io.vertx.core.Vertx;
import sap.escooters.domain.services.EScooterService;
import sap.escooters.domain.services.RideService;
import sap.escooters.domain.services.UserService;
import sap.escooters.ports.output.PresentationPort;
import sap.escooters.infrastructure.HttpServerAdapter;
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
    public void init() {
        Vertx vertx = Vertx.vertx();
        HttpServerAdapter myVerticle = new HttpServerAdapter(port, userService, escooterService, rideService);
        vertx.deployVerticle(myVerticle);
    }
}