package sap.pixelart.service;

/**
 * 
 * Cooperative PixelArt Service launcher
 * 
 * @author aricci
 *
 */
public class DistributedLogServiceLauncher {
		
    public static void main(String[] args) {

    	DistributedLogService service = new DistributedLogService();
    	service.launch();
    }
}
