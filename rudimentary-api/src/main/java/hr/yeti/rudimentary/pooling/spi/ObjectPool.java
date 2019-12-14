package hr.yeti.rudimentary.pooling.spi;

import hr.yeti.rudimentary.pooling.ObjectPoolException;
import hr.yeti.rudimentary.pooling.ObjectPoolSettings;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import hr.yeti.rudimentary.context.spi.Instance;
import java.sql.Connection;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * A SPI for providing object pooling. Most notable and currently only usage is a {@link JdbcConnectionPool}.
 *
 * Since this abstract class implements {@link Instance} it means it is loaded automatically via {@link ServiceLoader}
 * on application startup.
 *
 * You can have as many different ObjectPool providers as you want and you can register them in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.pooling.spi.ObjectPool</i>
 * file.
 *
 * If you have you own dedicated pool SPI such as {@link JdbcConnectionPool} you will then probably want to use a
 * different file under
 * <i>src/main/resources/META-INF/services</i> to register it.
 *
 * @author vedransmid@gmail.com
 * @param <T> A type managed by the pool e.g. {@link Connection}.
 */
public abstract class ObjectPool<T> implements Instance {

    /**
     * Object pool's status.
     */
    public enum PoolStatus {
        UP, DOWN
    }

    private ConcurrentLinkedQueue<T> pool;
    private ScheduledExecutorService executorService;
    private ObjectPoolSettings settings;

    public ObjectPool() {

    }

    /**
     * Call this method to pull the managed object from the pull.
     *
     * @return An object managed by the pool.
     */
    public T borrow() {
        T object;
        if ((object = pool.poll()) == null) {
            object = createObject();
        }
        return object;
    }

    /**
     * Call this method to return used managed object to the pool.
     *
     * @param object An object to be returned to the pool.
     */
    public void release(T object) {
        if (object == null) {
            return;
        }
        pool.offer(object);
    }

    /**
     * Implement how the object is created.
     *
     * @return A new instance of the managed type.
     * @throws ObjectPoolException
     */
    protected abstract T createObject() throws ObjectPoolException;

    /**
     * Sets basic configuration of the pool.
     *
     * @return Settings of the pool.
     */
    protected abstract ObjectPoolSettings settings();

    /**
     * Do not call this method unless you know what you are doing. It will be called by the framework on application
     * startup.
     *
     * Starts thread which keeps the state of the pool between the boundaries set by the minSize and maxSize values of
     * the {@link ObjectPoolSettings}.
     */
    @Override
    public void initialize() {
        this.settings = settings();
        executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleWithFixedDelay(() -> {
            int size = pool.size();

            if (size < settings.getMinSize()) {
                int objectsToBeAdded = settings.getMinSize() + size;
                for (int i = 0; i < objectsToBeAdded; i++) {
                    pool.add(createObject());
                }
            } else if (size > settings.getMaxSize()) {
                int objectToBeRemoved = size - settings.getMaxSize();
                for (int i = 0; i < objectToBeRemoved; i++) {
                    pool.poll();
                }
            }
        }, settings.getValidationInterval(), settings.getValidationInterval(), TimeUnit.SECONDS);

        pool = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < settings.getMinSize(); i++) {
            pool.add(createObject());
        }
    }

    /**
     * Do not call this method unless you know what you are doing. It will be called by the framework on application
     * shutdown.
     */
    @Override
    public void destroy() {
        try {
            executorService.awaitTermination(settings.getAwaitTerminationInterval(), TimeUnit.SECONDS);
            executorService.shutdown();
            settings = null;
            pool = null;
        } catch (InterruptedException ex) {
            Logger.getLogger(ObjectPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return Current number of objects contained within the object pool.
     */
    public int objectCount() {
        return pool.size();
    }

    /**
     * @return Current status of the pool, up or down.
     */
    public PoolStatus status() {
        return executorService.isShutdown() && Objects.isNull(settings) && Objects.isNull(pool)
            ? PoolStatus.DOWN : PoolStatus.UP;
    }

}
