package hr.yeti.rudimentary.ext.healthcheck;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.health.HealthCheckResponse;
import hr.yeti.rudimentary.health.HealthState;
import hr.yeti.rudimentary.health.spi.HealthCheck;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HealthCheckEndpoint implements HttpEndpoint<Empty, HealthCheckReport> {

  private static final long MB = 1024 * 1024;

  private List<HealthCheck> healthCheckProviders = new ArrayList<>();

  @Override
  public void initialize() {
    Instance.providersOf(HealthCheck.class)
        .forEach(healthCheckProviders::add);
  }

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @Override
  public URI path() {
    return URI.create("/health");
  }

  @Override
  public HealthCheckReport response(Request<Empty> request) {

    List<HealthCheckResponse> healthChecks = healthCheckProviders.stream()
        .map(HealthCheck::call)
        .collect(Collectors.toList());

    MemoryInfo memoryInfo = new MemoryInfo(
        Runtime.getRuntime().totalMemory() / MB,
        Runtime.getRuntime().maxMemory() / MB,
        Runtime.getRuntime().maxMemory() / MB
    );

    boolean down = healthChecks.stream().anyMatch(healthCheckResponse -> healthCheckResponse.getState() == HealthState.DOWN);

    HealthCheckReport healthCheckReport = new HealthCheckReport(down ? HealthState.DOWN : HealthState.UP, memoryInfo, healthChecks);

    return healthCheckReport;
  }

  @Override
  public String description() {
    return "Show service's health status like memory consumption.";
  }

}
