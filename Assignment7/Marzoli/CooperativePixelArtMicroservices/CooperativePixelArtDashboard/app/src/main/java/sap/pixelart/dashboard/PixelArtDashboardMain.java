package sap.pixelart.dashboard;

import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import sap.pixelart.dashboard.controller.Controller;
import sap.pixelart.library.PixelArtAsyncAPI;
import sap.pixelart.library.PixelArtServiceLib;

import java.io.IOException;

public class PixelArtDashboardMain {
	
	public static void main(String[] args) throws IOException, InterruptedException {

		// Inizializza le metriche JVM
		JvmMetrics.builder().register();

		// Inizializzo un instogramma per mostrare la durata della risposta delle mie API.
		Histogram apiResponseTime = Histogram.builder()
				.name("api_response_time_seconds")
				.help("Response time of API calls")
				.labelNames("endpoint")
				.nativeMaxNumberOfBuckets(10)
				.register();


		PixelArtAsyncAPI proxy = PixelArtServiceLib.getInstance().getDefaultInterface(apiResponseTime);
		Controller controller = new Controller(proxy);

		// Configura e avvia il server HTTP di Prometheus
		HTTPServer server = HTTPServer.builder()
				.port(9011)
				.buildAndStart();

		System.out.println("[Dashboard] HTTPServer listening on port http://localhost:" + server.getPort() + "/metrics");

		controller.init();

		// Attendi indefinitamente
		Thread.currentThread().join();
	}
}
