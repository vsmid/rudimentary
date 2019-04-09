package hr.yeti.rudimentary.ext.healthcheck;

import hr.yeti.rudimentary.health.HealthState;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HealthCheckTest {

  HealthCheckEndpoint healthCheckEndpoint;

  @BeforeEach
  public void beforeAll() {
    this.healthCheckEndpoint = new HealthCheckEndpoint();
  }

  @Test
  public void test_http_method_is_GET() {
    expect:
    assertEquals(HttpMethod.GET, healthCheckEndpoint.httpMethod());
  }

  @Test
  @DisplayName("Health endpoint should be available at /health")
  public void test_path_is_health() {
    expect:
    assertEquals("/health", healthCheckEndpoint.path().toString());
  }

  @Test
  public void test_response_id_health_object() {
    HealthCheckReport health;

    when:
    health = healthCheckEndpoint.response(new Request<>(null, null, new Empty(), null, null, null, null));

    then:
    assertNotNull(health);
    assertTrue(health instanceof HealthCheckReport);
    assertEquals(HealthState.UP, ((HealthCheckReport) health).getState());
    assertNotNull(((HealthCheckReport) health).getDetails());
    assertNotNull(((HealthCheckReport) health).getMemoryInfo());
  }

}
