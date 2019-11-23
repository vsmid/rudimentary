package hr.yeti.rudimentary.health;

/**
 * <pre>
 * Each health check implementation must return an instance of this class
 * to describe the state of application resource or integration part.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class HealthCheckResponse {

    /**
     * <pre>
     * The name of the health check.
     * e.q. database, file system, service etc.
     * </pre>
     */
    private String name;

    /**
     * Health state indicator.
     */
    private HealthState state;

    /**
     * <pre>
     * Custom health check details.
     * If you need to add some more additional information this is the place to put it.
     * </pre>
     */
    private Object data;

    public HealthCheckResponse(String name, HealthState state, Object data) {
        this.name = name;
        this.state = state;
        this.data = data;
    }

    public HealthState getState() {
        return state;
    }

    public Object getData() {
        return data;
    }

    public String getName() {
        return name;
    }
}
