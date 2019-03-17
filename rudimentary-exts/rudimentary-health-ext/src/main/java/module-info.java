import hr.yeti.rudimentary.ext.healthcheck.HealthCheckEndpoint;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

module hr.yeti.rudimentary.ext.health {
  uses hr.yeti.rudimentary.health.spi.HealthCheck;
  requires hr.yeti.rudimentary.api;

  exports hr.yeti.rudimentary.ext.healthcheck;

  provides HttpEndpoint with HealthCheckEndpoint;
}
