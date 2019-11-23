package hr.yeti.rudimentary.security.event;

import hr.yeti.rudimentary.events.Event;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.security.Identity;

public class AuthenticatedSessionEvent implements Event {

    private Session session;
    private Identity identity;

    public AuthenticatedSessionEvent(Session session, Identity identity) {
        this.session = session;
        this.identity = identity;
    }

    public Session getSession() {
        return session;
    }

    public Identity getIdentity() {
        return identity;
    }

}
