package hr.yeti.rudimentary.test;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.context.spi.Context;
import hr.yeti.rudimentary.context.spi.Instance;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class used for testing purposes to create mocked context on the fly.
 *
 * @author vedransmid@yeti-it.hr
 */
public final class ContextMock extends Context {

    private List<Instance> instances = new ArrayList<>();

    /**
     * Create context from classes.Use this constructor if instances do depend on some configuration properties.
     *
     * @param properties Configuration properties.
     * @param instances An instance of class implementing {@link Instance}.
     */
    public ContextMock(Map<String, String> properties, Class<? extends Instance>... instances) {
        destroy();
        initializeConfig(properties);

        for (Class<? extends Instance> instance : instances) {
            try {
                this.instances.add(instance.getDeclaredConstructor().newInstance());
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                logger().log(System.Logger.Level.ERROR, ex);
            }
        }

        initialize();
    }

    @Override
    public void initialize() {
        instances.forEach(super::add);

        super.buildInstanceDependenciesGraph();
        instanceDependencyGraph.keySet().forEach((instance) -> {
            super.checkForCircularDependencies(instance, null);
        });

        CONTEXT.values()
            .forEach((instance) -> {
                super.initializeInstance(instance);
            });
    }

    // Method used to test protected Context#isInstanceInitialized method.
    public boolean initialized(Class<?> clazz) {
        return super.isInstanceInitialized(clazz);
    }

    // Method used to test instanceDependencyGraph map.
    public Map<String, List<String>> getInstanceDependencyGraph() {
        return instanceDependencyGraph;
    }

    private void initializeConfig(Map<String, String> properties) {
        Config config = new ConfigMock().load(properties);
        config.seal();

        if (Objects.nonNull(config)) {
            super.initializeInstance(config);
            super.add(config);
        }
    }

    @Override
    public boolean primary() {
        return false;
    }

    @Override
    public void initLogger() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Config loadConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
