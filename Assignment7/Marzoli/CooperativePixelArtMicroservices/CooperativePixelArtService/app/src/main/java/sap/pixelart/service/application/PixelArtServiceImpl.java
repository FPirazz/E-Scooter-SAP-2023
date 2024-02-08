package sap.pixelart.service.application;

import brave.Tracer;
import brave.Tracing;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.pixelart.service.domain.Brush;
import sap.pixelart.service.domain.BrushManager;
import sap.pixelart.service.domain.PixelGrid;
import sap.pixelart.service.domain.PixelGridEventObserver;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * Application layer implemetation
 * <p>
 * - using the domain layer
 *
 * @author aricci
 */
public class PixelArtServiceImpl implements PixelArtAPI {

    private BrushManager brushManager;
    private PixelGrid grid;
    private int brushCounter;
    private Tracer tracer;

    public void init() {
        brushCounter = 0;
        brushManager = new BrushManager();
        grid = new PixelGrid(40, 40);

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
        tracer = tracing.tracer();
    }

    @Override
    public String createBrush() {
        var span = tracer.startScopedSpan("createBrush");
        try {
            brushCounter++;
            String brushId = "brush-" + brushCounter;
            brushManager.addBrush(brushId, new Brush(0, 0, 0));
            return brushId;
        } finally {
            span.finish();
        }
    }

    @Override
    public JsonArray getCurrentBrushes() {
        var span = tracer.startScopedSpan("getCurrentBrushes");
        try {
            var c = brushManager.getBrushesId();
            JsonArray list = new JsonArray();
            for (String s : c) {
                list.add(s);
            }
            return list;
        } finally {
            span.finish();
        }
    }

    @Override
    public void moveBrushTo(String brushId, int y, int x) {
        var span = tracer.startScopedSpan("moveBrushTo");
        try {
            Brush b = brushManager.getBrush(brushId);
            b.updatePosition(x, y);
        } finally {
            span.finish();
        }
    }

    @Override
    public void selectPixel(String brushId) {
        var span = tracer.startScopedSpan("selectPixel");
        try {
            Brush b = brushManager.getBrush(brushId);
            grid.set(b.getX(), b.getY(), b.getColor());
        } finally {
            span.finish();
        }
    }

    @Override
    public void changeBrushColor(String brushId, int color) {
        var span = tracer.startScopedSpan("changeBrushColor");
        try {
            Brush b = brushManager.getBrush(brushId);
            b.setColor(color);
        } finally {
            span.finish();
        }
    }

    @Override
    public void destroyBrush(String brushId) {
        var span = tracer.startScopedSpan("destroyBrush");
        try {
            brushManager.removeBrush(brushId);
        } finally {
            span.finish();
        }
    }

    @Override
    public JsonObject getBrushInfo(String brushId) {
        var span = tracer.startScopedSpan("getBrushInfo");
        try {
            Brush b = brushManager.getBrush(brushId);
            JsonObject info = new JsonObject();
            info.put("brushId", brushId);
            info.put("color", b.getColor());
            info.put("x", b.getX());
            info.put("y", b.getY());
            return info;
        } finally {
            span.finish();
        }
    }

    @Override
    public JsonObject getPixelGridState() {
        var span = tracer.startScopedSpan("getPixelGridState");
        try {
            JsonObject info = new JsonObject();
            info.put("numColumns", grid.getNumColumns());
            info.put("numRows", grid.getNumRows());
            JsonArray pixels = new JsonArray();
            for (int y = 0; y < grid.getNumRows(); y++) {
                for (int x = 0; x < grid.getNumColumns(); x++)
                    pixels.add(grid.get(x, y));
            }
            info.put("pixels", pixels);
            return info;
        } finally {
            span.finish();
        }
    }

    @Override
    public void subscribePixelGridEvents(PixelGridEventObserver l) {
        var span = tracer.startScopedSpan("subscribePixelGridEvents");
        try {
            grid.addPixelGridEventListener(l);
        } finally {
            span.finish();
        }
    }

    @Override
    public boolean verifyHealth() {
        return false;
    }
}
