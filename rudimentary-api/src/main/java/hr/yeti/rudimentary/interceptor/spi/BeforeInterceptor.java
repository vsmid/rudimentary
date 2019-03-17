package hr.yeti.rudimentary.interceptor.spi;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.interceptor.Interceptor;
import java.util.ServiceLoader;

/**
 * <pre>
 * Since this interface inherently extends {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 *
 * You can have as many different BeforeInterceptor implementations as you want and you can register
 * them in <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor</i>
 * file.
 *
 * The order of after interceptors is defined by implementing {@link Interceptor#order()} method.
 * The lowest the number, the higher priority is.
 *
 * There is a number which gurantees that user defined after interceptors will not be execute
 * before the one's that framework uses internally. That number is defined with the
 * value {@link Interceptor#APPLICATION_PRIORITY} and is the first available value you can use.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public interface BeforeInterceptor extends Interceptor {

  /**
   * Implement interceptor's logic.
   *
   * @param request Incoming HTTP request in {@link Request} form.
   */
  void intercept(Request request);

}
