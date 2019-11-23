package hr.yeti.rudimentary.shutdown.spi;

import hr.yeti.rudimentary.context.spi.Instance;
import java.util.ServiceLoader;

/**
 * SPI used to add on shutdown hook logic.
 *
 * Since this interface extends {@link Instance} it means it is loaded automatically via {@link ServiceLoader} on application startup.
 *
 * There should be only one ShutdownHook provider per application.
 *
 * You can register it in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.shutdown.spi.Instance</i>
 * file.
 *
 * @author vedransmid@yeti-it.hr.
 */
public interface ShutdownHook extends Instance {

    /**
     * <pre>
     * Implement this method to add logic which will be executed on shutdown. This method will be executed
     * right after server is stopped(no new incoming exchanges are accepted) and before context is destroyed.
     * Shutdown hook will wait for its execution server.stopDelay(property value) seconds.
     * </pre>
     */
    void onShutdown();

}
