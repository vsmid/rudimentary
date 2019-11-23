package hr.yeti.rudimentary.pooling;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.pooling.spi.ObjectPool;

/**
 * Basic pool configuration class mandatory for any pool. These properties should be available to the application via {@link Config} but it is not mandatory. To see how these properties are being used
 * by the framework itself using {@link Config}, take a look at the
 * <b>rudimentary-exts/rudimentary-sql-sqlite-ext</b> module implementation.
 *
 * @author vedransmid@yeti-it.hr
 */
public class ObjectPoolSettings {

    private int minSize;
    private int maxSize;
    private long validationInterval;
    private int awaitTerminationInterval;

    /**
     *
     * @param minSize Set the minimal size of the pool.
     * @param maxSize Set the maximal size of the pool.
     * @param validationInterval Period of time in seconds between two pool state validation checks. During validation checks, a pool is populated with new instances if the current number of instances
     * in the pool is lower than minSize or instances are removed from the pool if the current number of instances in the pool is greater than maxSize. For more technical details on validation check
     * implementation of {@link ObjectPool#initialize()}.
     * @param awaitTerminationInterval Period of time in seconds object pool will wait before it terminates itself.
     */
    public ObjectPoolSettings(int minSize, int maxSize, long validationInterval, int awaitTerminationInterval) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.validationInterval = validationInterval;
        this.awaitTerminationInterval = awaitTerminationInterval;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public long getValidationInterval() {
        return validationInterval;
    }

    public int getAwaitTerminationInterval() {
        return awaitTerminationInterval;
    }

}
