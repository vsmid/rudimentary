package hr.yeti.rudimentary.health.spi;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.health.HealthCheckResponse;
import java.util.ServiceLoader;

/**
 * <pre>
 * Since this interface extends {@link Instance} it means it is loaded automatically via
 * {@link ServiceLoader} on application startup.
 *
 * You can have as many different HealthCheck implementations as you want and you can register
 * them in <i>src/main/resources/META-INF/services/hr.yeti.rudimentary.health.spi.HealthCheck</i>
 * file.
 *
 * Each of these health checks will be exposed via application's
 * dedicated http endpoint {@code /health}. This endpoint is provided via an extension and can be
 * found under modules with name {@code rudimentary-health-ext}.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public interface HealthCheck extends Instance {

  /**
   * <pre>
   * Implementation of this method will be executed to gain health check information about some
   * application resource, integration part or whatever else you think is worth having as a part of
   * application health check.
   * </pre>
   *
   * @return health check into in the form of {@link HealthCheckResponse}.
   */
  HealthCheckResponse call();
}
