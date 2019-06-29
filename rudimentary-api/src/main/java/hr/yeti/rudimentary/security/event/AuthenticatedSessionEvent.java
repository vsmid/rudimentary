package hr.yeti.rudimentary.security.event;

import hr.yeti.rudimentary.events.Event;
import hr.yeti.rudimentary.http.session.Session;

public class AuthenticatedSessionEvent implements Event {

  private Session session;

  public AuthenticatedSessionEvent(Session session) {
    this.session = session;
  }

  public Session getSession() {
    return session;
  }

}
