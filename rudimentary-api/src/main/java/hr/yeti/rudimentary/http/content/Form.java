package hr.yeti.rudimentary.http.content;

import java.util.Map;

/**
 * <pre>
 * Class used to describe http request type when declaring
 * new {@link HttpEndpoint} provider.
 *
 * This class marks http request which would have content type header set
 * to application/x-www-form-urlencoded.
 *
 * Form values received will be available as {@link Map}.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public final class Form extends Model implements Value< Map<String, Object>> {

  private Map<String, Object> value;

  /**
   * @param value Value received through request body.
   */
  public Form(Map<String, Object> value) {
    this.value = value;
  }

  @Override
  public Map<String, Object> getValue() {
    return value;
  }

}
