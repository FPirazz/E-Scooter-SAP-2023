package sap.pixelart.service;

import sap.pixelart.service.infrastructure.RestDistributedLogController;

public class DistributedLogService {

	private static int DEFAULT_HTTP_PORT = 9003;
	private RestDistributedLogController restBasedAdapter;
	private int restAPIPort; 
	
	public DistributedLogService() {
    	restAPIPort = DEFAULT_HTTP_PORT;
	}
	
	public void configure(int port) {
		restAPIPort = port;
	}
	
	public void launch() {
		restBasedAdapter = new RestDistributedLogController(restAPIPort);
    	restBasedAdapter.init();
	}
}
