package sap.pixelart.service.domain;

public class LogEntry {
    private String source;
    private String message;

    public LogEntry(String source, String message) {
        this.source = source;
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
