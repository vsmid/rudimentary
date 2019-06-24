package hr.yeti.rudimentary.http.content;

import java.io.InputStream;

/**
 * <pre>
 * Class used to describe http request type when declaring
 * new {@link HttpEndpoint} provider. T
 * Typical use cases would be file upload or streaming infinite data in.
 *
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class StreamIn extends Model implements Value<InputStream> {

  private InputStream inputStream;

  private StreamIn() {
  }

  /**
   *
   * @param inputStream Incoming data stream.
   */
  public StreamIn(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public InputStream getValue() {
    return this.inputStream;
  }

}
