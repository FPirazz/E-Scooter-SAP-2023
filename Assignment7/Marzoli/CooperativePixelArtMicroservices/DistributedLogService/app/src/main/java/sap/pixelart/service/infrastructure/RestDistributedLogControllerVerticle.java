package sap.pixelart.service.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import sap.pixelart.service.application.DistributedLogAPI;
import sap.pixelart.service.domain.LogEntry;

import java.util.ArrayList;
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
	private List<LogEntry> logEntries = new LinkedList<>();
	private List<String> logMessagesList = new ArrayList<>(); // Lista di Log Messages.


	public RestDistributedLogControllerVerticle(int port, DistributedLogAPI appAPI) {
		this.port = port;
		this.distributedLogAPI = appAPI;
		logger.setLevel(Level.INFO);
	}

	public void start() {
		logger.log(Level.INFO, "Distributed Log  initializing...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);


		/* configure the HTTP routes following a REST style */
		router.route(HttpMethod.GET, "/api/log").handler(this::sendLogsList);
		//router.route(HttpMethod.POST, "/api/log/:jsonBody").handler(this::mergeAllLogs);
		router.route(HttpMethod.POST, "/api/log").handler(this::printLogMessage);

		/* start the server */
		server
		.requestHandler(router)
		.listen(port);

		logger.log(Level.INFO, "Distributed Log  - port: " + port);
	}

	private void printLogMessage(RoutingContext routingContext) {
		// Ottieni il contenuto del messaggio dal corpo della richiesta
		routingContext.request().bodyHandler(buffer -> {
			String message = buffer.toString();
			logMessagesList.add(message);
			System.out.println("Messaggio ricevuto: " + message);

			// Invia una risposta al client
			JsonObject responseJson = new JsonObject().put("status", "Messaggio ricevuto con successo");
			sendReply(routingContext.response(), responseJson);
		});
	}

	private void sendLogsList(RoutingContext routingContext) {
		JsonArray logsArray = new JsonArray();

		for (String message : logMessagesList) {
			JsonObject logJson = new JsonObject().put("content:", message);
			logsArray.add(logJson);
		}

		JsonObject logsJson = new JsonObject().put("logs", logsArray);
		String prettyLogs = logsJson.encodePrettily();
		routingContext.response().end(prettyLogs);
	}



	private void mergeAllLogs(RoutingContext routingContext) {
		// Esegui la logica per gestire un nuovo log entry
		JsonObject logEntryJson = new JsonObject();
		logEntryJson.put("jsonBody", routingContext.pathParam("jsonBody"));
		LogEntry logEntry = convertJsonToLogEntry(logEntryJson);
		logEntries.add(logEntry);
		logger.log(Level.INFO, "ELEMENTO AGGIUNTO NELLA LISTA");
		// Rispondi con un messaggio di successo
		JsonObject responseJson = new JsonObject().put("message", "Log entry added successfully");
		sendReply(routingContext.response(), responseJson);
		System.out.println("SONO DENTRO mergeALL (POST) /API/LOG E QUESTA E' LA LISTA: "+ logEntries);
	}

	private LogEntry convertJsonToLogEntry(JsonObject logEntryJson) {
		String source = logEntryJson.getString("source", "");
		String message = logEntryJson.getString("message", "");

		return new LogEntry(source, message);
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
