package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.util.List;
import java.util.Map;
import javax.json.JsonValue;

/**
 * <pre>
 * Class used to describe http request/response type when declaring
 * new {@link HttpEndpoint} provider.
 *
 * This class marks http request which would have
 * content type header (request) set to application/json
 * or accept header (response) set to application/json.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public final class Json extends Model implements Value<JsonValue> {

  private JsonValue value;

  /**
   * @param value Value received through request body or value to sent in a response.
   */
  public Json(JsonValue value) {
    this.value = value;
  }

  /**
   * Used for setting response body when you want to return single JSON object.
   *
   * @param value A value that will internally be converted to JSON and sent in a response.
   */
  public Json(Map<String, Object> value) {
    this.value = javax.json.Json.createObjectBuilder(value).build();
  }

  /**
   * Used for setting response body when you want to return an array of JSON objects.
   *
   * @param value A value that will internally be converted to JSON and sent in a response.
   */
  public Json(List<Map<String, Object>> value) {
    this.value = javax.json.Json.createArrayBuilder(value).build();
  }

  @Override
  public JsonValue getValue() {
    return value;
  }

}
