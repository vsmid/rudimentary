package hr.yeti.rudimentary.interceptor;

import hr.yeti.rudimentary.context.spi.Instance;

/**
 * Base class for all interceptors.
 *
 * @author vedransmid@yeti-it.hr
 */
public interface Interceptor extends Instance {

    /**
     * Set interceptor priority order.
     *
     * @return Interceptor priority order.
     */
    int order();

    /**
     * Set regular expression as string to which this interceptor will be applied to.
     *
     * @return String based regular expression.
     */
    String applyToURI();

}
