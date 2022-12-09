package performance;

import java.util.Date;

public class RequestStatistics {
    private final Date startTime;
    private final RequestType requestType;
    private final int latency;
    private final int responseCode;
    private boolean signal;

    public RequestStatistics(Date startTime, RequestType requestType, int latency, int responseCode, boolean signal) {
        this.startTime = startTime;
        this.requestType = requestType;
        this.latency = latency;
        this.responseCode = responseCode;
        this.signal = signal;
    }
    public RequestStatistics(Date startTime, RequestType requestType, int latency, int responseCode) {
        this(startTime, requestType, latency, responseCode, false);
    }

    public RequestStatistics() {
        this(null, null, 0, 0, true);
    }

    public Date getStartTime() {
        return startTime;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public int getLatency() {
        return latency;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean isSignal() {
        return this.signal;
    }
}
