package hr.yeti.rudimentary.server.http.session;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.HttpRequestUtils;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.http.session.Session;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InactiveHttpSessionFilter extends HttpFilter {

    private static final Logger LOGGER = Logger.getLogger(InactiveHttpSessionFilter.class.getName());

    private ConfigProperty inactivityPeriodAllowed = new ConfigProperty("session.inactivityPeriodAllowed");

    @Override
    public int order() {
        return 20;
    }

    @Override
    public String description() {
        return "Filter which checks whether session was inactive for a preconfigured period of time and then invalidates if so.";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        Map<String, HttpCookie> cookies = HttpRequestUtils.parseCookies(exchange.getRequestHeaders());

        if (!cookies.isEmpty() && cookies.containsKey(Session.COOKIE)) {
            String RSID = cookies.get(Session.COOKIE).getValue();
            Session session = (Session) exchange.getAttribute(RSID);

            if (Objects.nonNull(session)) {
                long lastAccessedTime = session.getLastAccessedTime();
                long currentTime = System.currentTimeMillis();

                if ((currentTime - lastAccessedTime) / 1_000 > inactivityPeriodAllowed.asInt()) {
                    LOGGER.log(Level.WARNING, "Session with RSID={0} has expired after period of inactivity.", RSID);

                    // Invalidate and remove session.
                    session.invalidate(exchange);

                    exchange.sendResponseHeaders(440, 0);
                    exchange.close();

                    return;
                }
            }
        }

        chain.doFilter(exchange);
    }

}
