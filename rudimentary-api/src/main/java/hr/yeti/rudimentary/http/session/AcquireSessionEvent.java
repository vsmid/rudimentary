package hr.yeti.rudimentary.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.events.Event;

public class AcquireSessionEvent implements Event {

    private HttpExchange exchange;
    private Session session;
    private boolean createIfAbsent;

    public AcquireSessionEvent(HttpExchange exchange, boolean createIfAbsent) {
        this.exchange = exchange;
        this.createIfAbsent = createIfAbsent;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isCreateIfAbsent() {
        return createIfAbsent;
    }

}
