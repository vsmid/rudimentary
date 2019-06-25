package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.io.InputStream;
import hr.yeti.rudimentary.http.stream.StreamOutWriteDef;

/**
 * <pre>
 * Class used to describe both http request and http response type when declaring
 * new {@link HttpEndpoint} provider. This class contains methods for both incoming and outgoing streams.
 *
 * <b>Use cases:</b>
 *
 * <ul>
 *  <li>Outgoing stream: streaming infinite data out or downloading a document.</li>
 *  <li>Incoming stream: file upload.</li>
 * </ul>
 *
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class ByteStream extends Model implements Value<InputStream> {

  /**
   *
   * Implementation of how data is written to output stream.
   */
  private StreamOutWriteDef streamOutWriteDef;

  /**
   *
   * Incoming data stream as {@link InputSteram}.
   */
  private InputStream inputStream;

  private ByteStream() {

  }

  /**
   *
   * @param inputStream Incoming data stream.
   */
  public ByteStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  /**
   *
   * @param streamOutWriteDef Implementation of how data is written to output stream.
   */
  public ByteStream(StreamOutWriteDef streamOutWriteDef) {
    this.streamOutWriteDef = streamOutWriteDef;
  }

  /**
   *
   * @return Implementation of how data is written to output stream.
   */
  public StreamOutWriteDef getStreamOutWriteDef() {
    return streamOutWriteDef;
  }

  /**
   *
   * @return Incoming data stream as {@link InputSteram}.
   */
  @Override
  public InputStream getValue() {
    return this.inputStream;
  }

}
