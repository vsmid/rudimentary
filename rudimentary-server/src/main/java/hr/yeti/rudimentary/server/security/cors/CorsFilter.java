package hr.yeti.rudimentary.server.security.cors;

import com.sun.net.httpserver.HttpExchange;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import java.io.IOException;

public class CorsFilter extends HttpFilter {

    private ConfigProperty corsEnabled = new ConfigProperty("security.cors.enabled");
    private ConfigProperty corsAllowOrigin = new ConfigProperty("security.cors.allowOrigin");
    private ConfigProperty corsAllowHeaders = new ConfigProperty("security.cors.allowHeaders");
    private ConfigProperty corsExposeHeaders = new ConfigProperty("security.cors.exposeHeaders");
    private ConfigProperty corsAllowCredentials = new ConfigProperty("security.cors.allowCredentials");
    private ConfigProperty corsAllowMethods = new ConfigProperty("security.cors.allowMethods");
    private ConfigProperty corsMaxAge = new ConfigProperty("security.cors.maxAge");

    @Override
    public boolean conditional() {
        return corsEnabled.asBoolean();
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", corsAllowOrigin.value());
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", corsAllowHeaders.value());
        exchange.getResponseHeaders().add("Access-Control-Expose-Headers", corsExposeHeaders.value());
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", corsAllowCredentials.value());
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", corsAllowMethods.value());
        exchange.getResponseHeaders().add("Access-Control-Max-Age", corsMaxAge.value());
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Filter which enables cross origin resource sharing.";
    }

}
