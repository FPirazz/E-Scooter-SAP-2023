package sap.escooters.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import sap.escooters.domain.services.EScooterService;
import sap.escooters.domain.services.RideService;
import sap.escooters.domain.services.UserService;
import sap.escooters.ports.input.ServerPort;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServerAdapter extends AbstractVerticle implements ServerPort {

    static Logger logger = Logger.getLogger("[EScooter Server]");
    private int port;
    private UserService userService;
    private EScooterService escooterService;
    private RideService rideService;

    public HttpServerAdapter(int port, UserService userService, EScooterService escooterService, RideService rideService) {
        this.port = port;
        this.userService = userService;
        this.escooterService = escooterService;
        this.rideService = rideService;
        logger.setLevel(Level.INFO);
    }

    public void start() {
        logger.log(Level.INFO, "EScooterMan server initializing...");
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        /* static files by default searched in "webroot" directory */
        router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
        router.route().handler(BodyHandler.create());
        // manage the root
        router.route(HttpMethod.GET, "/api/dashboard").handler(this::getDashboard);
        router.route(HttpMethod.POST, "/api/users").handler(this::registerNewUser);
        router.route(HttpMethod.GET, "/api/users/:userId").handler(this::getUserInfo);
        router.route(HttpMethod.POST, "/api/escooters").handler(this::registerNewEScooter);
        router.route(HttpMethod.GET, "/api/escooters/:escooterId").handler(this::getEScooterInfo);
        router.route(HttpMethod.POST, "/api/rides").handler(this::startNewRide);
        router.route(HttpMethod.GET, "/api/rides/:rideId").handler(this::getRideInfo);
        router.route(HttpMethod.POST, "/api/rides/:rideId/end").handler(this::endRide);

        server
                .requestHandler(router)
                .listen(port);

        logger.log(Level.INFO, "EScooterMan server ready - port: " + port);
    }

    private void getDashboard(RoutingContext routingContext) {
        logger.log(Level.INFO, "New dashboard request: " + routingContext.currentRoute().getPath());
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "text/html");

        vertx.fileSystem().readFile("resources/webroot/ride-dashboard.html", result -> {
            if (result.succeeded()) {
                response.end(result.result());
            } else {
                logger.log(Level.SEVERE, "Failed to read file", result.cause());
                response.setStatusCode(500).end();
            }
        });
    }

    private void registerNewUser(RoutingContext context) {
        JsonObject userInfo = context.body().asJsonObject();
        String id = userInfo.getString("id");
        String name = userInfo.getString("name");
        String surname = userInfo.getString("surname");

        JsonObject reply = new JsonObject();
        try {
            userService.registerNewUser(id, name, surname);
            reply.put("result", "ok");
        } catch (Exception ex) {
            reply.put("result", "user-id-already-existing");
        }
        sendReply(context, reply);
    }

    protected void getUserInfo(RoutingContext context) {
        String userId = context.pathParam("userId");
        JsonObject reply = new JsonObject();
        try {
            JsonObject info = userService.getUserInfo(userId);
            reply.put("result", "ok");
            reply.put("user", info);
        } catch (Exception ex) {
            reply.put("result", "user-not-found");
        }
        sendReply(context, reply);
    }

    protected void registerNewEScooter(RoutingContext context) {
        JsonObject escooterInfo = context.body().asJsonObject();
        String id = escooterInfo.getString("id");

        JsonObject reply = new JsonObject();
        try {
            logger.info("Attempting to register new e-scooter with id: " + id);
            escooterService.registerNewEScooter(id);
            reply.put("result", "ok");
            logger.info("E-scooter registered successfully with id: " + id);
        } catch (Exception ex) {
            reply.put("result", "escooter-id-already-existing");
            logger.severe("Failed to register new e-scooter with id: " + id + ". Error: " + ex.getMessage());
        }
        sendReply(context, reply);
    }

    protected void getEScooterInfo(RoutingContext context) {
        String escooterId = context.pathParam("escooterId");
        JsonObject reply = new JsonObject();
        try {
            JsonObject info = escooterService.getEScooterInfo(escooterId);
            reply.put("result", "ok");
            reply.put("escooter", info);
        } catch (Exception ex) {
            reply.put("result", "escooter-not-found");
        }
        sendReply(context, reply);
    }

    protected void startNewRide(RoutingContext context) {
        JsonObject rideInfo = context.body().asJsonObject();
        String userId = rideInfo.getString("userId");
        String escooterId = rideInfo.getString("escooterId");

        JsonObject reply = new JsonObject();
        try {
            if (!userService.userExists(userId)) {
                reply.put("result", "user-does-not-exist");
                logger.severe("Failed to start new ride because user does not exist: " + userId);
                sendReply(context, reply);
                return;
            }

            if (!escooterService.escooterExists(escooterId)) {
                reply.put("result", "escooter-does-not-exist");
                logger.severe("Failed to start new ride because e-scooter does not exist: " + escooterId);
                sendReply(context, reply);
                return;
            }

            String rideId = rideService.startNewRide(userId, escooterId);
            reply.put("result", "ok");
            reply.put("rideId", rideId);
            logger.info("Ride started successfully with rideId: " + rideId);
        } catch (Exception ex) {
            reply.put("result", "start-new-ride-failed");
            logger.severe("Failed to start new ride for user: " + userId + " with e-scooter: " + escooterId + ". Error: "
                    + ex.getMessage());
        }
        sendReply(context, reply);
    }

    protected void getRideInfo(RoutingContext context) {
        String rideId = context.pathParam("rideId");
        JsonObject reply = new JsonObject();
        try {
            JsonObject info = rideService.getRideInfo(rideId);
            reply.put("result", "ok");
            reply.put("ride", info);
        } catch (Exception ex) {
            reply.put("result", "ride-not-found");
        }
        sendReply(context, reply);
    }

    protected void endRide(RoutingContext context) {
        String rideId = context.pathParam("rideId");
        JsonObject reply = new JsonObject();
        try {
            rideService.endRide(rideId);
            reply.put("result", "ok");
        } catch (Exception ex) {
            reply.put("result", "ride-already-ended");
        }
        sendReply(context, reply);
    }

    private void sendReply(RoutingContext request, JsonObject reply) {
        HttpServerResponse response = request.response();
        response.putHeader("content-type", "application/json");
        response.end(reply.toString());
    }
}