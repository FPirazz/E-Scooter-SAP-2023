package sap.pixelart.apigateway;

/**
 * 
 * Cooperative PixelArt Service launcher
 * 
 * @author aricci
 *
 */

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;

import java.io.IOException;

public class APIGatewayServiceLauncher {
		
    public static void main(String[] args) throws InterruptedException, IOException {
        // Inizializza le metriche JVM
        JvmMetrics.builder().register();

        // Inizializza e registra un contatore personalizzato
        Counter myCountTotal = Counter.builder()
                .name("API_Gateway_http_requests_total")
                .help("Total number of HTTP requests")
                .labelNames("method", "path", "status")
                .register();

        // Inizializza il tuo servizio
        APIGatewayService service = new APIGatewayService();

        // Configura e avvia il server HTTP di Prometheus
        HTTPServer server = HTTPServer.builder()
                .port(9010)
                .buildAndStart();

        System.out.println("[API Gateway] HTTPServer listening on port http://localhost:" + server.getPort() + "/metrics");

        // Esegui la tua logica di applicazione
        service.launch(myCountTotal);

        // Attendi indefinitamente
        Thread.currentThread().join();

    }
}
