package hr.yeti.rudimentary.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.events.Event;

public class AcquireSessionEvent implements Event {

    private HttpExchange exchange;
    private Session session;

    public AcquireSessionEvent(HttpExchange exchange) {
        this.exchange = exchange;
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

}
