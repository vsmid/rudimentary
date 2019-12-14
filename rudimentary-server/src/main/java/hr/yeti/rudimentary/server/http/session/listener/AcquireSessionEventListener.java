package hr.yeti.rudimentary.server.http.session.listener;

import hr.yeti.rudimentary.events.spi.EventListener;
import hr.yeti.rudimentary.http.Cookie;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.session.AcquireSessionEvent;
import hr.yeti.rudimentary.http.session.Session;
import hr.yeti.rudimentary.server.http.session.HttpSession;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AcquireSessionEventListener implements EventListener<AcquireSessionEvent> {

    @Override
    public void onEvent(AcquireSessionEvent event) {
        Session session = null;
        Map<String, HttpCookie> cookies = HttpRequestUtils.parseCookies(event.getExchange().getRequestHeaders());

        // This part handles RSID tokens which are not stored in server's memory
        // by creating new session and overwriting unknown RSID cookie.
        boolean overwriteRsidCookie = false;
        if (cookies.containsKey(Session.COOKIE)) {

            session = (Session) event.getExchange().getAttribute(cookies.get(Session.COOKIE).getValue());

            if (Objects.isNull(session)) {
                overwriteRsidCookie = true;
            }
        }

        if (event.isCreateIfAbsent()) {
            if (Objects.isNull(session)) {
                HttpSession newSession = new HttpSession();
                event.getExchange().setAttribute(newSession.getRsid(), newSession);

                String rsid = newSession.getRsid();

                HttpCookie rsidCookie = new HttpCookie(Session.COOKIE, rsid);
                rsidCookie.setHttpOnly(true);
                rsidCookie.setMaxAge(-1);

                event.getExchange().getResponseHeaders().add("Set-Cookie", new Cookie(rsidCookie).toString());

                List<String> rawRequestCookies = event.getExchange().getRequestHeaders().get("Cookie");
                if (overwriteRsidCookie) {
                    rawRequestCookies.clear();
                    rawRequestCookies.add(Session.COOKIE + "=" + rsid);
                }
                // This is the first URI triggered by user which created session. Useful for login form
                // authentication to know to which page to land after successful login. 
                // This is true if requested URI is secured.
                // TODO Conditionally set based on LogiFormAuthenticationMechanism settings.
                newSession.getAttributes().put(Session.DEEP_LINK_URI, event.getExchange().getRequestURI().toString());
                session = newSession;
            }
        }

        if (Objects.nonNull(session)) {
            ((HttpSession) session).setLastAccessedTime(System.currentTimeMillis());
        }

        event.setSession(session);
    }

}
