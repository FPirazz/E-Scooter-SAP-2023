package sap.pixelart.library;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.logging.Level;
import java.util.logging.Logger;

class PixelArtServiceProxy implements PixelArtAsyncAPI {

    static Logger logger = Logger.getLogger("[PixelArt Service Proxy]");	
	private HttpClient client;
	private Vertx vertx;
	private String brushId;
	private String host;
	private int port;
	
	public PixelArtServiceProxy() {
		vertx = Vertx.vertx();
	}
	
	public void init(String host, int port) {		
		this.host = host;
		this.port = port;
		HttpClientOptions options = new HttpClientOptions()
					.setDefaultHost(host)
					.setDefaultPort(port);
		client = vertx.createHttpClient(options);
	}

	public Future<String> createBrush() {	
		logger.log(Level.INFO,"CreateBrush request...");
		Promise<String> p = Promise.promise();
		client
		.request(HttpMethod.POST, "/api/brushes")
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				response.body().onSuccess(buf -> {
					JsonObject obj = buf.toJsonObject();
					p.complete(obj.getString("brushId"));
				});
			});
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		//Effettuo un log di prova.
		sendLogRequest("Ho APPENA FINITO DI ESEGUIRE CREATE BRUSH!");
		return p.future();
	}

	@Override
	public Future<JsonArray> getCurrentBrushes() {
		logger.log(Level.INFO,"GetCurrentBrushes request...");
		Promise<JsonArray> p = Promise.promise();
		client
		.request(HttpMethod.GET, "/api/brushes")
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				response.body().onSuccess(buf -> {
					JsonArray obj = buf.toJsonObject().getJsonArray("brushes");
					p.complete(obj);
				});
			});
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
	}

	@Override
	public Future<JsonObject> getBrushInfo(String brushId) {
		logger.log(Level.INFO,"GetBrushInfo request...");
		Promise<JsonObject> p = Promise.promise();
		client
		.request(HttpMethod.GET, "/api/brushes/" + brushId)
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				response.body().onSuccess(buf -> {
					JsonObject obj = buf.toJsonObject().getJsonObject("brushInfo");
					p.complete(obj);
				});
			});
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
	}

	@Override
	public Future<JsonObject> getPixelGridState() {
		logger.log(Level.INFO,"GetPixelGridState request...");
		Promise<JsonObject> p = Promise.promise();
		client
		.request(HttpMethod.GET, "/api/pixel-grid")
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				response.body().onSuccess(buf -> {
					JsonObject obj = buf.toJsonObject().getJsonObject("pixelGrid");
					p.complete(obj);
				});
			});
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
	}

	@Override
	public Future<Void> moveBrushTo(String brushId, int y, int x) {
		logger.log(Level.INFO,"MoveBrushTo request...");
		Promise<Void> p = Promise.promise();
		client
		.request(HttpMethod.POST, "/api/brushes/" + brushId + "/move-to")
		.onSuccess(request -> {
			request.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				p.complete();
			});
			request.putHeader("content-type", "application/json");
			JsonObject body = new JsonObject();
			body.put("x", x);
			body.put("y", y);		
			String payload = body.encodePrettily();
		    request.putHeader("content-length", "" + payload.length());
			request.write(payload);
			request.end();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
	}

	@Override
	public Future<Void> selectPixel(String brushId) {
		logger.log(Level.INFO,"SelectPixel request...");
		Promise<Void> p = Promise.promise();
		client
		.request(HttpMethod.POST, "/api/brushes/" + brushId + "/select-pixel")
		.onSuccess(request -> {
			request.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				p.complete();
			});
			request.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
	}

	@Override
	public Future<Void> changeBrushColor(String brushId, int color) {
		logger.log(Level.INFO,"ChangeBrushColor request...");
		Promise<Void> p = Promise.promise();
		client
		.request(HttpMethod.POST, "/api/brushes/" + brushId + "/change-color")
		.onSuccess(request -> {
			request.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				p.complete();
			});
			request.putHeader("content-type", "application/json");
			JsonObject body = new JsonObject();
			body.put("color", color);
			String payload = body.encodePrettily();
		    request.putHeader("content-length", "" + payload.length());
			request.write(payload);
			request.end();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
	}

	@Override
	public Future<Void> destroyBrush(String brushId) {
		logger.log(Level.INFO,"DeleteBrush request...");
		Promise<Void> p = Promise.promise();
		client
		.request(HttpMethod.DELETE, "/api/brushes/" + brushId)
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				System.out.println("Received response with status code " + response.statusCode());
				response.body().onSuccess(buf -> {
					p.complete();
				});
			});
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
	}

	@Override
	public Future<JsonObject> subscribePixelGridEvents(PixelGridEventObserver l) {
		logger.log(Level.INFO,"SubscribePixelGridEvents request...");
		Promise<JsonObject> p = Promise.promise();
		
		WebSocketConnectOptions wsoptions = new WebSocketConnectOptions()
				  .setHost(host)
				  .setPort(port)
				  .setURI("/api/pixel-grid/events")
				  .setAllowOriginHeader(false);
		
		client
		.webSocket(wsoptions)
		.onComplete(res -> {
				if (res.succeeded()) {
					WebSocket ws = res.result();
					System.out.println("Connected!");
					ws.frameHandler(frame -> {
						if (frame.isText()) {
							String data = frame.textData();
							JsonObject obj = new JsonObject(data);
							String evType = obj.getString("event");
							if (evType.equals("subscription-started")) {
								JsonObject grid = obj.getJsonObject("pixelGridCurrentState");
								p.complete(grid);
							} else if (evType.equals("pixel-selected")) {
								int x = obj.getInteger("x");
								int y = obj.getInteger("y");
								int color = obj.getInteger("color");
								l.pixelColorChanged(x, y, color);
							}
						}
					});
				} else {
					p.fail(res.cause());
				}
		});
		
		return p.future();
	}

	private Future<Void> sendLogRequest(String messageLog) {
		Promise<Void> p = Promise.promise();

		JsonObject logData = new JsonObject().put("message", messageLog);
		client
				.request(HttpMethod.POST, "/api/log")
				.onSuccess(request -> {
					// Imposta l'header content-type
					request.putHeader("content-type", "application/json");

					// Converti l'oggetto JSON in una stringa e invialo come corpo della richiesta
					String payload = logData.encodePrettily();
					request.putHeader("content-length", "" + payload.length());
					request.write(payload);

					// Gestisci la risposta
					request.response().onSuccess(response -> {
						System.out.println("[Log] Received response with status code " + response.statusCode());
						p.complete();
					}).onFailure(error -> {
						System.err.println("[Log] Error in response: " + error.getMessage());
						p.fail(error.getMessage());
					});

					// Invia la richiesta
					request.end();
				})
				.onFailure(f -> {
					p.fail(f.getMessage());
				});

		return p.future();
	}

}
