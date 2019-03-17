package hr.yeti.rudimentary.exception.spi;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.exception.ExceptionInfo;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.util.ServiceLoader;

/**
 * <pre>
 * An instance of this SPI will act as an exception handler for any exception ocurred during
 * {@link HttpEndpoint#response(hr.yeti.rudimentary.http.Request)} method execution.
 * Other parts of request processing already have their own, standardized
 * way of reporting exceptions depending on the processing phase.
 *
 * In the future this may(and is very likely) be extended to the whole request processing part to
 * enable full customization of exception handling through provider of this SPI.
 *
 * Since this interface extends {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 * There should be only one ExceptionHandler provider per application and you can register it in
 * <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.exception.spi.ExceptionHandler</i>
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public interface ExceptionHandler extends Instance {

  /**
   * <pre>
   * Implement this method to handle any exception you like. This basically means setting the http
   * status and dedicated error description based on exception thrown.
   * </pre>
   *
   * @param e Exception thrown by the application.
   * @return Custom {@link ExceptionInfo} description depending on the type of thrown exception.
   */
  ExceptionInfo onException(Exception e);
}
