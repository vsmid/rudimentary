package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.http.stream.StreamOutDef;

/**
 * <pre>
 * Class used to describe http response type when declaring
 * new {@link HttpEndpoint} provider. This type of response is
 * used for streaming response. Typicall uses would be streaming infinite data or downloading a document.
 *
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class StreamOut extends Model {

  private StreamOutDef streamOutDef;

  private StreamOut() {

  }

  /**
   *
   * @param streamOutDef Implementation of how data is written to output stream.
   */
  public StreamOut(StreamOutDef streamOutDef) {
    this.streamOutDef = streamOutDef;
  }

  public StreamOutDef getStreamOutDef() {
    return streamOutDef;
  }

}
