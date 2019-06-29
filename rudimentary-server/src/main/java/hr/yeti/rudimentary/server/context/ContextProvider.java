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
import hr.yeti.rudimentary.http.filter.spi.HttpFilter;
import hr.yeti.rudimentary.interceptor.spi.AfterInterceptor;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import hr.yeti.rudimentary.pooling.spi.ObjectPool;
import java.util.List;
import hr.yeti.rudimentary.security.spi.IdentityDetails;
import javax.sql.DataSource;

public class ContextProvider extends Context {

  private final List<Class<?>> SPIS = List.of(// Top priorities are Config and Context
      // Config.class, omitted, has its own loader
      // Context.class, omitted, has its own loader
      AfterInterceptor.class,
      BeforeInterceptor.class,
      ExceptionHandler.class,
      EventListener.class,
      HealthCheck.class,
      HttpEndpoint.class,
      HttpEndpointContextProvider.class,
      HttpFilter.class,
      IdentityDetails.class,
      IdentityStore.class,
      AuthMechanism.class,
      DataSource.class,
      ViewEndpoint.class,
      ViewEngine.class,
      ObjectPool.class,
      Instance.class
  );

  @Override
  public void initialize() {
    // Config before any instance is loaded.
    ServiceLoader.load(Config.class).
        forEach(instance -> {
          this.initializeInstance((Instance) instance);
          add((Instance) instance);
        });

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
    getContext().values()
        .forEach((instance) -> {
          this.initializeInstance(instance);
        });

  }

  @Override
  public boolean primary() {
    return true;
  }

}
