package hr.yeti.rudimentary.server.context;

import hr.yeti.rudimentary.server.http.HttpEndpointContextProvider;
import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.health.spi.HealthCheck;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import hr.yeti.rudimentary.security.spi.AuthMechanism;
import hr.yeti.rudimentary.security.spi.IdentityStore;
import java.util.ServiceLoader;
import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.events.spi.EventListener;
import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.content.handler.spi.ContentHandler;
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import hr.yeti.rudimentary.pooling.spi.ObjectPool;
import java.util.List;
import hr.yeti.rudimentary.security.spi.IdentityDetails;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import hr.yeti.rudimentary.shutdown.spi.ShutdownHook;
import hr.yeti.rudimentary.sql.spi.BasicDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class DefaultContextProvider extends Context {

    private final List<Class<?>> SPIS = List.of(// Top priorities are Config and Context
        // Config.class, omitted, has its own loader
        // Context.class, omitted, has its own loader
        AfterInterceptor.class,
        BeforeInterceptor.class,
        ExceptionHandler.class,
        EventListener.class,
        HealthCheck.class,
        ContentHandler.class,
        HttpEndpoint.class,
        HttpEndpointContextProvider.class,
        HttpFilter.class,
        IdentityDetails.class,
        IdentityStore.class,
        AuthMechanism.class,
        BasicDataSource.class,
        ShutdownHook.class,
        ViewEndpoint.class,
        ViewEngine.class,
        ObjectPool.class,
        Instance.class
    );

    @Override
    public void initLogger() {
        try {
            String propertiesFile = Config.provider().value("logging.propertiesFile");
            InputStream props;

            if (propertiesFile.startsWith("classpath:")) {
                props = new ClasspathResource(propertiesFile.split(":")[1]).get();
            } else {
                props = new FileInputStream(propertiesFile);
            }

            LogManager.getLogManager().readConfiguration(props);
        } catch (IOException | SecurityException ex) {
            logger().log(System.Logger.Level.ERROR, ex);
        }
    }

    @Override
    public Config loadConfig() {
        Config[] config = new Config[]{ null };

        ServiceLoader.load(Config.class)
            .stream()
            .filter(cfg -> cfg.get().conditional())
            .findFirst()
            .ifPresent(provider -> {
                Config configProvider = provider.get();
                this.initializeInstance(configProvider);
                add(configProvider);
                config[0] = configProvider;
            });

        return config[0];
    }

    @Override
    public void initialize() {
        // Create context map.
        SPIS.forEach((clazz) -> {
            ServiceLoader.load(clazz).
                forEach(instance -> {
                    add((Instance) instance);
                });
        });

        // Check for circular dependencies.
        buildInstanceDependenciesGraph();

        instanceDependencyGraph.keySet().forEach((instance) -> {
            checkForCircularDependencies(instance, null);
        });

        // Initialize instances.
        CONTEXT.values()
            .forEach((instance) -> {
                this.initializeInstance(instance);
            });

    }

    @Override
    public boolean primary() {
        return true;
    }

}
