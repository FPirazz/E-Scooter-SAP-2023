package sap.pixelart.service.infrastructure;

import io.vertx.core.Vertx;
import sap.pixelart.service.application.DistributedLogAPI;

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
		
	public void init(DistributedLogAPI distributedLogAPI) {
    	Vertx vertx = Vertx.vertx();
		this.service = new RestDistributedLogControllerVerticle(port, distributedLogAPI);
		vertx.deployVerticle(service);
	}
}
