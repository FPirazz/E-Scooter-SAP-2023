package sap.pixelart.apigateway.infrastructure;

import io.prometheus.metrics.core.metrics.Counter;
import io.vertx.core.Vertx;
import sap.pixelart.library.PixelArtAsyncAPI;

import java.util.logging.Logger;

public class APIGatewayController {
    static Logger logger = Logger.getLogger("[APIGatewayController]");	
	private int port;
	private APIGatewayControllerVerticle verticle;
	
	public APIGatewayController(int port) {	
		this.port = port;
	}
		
	public void init(PixelArtAsyncAPI pixelArtAPI, Counter prometheusCounter) {
    	Vertx vertx = Vertx.vertx();
		this.verticle = new APIGatewayControllerVerticle(port, pixelArtAPI, prometheusCounter);
		vertx.deployVerticle(verticle);	
	}

}
