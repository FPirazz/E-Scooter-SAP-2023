package sap.pixelart.service;

import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;

import java.io.IOException;

/**
 * 
 * Cooperative PixelArt Service launcher
 * 
 * @author aricci
 *
 */
public class PixelArtServiceLauncher {
		
    public static void main(String[] args) throws IOException, InterruptedException {

        // Inizializza le metriche JVM
        JvmMetrics.builder().register();

        // Define Prometheus metrics
        Gauge cpuUsage = Gauge.builder()
                .name("cpu_usage_percentage")
                .help("CPU usage percentage")
                .register();

        Gauge heapMemoryUsed = Gauge.builder()
                .name("heap_memory_used_bytes")
                .help("Used heap memory in bytes")
                .register();

        Gauge nonHeapMemoryUsed = Gauge.builder()
                .name("non_heap_memory_used_bytes")
                .help("Used non-heap memory in bytes")
                .register();

    	PixelArtService service = new PixelArtService();

        // Configura e avvia il server HTTP di Prometheus
        HTTPServer server = HTTPServer.builder()
                .port(9012)
                .buildAndStart();

        System.out.println("[CooperativePixelArtService] HTTPServer listening on port http://localhost:" + server.getPort() + "/metrics");

        // Faccio partire il servizio.
    	service.launch(cpuUsage, heapMemoryUsed, nonHeapMemoryUsed);

        // Attendi indefinitamente
        Thread.currentThread().join();
    }
}
