package sap.pixelart.apigateway;

import brave.Tracing;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;
import io.prometheus.metrics.core.metrics.Counter;
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
public class APIGatewayServiceLauncher {

    public static void main(String[] args) throws InterruptedException, IOException {
        var sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
        var zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);

        var tracing = Tracing
                .newBuilder()
                .localServiceName("APIGatewayService")
                .addSpanHandler(zipkinSpanHandler)
                .build();

        var tracer = tracing.tracer();

        var span = tracer.startScopedSpan("APIGatewayService-main");

        // Inizializza le metriche JVM
        JvmMetrics.builder().register();

        // Inizializza e registra un contatore personalizzato
        Counter myCountTotal = Counter.builder()
                .name("API_Gateway_http_requests_total")
                .help("Total number of HTTP requests")
                .labelNames("method", "path", "status")
                .register();

        try {
            APIGatewayService service = new APIGatewayService();

            // Configura e avvia il server HTTP di Prometheus
            HTTPServer server = HTTPServer.builder()
                    .port(9010)
                    .buildAndStart();

            System.out.println("[API Gateway] HTTPServer listening on port http://localhost:" + server.getPort() + "/metrics");

            service.launch(myCountTotal);

            // Attendi indefinitamente
            Thread.currentThread().join();
        } catch (Exception ex) {
            span.error(ex);
        } finally {
            span.finish();
        }

        tracing.close();
        zipkinSpanHandler.close();
        sender.close();
    }
}