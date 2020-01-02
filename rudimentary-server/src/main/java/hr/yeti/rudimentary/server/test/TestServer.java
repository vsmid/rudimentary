package hr.yeti.rudimentary.server.test;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.URIUtils;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import hr.yeti.rudimentary.server.http.processor.HttpProcessor;
import hr.yeti.rudimentary.server.mvc.DefaultStaticHTMLViewEngine;
import hr.yeti.rudimentary.test.ContextMock;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class TestServer {

    private static Set<Integer> portsBound = new HashSet<>();

    private Builder builder;

    private TestServer(Builder builder) {
        this.builder = builder;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void start() {
        builder.server.start();
    }

    public void stop() {
        builder.getServer().stop(Config.provider().property("server.stopDelay").asInt());
        portsBound.remove(Config.provider().property("server.port").asInt());
    }

    public URI buildUri(String uri) {
        URI resolvedUri = URIUtils.removeSlashPrefix(URI.create(uri));
        return URI.create("http://localhost:" + Config.provider().value("server.port") + "/" + resolvedUri.toString());
    }

    public static class Builder {

        private Random portGenerator = new Random();
        private Context context;
        private Map<String, String> config = new HashMap<>() {
            {
                put("server.threadPoolSize", "10");
                put("server.stopDelay", "0");
                put("mvc.templatesDir", "view");
                put("mvc.staticResourcesDir", "static");
            }
        };
        private Set<Class<? extends Instance>> providers = new HashSet<>() {
            {
                add(HttpProcessor.class);
                add(HttpEndpointContextProvider.class);
            }
        };
        private List<Class<? extends HttpEndpoint>> httpEndpoints = new ArrayList<>();
        private List<Class<? extends ViewEndpoint>> viewEndpoints = new ArrayList<>();
        private Class<? extends ViewEngine> viewEngine;
        private HttpServer server;
        private HttpContext httpContext;

        public Builder config(Map<String, String> config) {
            this.config.putAll(config);
            return this;
        }

        public Builder viewEngine(Class<? extends ViewEngine> viewEngine) {
            this.viewEngine = viewEngine;
            return this;
        }

        public Builder httpEndpoints(Class<? extends HttpEndpoint>... httpEndpoints) {
            if (Objects.nonNull(httpEndpoints)) {
                providers.addAll(
                    Arrays.asList(httpEndpoints)
                );
            }
            return this;
        }

        public Builder viewEndpoints(Class<? extends ViewEndpoint>... viewEndpoints) {
            if (Objects.nonNull(viewEndpoints)) {
                this.viewEndpoints.addAll(
                    Arrays.asList(viewEndpoints)
                );
            }
            return this;
        }

        public TestServer build() {
            assignPort();

            if (!viewEndpoints.isEmpty() && Objects.isNull(viewEngine)) {
                viewEngine = DefaultStaticHTMLViewEngine.class;
                providers.addAll(viewEndpoints);
            }

            if (Objects.nonNull(viewEngine)) {
                providers.add(viewEngine);
            }

            this.context = new ContextMock(
                this.config,
                providers.toArray(Class[]::new)
            );

            try {
                server = HttpServer.create(
                    new InetSocketAddress(Config.provider().property("server.port").asInt()),
                    Config.provider().property("server.threadPoolSize").asInt()
                );
            } catch (IOException e) {

            }

            httpContext = server.createContext("/", Instance.of(HttpProcessor.class));

            return new TestServer(this);
        }

        private void assignPort() {
            int port = portGenerator.nextInt(9000 - 8000 + 1) + 8000;
            if (!portsBound.contains(port)) {
                config.put("server.port", String.valueOf(port));
                portsBound.add(port);
            } else {
                assignPort();
            }
        }

        public Map<String, String> getConfig() {
            return config;
        }

        public Context getContext() {
            return context;
        }

        public HttpServer getServer() {
            return server;
        }

    }
}
