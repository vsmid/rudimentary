package hr.yeti.rudimentary.pooling;

import hr.yeti.rudimentary.pooling.spi.ObjectPool;

/**
 * An exception thrown is something goes wrong during {@link ObjectPool#createObject()} method
 * execution.
 *
 * @author vedransmid@yeti-it.hr
 */
public class ObjectPoolException extends RuntimeException {

  public ObjectPoolException(String message) {
    super(message);
  }

  public ObjectPoolException(String message, Throwable cause) {
    super(message, cause);
  }

}
