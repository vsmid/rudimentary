package hr.yeti.rudimentary.interceptor;

import hr.yeti.rudimentary.context.spi.Instance;

/**
 * Base class for all interceptors.
 *
 * @author vedransmid@yeti-it.hr
 */
public interface Interceptor extends Instance {

  /**
   * First available number which you can use to set interceptor priority order. Interceptor with
   * order value = 1000 will be execute first and so on. This is valid only for interceptors
   * implemented by application and not framework.
   */
  public static final int APPLICATION_PRIORITY = 1000;

  /**
   * Set interceptor priority order.
   *
   * @return Interceptor priority order.
   */
  int order();

}
