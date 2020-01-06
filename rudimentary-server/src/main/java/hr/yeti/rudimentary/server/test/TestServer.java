package hr.yeti.rudimentary.server.test;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.EventPublisher;
import hr.yeti.rudimentary.events.spi.EventListener;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.URIUtils;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.security.spi.AuthMechanism;
import hr.yeti.rudimentary.security.spi.IdentityDetails;
import hr.yeti.rudimentary.security.spi.IdentityStore;
import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import hr.yeti.rudimentary.server.http.processor.HttpProcessor;
import hr.yeti.rudimentary.server.mvc.DefaultStaticHTMLViewEngine;
import hr.yeti.rudimentary.sql.spi.BasicDataSource;
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
        private List<Class<? extends BeforeInterceptor>> beforeInterceptors = new ArrayList<>();
        private List<Class<? extends AfterInterceptor>> afterInterceptors = new ArrayList<>();
        private List<Class<? extends EventListener>> eventListeners = new ArrayList<>();
        private List<Class<? extends HttpFilter>> httpFilters = new ArrayList<>();
        private List<Class<? extends BasicDataSource>> dataSources = new ArrayList<>();
        private List<Class<? extends Instance>> instances = new ArrayList<>();
        private Class<? extends AuthMechanism> authMechanism;
        private Class<? extends IdentityStore> identityStore;
        private Class<? extends IdentityDetails> identityDetails;
        private Class<? extends ViewEngine> viewEngine;
        private Class<? extends ExceptionHandler> exceptionHandler;

        private HttpServer server;
        private HttpContext httpContext;

        public Builder config(Map<String, String> config) {
            this.config.putAll(config);
            return this;
        }

        public Builder exceptionHandler(Class<? extends ExceptionHandler> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public Builder beforeInterceptors(Class<? extends BeforeInterceptor>... beforeInterceptors) {
            if (Objects.nonNull(beforeInterceptors)) {
                this.beforeInterceptors.addAll(
                    Arrays.asList(beforeInterceptors)
                );
            }
            return this;
        }

        public Builder afterInterceptors(Class<? extends AfterInterceptor>... afterInterceptors) {
            if (Objects.nonNull(afterInterceptors)) {
                this.afterInterceptors.addAll(
                    Arrays.asList(afterInterceptors)
                );
            }
            return this;
        }

        public Builder eventListeners(Class<? extends EventListener>... eventListeners) {
            if (Objects.nonNull(eventListeners)) {
                this.eventListeners.addAll(
                    Arrays.asList(eventListeners)
                );
            }
            return this;
        }

        public Builder dataSources(Class<? extends BasicDataSource>... dataSources) {
            if (Objects.nonNull(dataSources)) {
                this.dataSources.addAll(
                    Arrays.asList(dataSources)
                );
            }
            return this;
        }

        public Builder httpFilters(Class<? extends HttpFilter>... httpFilters) {
            if (Objects.nonNull(httpFilters)) {
                this.httpFilters.addAll(
                    Arrays.asList(httpFilters)
                );
            }
            return this;
        }

        public Builder afterInterceptors(Class<? extends ExceptionHandler> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public Builder viewEngine(Class<? extends ViewEngine> viewEngine) {
            this.viewEngine = viewEngine;
            return this;
        }

        public Builder httpEndpoints(Class<? extends HttpEndpoint>... httpEndpoints) {
            if (Objects.nonNull(httpEndpoints)) {
                this.httpEndpoints.addAll(
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

        public Builder authMechanism(
            Class<? extends AuthMechanism> authMechanism,
            Class<? extends IdentityStore> identityStore,
            Class<? extends IdentityDetails> identityDetails
        ) {
            if (Objects.nonNull(authMechanism)) {
                this.authMechanism = authMechanism;
                if (Objects.nonNull(identityStore)) {
                    this.identityStore = identityStore;
                    this.identityDetails = identityDetails;
                }
            }
            return this;
        }

        public Builder instances(Class<? extends Instance>... instances) {
            if (Objects.nonNull(instances)) {
                this.instances.addAll(
                    Arrays.asList(instances)
                );
            }
            return this;
        }

        public TestServer build() {
            assignPort();

            if (!httpEndpoints.isEmpty()) {
                providers.addAll(httpEndpoints);
            }

            if (!beforeInterceptors.isEmpty()) {
                providers.addAll(beforeInterceptors);
            }

            if (!afterInterceptors.isEmpty()) {
                providers.addAll(afterInterceptors);
            }

            if (!eventListeners.isEmpty()) {
                providers.add(EventPublisher.class);
                providers.addAll(eventListeners);
            }

            if (!httpFilters.isEmpty()) {
                providers.addAll(httpFilters);
            }

            if (!dataSources.isEmpty()) {
                providers.addAll(dataSources);
            }

            if (!viewEndpoints.isEmpty()) {
                if (Objects.isNull(viewEngine)) {
                    viewEngine = DefaultStaticHTMLViewEngine.class;
                }
                providers.add(viewEngine);
                providers.addAll(viewEndpoints);
            }

            if (Objects.nonNull(exceptionHandler)) {
                providers.add(exceptionHandler);
            }

            if (Objects.nonNull(authMechanism)) {
                providers.add(authMechanism);
                if (Objects.nonNull(identityStore)) {
                    providers.add(identityStore);
                    if (Objects.nonNull(identityDetails)) {
                        providers.add(identityDetails);
                    }
                }
                if (eventListeners.isEmpty()) {
                    providers.add(EventPublisher.class);
                }
            }

            if (!instances.isEmpty()) {
                providers.addAll(instances);
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
            if (Objects.nonNull(authMechanism)) {
                httpContext.setAuthenticator(Instance.of(AuthMechanism.class));
            }

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
