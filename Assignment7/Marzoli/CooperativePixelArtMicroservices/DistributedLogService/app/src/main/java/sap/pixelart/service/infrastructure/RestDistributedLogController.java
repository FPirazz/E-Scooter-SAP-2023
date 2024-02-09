package sap.pixelart.service.infrastructure;

import io.vertx.core.Vertx;

import java.util.logging.Logger;

/**
 * 
 * Adapter implementing a REST API  
 * 
 * - interacting with the application layer through the PixelArtAPI interface 
 * 
 * @author aricci
 *
 */
public class RestDistributedLogController {
    static Logger logger = Logger.getLogger("[Distributed Log]");
	private int port;
	private RestDistributedLogControllerVerticle service;
	
	public RestDistributedLogController(int port) {
		this.port = port;
	}
		
	public void init() {
    	Vertx vertx = Vertx.vertx();
		this.service = new RestDistributedLogControllerVerticle(port);
		vertx.deployVerticle(service);
	}
}
