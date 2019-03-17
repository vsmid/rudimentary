package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.http.MediaType;
import java.io.InputStream;

/**
 * <pre>
 * Class used to describe http response type when declaring
 * new {@link HttpEndpoint} provider. This type of response is
 * used for returning resources such as javascript or css files.
 *
 * Project rumdimentary-server provides {@link HttpEndpoint} called {@code StaticResourceEndpoint}
 * which has response type set to {@link StaticResource}. This endpoint is heavily used in MVC when
 * rendered HTML pages contain links to javascript or css files.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public final class StaticResource extends Model implements Value<InputStream> {

  private InputStream staticResource;
  private String mediaType;

  /**
   * @param staticResource Resource to be loaded.
   * @param mediaType Type of resource, e.q. for javascript set to
   * {@link MediaType#APPLICATION_JAVASCRIPT}, for others {@link MediaType#APPLICATION_OCTET_STREAM}
   */
  public StaticResource(InputStream staticResource, String mediaType) {
    this.staticResource = staticResource;
    this.mediaType = mediaType;
  }

  @Override
  public InputStream getValue() {
    return staticResource;
  }

  public String getMediaType() {
    return mediaType;
  }

}
