package sap.pixelart.service.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import sap.pixelart.service.application.DistributedLogAPI;
import sap.pixelart.service.domain.LogEntry;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Verticle impementing the behaviour of a REST Adapter for the
 * PixelArt microservice
 *
 * @author aricci
 *
 */
public class RestDistributedLogControllerVerticle extends AbstractVerticle {

	private int port;
	private DistributedLogAPI distributedLogAPI;
	static Logger logger = Logger.getLogger("[Distributed Log]");
	private EventBus eventBus;
	private List<LogEntry> logEntries = new LinkedList<>();

	public RestDistributedLogControllerVerticle(int port, DistributedLogAPI appAPI, EventBus eventBus) {
		this.port = port;
		this.distributedLogAPI = appAPI;
		this.eventBus = eventBus;
		logger.setLevel(Level.INFO);
	}

	public void start() {
		logger.log(Level.INFO, "Distributed Log  initializing...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		/* configure an event bus event */
		eventBus.consumer("log.entry", this::handleLogEntry);

		/* configure the HTTP routes following a REST style */
		router.route(HttpMethod.GET, "/api/log").handler(this::mergeAllLogs);

		/* start the server */
		
		server
		.requestHandler(router)
		.listen(port);

		logger.log(Level.INFO, "Distributed Log  - port: " + port);
	}

	private void handleLogEntry(Message<JsonObject> message) {
		JsonObject logEntryJson = message.body();
		// Converte il JsonObject in un oggetto LogEntry e lo aggiunge alla lista logEntries
		LogEntry logEntry = convertJsonToLogEntry(logEntryJson);
		logEntries.add(logEntry);
	}

	private LogEntry convertJsonToLogEntry(JsonObject logEntryJson) {
		String source = logEntryJson.getString("source", "");
		String message = logEntryJson.getString("message", "");

		return new LogEntry(source, message);
	}


	private void mergeAllLogs(RoutingContext routingContext) {
		JsonArray logsArray = new JsonArray();

		for (LogEntry logEntry : logEntries) {
			JsonObject logJson = new JsonObject()
					.put("source", logEntry.getSource())
					.put("message", logEntry.getMessage());
			logsArray.add(logJson);
		}

		JsonObject logsJson = new JsonObject().put("logs", logsArray);
		sendReply(routingContext.response(), logsJson);
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

}
