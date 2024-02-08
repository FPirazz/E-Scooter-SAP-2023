package sap.pixelart.apigateway;

import brave.Tracing;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class APIGatewayServiceLauncher {

    public static void main(String[] args) {

        var sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
        var zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);

        var tracing = Tracing
                .newBuilder()
                .localServiceName("APIGatewayService")
                .addSpanHandler(zipkinSpanHandler)
                .build();

        var tracer = tracing.tracer();

        var span = tracer.startScopedSpan("APIGatewayService-main");

        try {
            APIGatewayService service = new APIGatewayService();
            service.launch();
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