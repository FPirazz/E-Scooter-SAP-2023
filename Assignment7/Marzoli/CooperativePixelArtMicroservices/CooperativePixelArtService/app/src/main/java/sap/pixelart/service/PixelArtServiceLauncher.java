package sap.pixelart.service;

import brave.Tracing;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * Cooperative PixelArt Service launcher
 *
 * @author aricci
 */
public class PixelArtServiceLauncher {

    public static void main(String[] args) {

        // Configure a reporter, which controls how often spans are sent
        var sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
        var zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);

        // Create a tracing component with the service name you want to see in Zipkin.
        var tracing = Tracing
                .newBuilder()
                .localServiceName("PixelArtService")
                .addSpanHandler(zipkinSpanHandler)
                .build();

        // Tracing exposes objects you might need, most importantly the tracer
        var tracer = tracing.tracer();

        // Start a new trace or a span within an existing trace representing an operation
        var span = tracer.startScopedSpan("PixelArtService-main");

        try {
            PixelArtService service = new PixelArtService();
            service.launch();
        } catch (Exception ex) {
            span.error(ex); // Unless you handle exceptions, you might not know the operation failed!
        } finally {
            span.finish(); // always finish the span
        }

        // Failing to close resources can result in dropped spans! When tracing is no
        // longer needed, close the components you made in reverse order. This might be
        // a shutdown hook for some users.
        tracing.close();
        zipkinSpanHandler.close();
        sender.close();
    }
}