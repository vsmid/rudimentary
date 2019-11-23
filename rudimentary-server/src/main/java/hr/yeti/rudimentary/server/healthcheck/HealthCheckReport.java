package hr.yeti.rudimentary.server.healthcheck;

import hr.yeti.rudimentary.health.HealthState;
import hr.yeti.rudimentary.http.content.Model;

public class HealthCheckReport extends Model {

    private HealthState state;
    private MemoryInfo memoryInfo;
    private Object details;

    public HealthCheckReport(HealthState state, MemoryInfo memoryInfo, Object details) {
        this.state = state;
        this.memoryInfo = memoryInfo;
        this.details = details;
    }

    public HealthState getState() {
        return state;
    }

    public MemoryInfo getMemoryInfo() {
        return memoryInfo;
    }

    public Object getDetails() {
        return details;
    }
}
