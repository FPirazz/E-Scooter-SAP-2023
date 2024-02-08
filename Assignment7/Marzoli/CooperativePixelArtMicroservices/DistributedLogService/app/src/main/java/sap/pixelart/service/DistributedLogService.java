package sap.pixelart.service;

import sap.pixelart.service.application.DistributedLogAPIImpl;
import sap.pixelart.service.infrastructure.RestDistributedLogController;

public class DistributedLogService {

	private static int DEFAULT_HTTP_PORT = 9003;

	private DistributedLogAPIImpl service;
	private RestDistributedLogController restBasedAdapter;
	private int restAPIPort; 
	
	public DistributedLogService() {
    	service = new DistributedLogAPIImpl();
    	restAPIPort = DEFAULT_HTTP_PORT;
	}
	
	public void configure(int port) {
		restAPIPort = port;
	}
	
	public void launch() {
		restBasedAdapter = new RestDistributedLogController(restAPIPort);
    	restBasedAdapter.init(service);
	}
}
