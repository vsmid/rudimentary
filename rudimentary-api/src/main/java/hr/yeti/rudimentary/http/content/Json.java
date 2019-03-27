package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

/**
 * <pre>
 * Class used to describe http request/response type when declaring
 * new {@link HttpEndpoint} provider.This class marks http request which would have
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

  /**
   * Cast single JSON object to a typed POJO.
   *
   * @param <T> Type of class.
   * @param type Class to which JSON object is converted.
   * @return POJO.
   */
  public <T> T as(Class<T> type) {
    if (isArray()) {
      throw new RuntimeException("Json value is an array and can not be converted to POJO.");
    }
    return JsonbBuilder.create().fromJson(this.value.toString(), type);
  }

  /**
   * Cast JSON array to a list of typed POJOs.
   *
   * @param <T> Type of class.
   * @param type Class to which JSON object is converted.
   * @return A list of POJOs.
   */
  public <T> List<T> asListOf(Class<T> type) {
    if (!isArray()) {
      throw new RuntimeException("Json value is not an array.");
    }
    final List<T> typedList = new ArrayList();
    Jsonb builder = JsonbBuilder.create();
    this.value.asJsonArray().forEach((json) -> {
      T typedJson = builder.fromJson(json.toString(), type);
      typedList.add(typedJson);
    });
    return typedList;
  }

  /**
   * @return true if this JSON is array, false otherwise.
   */
  public boolean isArray() {
    return this.value.getValueType() == JsonValue.ValueType.ARRAY;
  }

}
