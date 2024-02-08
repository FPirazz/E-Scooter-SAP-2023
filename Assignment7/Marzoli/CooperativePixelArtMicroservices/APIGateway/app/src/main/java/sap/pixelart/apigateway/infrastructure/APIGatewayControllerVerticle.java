package sap.pixelart.apigateway.infrastructure;

import brave.Tracer;
import brave.Tracing;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import sap.pixelart.library.PixelArtAsyncAPI;
import sap.pixelart.library.PixelGridEventObserver;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Verticle impementing the behaviour of a REST Adapter for the
 * PixelArt microservice
 *
 * @author aricci
 */
public class APIGatewayControllerVerticle extends AbstractVerticle implements PixelGridEventObserver {

    static Logger logger = Logger.getLogger("[PixelArt Service]");
    static String PIXEL_GRID_CHANNEL = "pixel-grid-events";
    private int port;
    private PixelArtAsyncAPI serviceAPI;
    private Tracer tracer;

    public APIGatewayControllerVerticle(int port, PixelArtAsyncAPI serviceAPI) {
        this.port = port;
        this.serviceAPI = serviceAPI;
        logger.setLevel(Level.INFO);
        // Initialize the Tracer
        var sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
        var zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);
        var tracing = Tracing.newBuilder()
                .localServiceName("APIGatewayService")
                .addSpanHandler(zipkinSpanHandler)
                .build();
        tracer = tracing.tracer();
    }

    public void start() {
        logger.log(Level.INFO, "PixelArt Service initializing...");
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        /* configure the HTTP routes following a REST style */

        //E very incoming request will first go through the handleRouteRequest method, which performs the health check.
        router.route().handler(this::handleRouteRequest);
        //If you want to check manually through localhost decomment this line of code.
        //router.route(HttpMethod.GET, "/health").handler(this::healthCheck);

        router.route(HttpMethod.POST, "/api/brushes").handler(this::createBrush);
        router.route(HttpMethod.GET, "/api/brushes").handler(this::getCurrentBrushes);
        router.route(HttpMethod.GET, "/api/brushes/:brushId").handler(this::getBrushInfo);
        router.route(HttpMethod.DELETE, "/api/brushes/:brushId").handler(this::destroyBrush);
        router.route(HttpMethod.POST, "/api/brushes/:brushId/move-to").handler(this::moveBrushTo);
        router.route(HttpMethod.POST, "/api/brushes/:brushId/change-color").handler(this::changeBrushColor);
        router.route(HttpMethod.POST, "/api/brushes/:brushId/select-pixel").handler(this::selectPixel);
        router.route(HttpMethod.GET, "/api/pixel-grid").handler(this::getPixelGridState);
        this.handleEventSubscription(server, "/api/pixel-grid/events");

        /* start the server */

        server
                .requestHandler(router)
                .listen(port);

        logger.log(Level.INFO, "PixelArt Service ready - port: " + port);
    }

    private void handleRouteRequest(RoutingContext context) {
        if (healthCheck(context)) {
            // Proceed with the actual route handling logic
            context.next();
        } else {
            // Respond with a service unavailable status
            sendServiceUnavailable(context.response());
        }
    }


    protected boolean healthCheck(RoutingContext context) {
        JsonObject healthStatus = new JsonObject();

        //Checking for the API methods to work Properly.
        // Check 1
        boolean isCreateBrushSuccessful = executeHealthCheck(() -> createBrush(context));
        // Check 2:
        boolean isGetCurrentBrushesSuccessful = executeHealthCheck(() -> getCurrentBrushes(context));
        //Check 3:
        boolean isGetBrushInfoSuccessful = executeHealthCheck(() -> getBrushInfo(context));
        //Check 4:
        boolean isDestroyBrushSuccessful = executeHealthCheck(() -> destroyBrush(context));
        //Check 5:
        boolean isMoveBrushToSuccessful = executeHealthCheck(() -> moveBrushTo(context));
        //Check 6:
        boolean isChangeBrushColorSuccessful = executeHealthCheck(() -> changeBrushColor(context));
        //Check 7:
        boolean isSelectPixelSuccessful = executeHealthCheck(() -> selectPixel(context));
        //Check 8:
        boolean isGetPixelGridStateSuccessful = executeHealthCheck(() -> getPixelGridState(context));


        // Aggiorna lo stato generale in base al risultato delle invocazioni delle varie API.
        healthStatus.put("createBrush", isCreateBrushSuccessful);
        healthStatus.put("getCurrentBrushes", isGetCurrentBrushesSuccessful);
        healthStatus.put("getBrushInfo", isGetBrushInfoSuccessful);
        healthStatus.put("destroyBrush", isDestroyBrushSuccessful);
        healthStatus.put("moveBrushTo", isMoveBrushToSuccessful);
        healthStatus.put("changeBrushColor", isChangeBrushColorSuccessful);
        healthStatus.put("selectPixel", isSelectPixelSuccessful);
        healthStatus.put("getPixelGridState", isGetPixelGridStateSuccessful);

        // Overall status
        boolean isSystemHealthy = isCreateBrushSuccessful &&
                isGetCurrentBrushesSuccessful &&
                isGetBrushInfoSuccessful &&
                isDestroyBrushSuccessful &&
                isMoveBrushToSuccessful &&
                isChangeBrushColorSuccessful &&
                isSelectPixelSuccessful &&
                isGetPixelGridStateSuccessful;

        healthStatus.put("status", isSystemHealthy ? "UP" : "DOWN");

        logger.log(Level.INFO, "API HealthCheck request - " + context.currentRoute().getPath());
        logger.log(Level.INFO, "Body: " + healthStatus.encodePrettily());

        // Health check is okay
        return isSystemHealthy;
    }

    // Metodo di supporto per eseguire un health check generico
    private boolean executeHealthCheck(Runnable operation) {
        try {
            // Esegui l'operazione
            operation.run();
            return true; // Se l'operazione ha successo, ritorna true
        } catch (IllegalStateException ex) {
            // Handle the case where the response has already been sent which may happen since i'm invoking the API
            // and is not part of the final purpose of the Health System checking.
            return true;
        } catch (Exception ex) {
            logger.log(Level.WARNING, " Triggered the excepetion: " + ex);
            return false; // Se c'è un errore, ritorna false
        }
    }


    /* List of handlers, mapping the API */

    protected void createBrush(RoutingContext context) {
        var span = tracer.startScopedSpan("createBrush");
        try {
            logger.log(Level.INFO, "CreateBrush request - " + context.currentRoute().getPath());
            JsonObject reply = new JsonObject();
            serviceAPI
                    .createBrush()
                    .onSuccess((String brushId) -> {
                        try {
                            reply.put("brushId", brushId);
                            sendReply(context.response(), reply);
                        } catch (Exception ex) {
                            sendServiceError(context.response());
                        }
                    })
                    .onFailure((e) -> {
                        sendServiceError(context.response());
                    });
        } finally {
            span.finish();
        }
    }

    protected void getCurrentBrushes(RoutingContext context) {
        var span = tracer.startScopedSpan("getCurrentBrushes");
        try {
            logger.log(Level.INFO, "GetCurrentBrushes request - " + context.currentRoute().getPath());

            JsonObject reply = new JsonObject();
            serviceAPI
                    .getCurrentBrushes()
                    .onSuccess((JsonArray brushes) -> {
                        try {
                            reply.put("brushes", brushes);
                            sendReply(context.response(), reply);
                        } catch (Exception ex) {
                            sendServiceError(context.response());
                        }
                    })
                    .onFailure((e) -> {
                        sendServiceError(context.response());
                    });
        } finally {
            span.finish();
        }
    }

    protected void getBrushInfo(RoutingContext context) {
        var span = tracer.startScopedSpan("getBrushInfo");
        try {
            logger.log(Level.INFO, "Get Brush info request: " + context.currentRoute().getPath());
            String brushId = context.pathParam("brushId");
            JsonObject reply = new JsonObject();
            serviceAPI
                    .getBrushInfo(brushId)
                    .onSuccess((JsonObject info) -> {
                        try {
                            reply.put("brushInfo", info);
                            sendReply(context.response(), reply);
                        } catch (Exception ex) {
                            sendServiceError(context.response());
                        }
                    })
                    .onFailure((e) -> {
                        sendServiceError(context.response());
                    });
        } finally {
            span.finish();
        }
    }

    protected void moveBrushTo(RoutingContext context) {
        var span = tracer.startScopedSpan("moveBrushTo");
        try {
            logger.log(Level.INFO, "MoveBrushTo request: " + context.currentRoute().getPath());
            String brushId = context.pathParam("brushId");
            logger.log(Level.INFO, "Brush id: " + brushId);
            context.request().handler(buf -> {
                JsonObject brushInfo = buf.toJsonObject();
                int x = brushInfo.getInteger("x");
                int y = brushInfo.getInteger("y");
                JsonObject reply = new JsonObject();

                serviceAPI
                        .moveBrushTo(brushId, y, x)
                        .onSuccess((v) -> {
                            try {
                                sendReply(context.response(), reply);
                            } catch (Exception ex) {
                                sendServiceError(context.response());
                            }
                        })
                        .onFailure((e) -> {
                            sendServiceError(context.response());
                        });
            });
        } finally {
            span.finish();
        }
    }

    protected void changeBrushColor(RoutingContext context) {
        var span = tracer.startScopedSpan("changeBrushColor");
        try {
            logger.log(Level.INFO, "ChangeBrushColor request: " + context.currentRoute().getPath());
            String brushId = context.pathParam("brushId");
            context.request().handler(buf -> {
                JsonObject brushInfo = buf.toJsonObject();
                logger.log(Level.INFO, "Body: " + brushInfo.encodePrettily());
                int c = brushInfo.getInteger("color");
                JsonObject reply = new JsonObject();
                serviceAPI
                        .changeBrushColor(brushId, c)
                        .onSuccess((v) -> {
                            try {
                                sendReply(context.response(), reply);
                            } catch (Exception ex) {
                                sendServiceError(context.response());
                            }
                        })
                        .onFailure((e) -> {
                            sendServiceError(context.response());
                        });
            });
        } finally {
            span.finish();
        }
    }

    protected void selectPixel(RoutingContext context) {
        var span = tracer.startScopedSpan("selectPixel");
        try {
            logger.log(Level.INFO, "SelectPixel request: " + context.currentRoute().getPath());
            String brushId = context.pathParam("brushId");
            JsonObject reply = new JsonObject();
            serviceAPI
                    .selectPixel(brushId)
                    .onSuccess((v) -> {
                        try {
                            sendReply(context.response(), reply);
                        } catch (Exception ex) {
                            sendServiceError(context.response());
                        }
                    })
                    .onFailure((e) -> {
                        sendServiceError(context.response());
                    });
        } finally {
            span.finish();
        }
    }

    protected void destroyBrush(RoutingContext context) {
        var span = tracer.startScopedSpan("destroyBrush");
        try {
            logger.log(Level.INFO, "Destroy Brush request: " + context.currentRoute().getPath());
            String brushId = context.pathParam("brushId");
            JsonObject reply = new JsonObject();
            serviceAPI
                    .destroyBrush(brushId)
                    .onSuccess((v) -> {
                        try {
                            sendReply(context.response(), reply);
                        } catch (Exception ex) {
                            sendServiceError(context.response());
                        }
                    })
                    .onFailure((e) -> {
                        sendServiceError(context.response());
                    });
        } finally {
            span.finish();
        }
    }

    protected void getPixelGridState(RoutingContext context) {
        var span = tracer.startScopedSpan("getPixelGridState");
        try {
            logger.log(Level.INFO, "Get Pixel Grid state request: " + context.currentRoute().getPath());
            JsonObject reply = new JsonObject();
            serviceAPI
                    .getPixelGridState()
                    .onSuccess((JsonObject info) -> {
                        try {
                            reply.put("pixelGrid", info);
                            sendReply(context.response(), reply);
                        } catch (Exception ex) {
                            sendServiceError(context.response());
                        }
                    })
                    .onFailure((e) -> {
                        sendServiceError(context.response());
                    });
        } finally {
            span.finish();
        }
    }

    /* Handling subscribers using web sockets */

    protected void handleEventSubscription(HttpServer server, String path) {
        server.webSocketHandler(webSocket -> {
            if (webSocket.path().equals(path)) {
                webSocket.accept();
                logger.log(Level.INFO, "New PixelGrid subscription accepted.");

                JsonObject reply = new JsonObject();
                serviceAPI
                        .subscribePixelGridEvents(this::pixelColorChanged)
                        .onSuccess((JsonObject grid) -> {
                            reply.put("event", "subscription-started");
                            reply.put("pixelGridCurrentState", grid);
                            webSocket.writeTextMessage(reply.encodePrettily());

                            EventBus eb = vertx.eventBus();
                            eb.consumer(PIXEL_GRID_CHANNEL, msg -> {
                                JsonObject ev = (JsonObject) msg.body();
                                logger.log(Level.INFO, "Event: " + ev.encodePrettily());
                                webSocket.writeTextMessage(ev.encodePrettily());
                            });
                        })
                        .onFailure((e) -> {
                            logger.log(Level.INFO, "PixelGrid subscription refused.");
                            webSocket.reject();
                        });
            } else {
                logger.log(Level.INFO, "PixelGrid subscription refused.");
                webSocket.reject();
            }
        });
    }

    /* This is notified by the application/domain layer */

    @Override
    public void pixelColorChanged(int x, int y, int color) {
        var span = tracer.startScopedSpan("pixelColorChanged");
        try {
            logger.log(Level.INFO, "New PixelGrid event - pixel selected");
            EventBus eb = vertx.eventBus();
            JsonObject obj = new JsonObject();
            obj.put("event", "pixel-selected");
            obj.put("x", x);
            obj.put("y", y);
            obj.put("color", color);
            eb.publish(PIXEL_GRID_CHANNEL, obj);
        } finally {
            span.finish();
        }
    }

    /* Aux methods */


    private void sendReply(HttpServerResponse response, JsonObject reply) {
        response.putHeader("content-type", "application/json");
        response.end(reply.toString());
    }

    private void sendBadRequest(HttpServerResponse response, JsonObject reply) {
        response.setStatusCode(400);
        response.putHeader("content-type", "application/json");
        response.end(reply.toString());
    }

    private void sendServiceError(HttpServerResponse response) {
        response.setStatusCode(500);
        response.putHeader("content-type", "application/json");
        response.end();
    }

    private void sendServiceUnavailable(HttpServerResponse response) {
        response.setStatusCode(503);
        response.putHeader("content-type", "application/json");
        response.end();
    }

}
