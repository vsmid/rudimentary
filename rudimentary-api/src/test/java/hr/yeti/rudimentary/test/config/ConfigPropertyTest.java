package hr.yeti.rudimentary.test.config;

import hr.yeti.rudimentary.config.ConfigException;
import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.test.ConfigMock;
import hr.yeti.rudimentary.test.ContextMock;
import java.net.URI;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigPropertyTest {

  // Mimics configuration instance which will be loaded via ServiceLoader.
  ConfigMock config;

  @BeforeEach
  public void beforeEach() {
    config = new ConfigMock();
  }

  @Test
  public void test_toString_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("val", "Rudy");

    expect:
    assertEquals("Rudy", value.toString());
  }

  @Test
  public void test_value_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("val", "Rudy");

    expect:
    assertEquals("Rudy", value.value());
  }

  @Test
  public void test_value_should_equal_toString_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("val", "Rudy");

    expect:
    assertEquals(value.toString(), value.value());
  }

  @Test
  public void test_asInt_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("val", "1");

    expect:
    assertEquals(1, value.asInt().intValue());
  }

  @Test
  public void test_asLong_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("val", "1");

    expect:
    assertEquals(1l, value.asLong().longValue());
  }

  @Test
  public void test_asBoolean_method() {
    // setup:
    ConfigProperty valueTrue = new ConfigProperty("val", "true");
    ConfigProperty valueFalse = new ConfigProperty("val", "false");

    expect:
    assertEquals(true, valueTrue.asBoolean());
    assertEquals(false, valueFalse.asBoolean());
  }

  @Test
  public void test_asArray_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("val", "   one, one two, three   ");
    String[] array;

    when:
    array = value.asArray();

    then:
    assertNotNull(array);
    assertEquals("one", array[0]);
    assertEquals("one two", array[1]);
    assertEquals("three", array[2]);
  }

  @Test
  public void test_asURI_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("uri", "/api/v1");

    then:
    assertNotNull(value.asURI());
    assertTrue(value.asURI() instanceof URI);
    assertEquals(URI.create("/api/v1"), value.asURI());
  }

  @Test
  public void test_asURL_method() {
    // setup:
    ConfigProperty value = new ConfigProperty("url", "http://localhost:8080/api/v1");

    then:
    assertNotNull(value.asURL());
    assertTrue(value.asURL() instanceof URL);
    assertEquals("http://localhost:8080/api/v1", value.asURL().toString());
  }

  @Test
  public void test_asURL_method_throws_ConfigException() {
    // setup:
    ConfigProperty value = new ConfigProperty("url", "/api/v1");

    expect:
    assertThrows(ConfigException.class, () -> {
      value.asURL();
    });

  }

  @Test
  public void test_asArray_should_be_empty() {
    // setup:
    ConfigProperty value = new ConfigProperty("val", "");
    String[] array;

    when:
    array = value.asArray();

    then:
    assertNotNull(array);
    assertEquals(0, array.length);
  }
}
