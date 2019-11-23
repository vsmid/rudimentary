package hr.yeti.rudimentary.server.http.session.listener;

import hr.yeti.rudimentary.events.spi.EventListener;
import hr.yeti.rudimentary.security.event.AuthenticatedSessionEvent;
import hr.yeti.rudimentary.server.http.session.HttpSession;

public class AuthenticatedSessionEventListener implements EventListener<AuthenticatedSessionEvent> {

    @Override
    public void onEvent(AuthenticatedSessionEvent event) {
        ((HttpSession) event.getSession()).setAuthenticated(true);
        ((HttpSession) event.getSession()).setIdentity(event.getIdentity());
    }

}
