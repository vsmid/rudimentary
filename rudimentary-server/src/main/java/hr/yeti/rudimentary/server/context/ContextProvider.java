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
import java.util.Objects;
import hr.yeti.rudimentary.security.spi.IdentityDetails;
import hr.yeti.rudimentary.server.http.session.HttpSessionManager;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
      HttpSessionManager.class,
      IdentityDetails.class,
      IdentityStore.class,
      AuthMechanism.class,
      DataSource.class,
      ViewEndpoint.class,
      ViewEngine.class,
      ObjectPool.class,
      Instance.class
  );

  private Map<String, List<String>> instanceDependencyGraph = new HashMap();

  @Override
  public void initialize() {
    // Config before any instance is loaded.
    ServiceLoader.load(Config.class).
        forEach(instance -> {
          this.initializeCaller((Instance) instance);
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
          this.initializeCaller(instance);
        });

  }

  @Override
  public boolean primary() {
    return true;
  }

  public void initializeCaller(Instance instance) {
    if (Objects.nonNull(instance)) {
      Class[] dependsOn = instance.dependsOn();
      for (Class c : dependsOn) {
        if (!isInstanceInitialized(c)) {
          Instance dependencyInstance = (Instance) Instance.of(c);
          if (Objects.nonNull(dependencyInstance)) {
            if (!isInstanceInitialized(dependencyInstance.getClass())) {
              initializeCaller(dependencyInstance);
            }
          } else {
            List providers = Instance.providersOf(c);
            if (Objects.nonNull(providers) && providers.size() > 0) {
              for (Object provider : providers) {
                initializeCaller((Instance) provider);
              }
            }
          }
        }
      }
      if (!isInstanceInitialized(instance.getClass())) {
        instance.initialize();
        setInitialized(instance);
      }
    }
  }

  public void buildInstanceDependenciesGraph() {
    getContext().forEach((key, value) -> {
      instanceDependencyGraph.put(key, List.of(value.dependsOn()).stream().map(Class::getCanonicalName).collect(Collectors.toList()));
    });
  }

  public void checkForCircularDependencies(String rootInstance, String dependencyInstance) {
    List<String> dependencies;

    if (Objects.isNull(dependencyInstance)) {
      dependencies = instanceDependencyGraph.get(rootInstance);
    } else {
      dependencies = instanceDependencyGraph.get(dependencyInstance);
    }

    if (Objects.nonNull(dependencies)) {
      if (dependencies.contains(rootInstance)) {
        throw new ContextException("Circular dependency detected, " + rootInstance + " -> " + dependencyInstance + " -> " + dependencies.toString());
      }

      dependencies.forEach((dependency) -> {
        checkForCircularDependencies(rootInstance, dependency);
      });
    }

  }

}
