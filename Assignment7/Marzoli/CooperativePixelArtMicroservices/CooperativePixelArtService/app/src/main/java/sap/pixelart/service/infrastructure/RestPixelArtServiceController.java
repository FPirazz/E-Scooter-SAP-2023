package sap.pixelart.service.infrastructure;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import sap.pixelart.service.application.PixelArtAPI;
import sap.pixelart.service.domain.PixelGridEventObserver;

import java.util.logging.Level;
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
public class RestPixelArtServiceController implements PixelGridEventObserver {
    static Logger logger = Logger.getLogger("[WebUIAdapter]");	
	private int port;
	private RestPixelArtServiceControllerVerticle service;
	
	public RestPixelArtServiceController(int port) {	
		this.port = port;
	}
		
	public void init(PixelArtAPI pixelArtAPI) {
    	Vertx vertx = Vertx.vertx();
		//Mi collego al bus per comunicare col microservizio di log.
		EventBus eventBus = vertx.eventBus();
		this.service = new RestPixelArtServiceControllerVerticle(port, pixelArtAPI, eventBus);
		vertx.deployVerticle(service);	
		pixelArtAPI.subscribePixelGridEvents(this);
	}

	/* called by the application layer */
	
	@Override
	public void pixelColorChanged(int x, int y, int color) {
		logger.log(Level.INFO, "New PixelGrid event - pixel selected " + x + " " + y + " " + color);
		service.pixelColorChanged(x, y, color);
	}

}
