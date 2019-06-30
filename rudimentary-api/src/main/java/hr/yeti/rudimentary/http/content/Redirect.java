package hr.yeti.rudimentary.http.content;

import java.net.URI;

/**
 * <pre>
 * Class used to describe http response type when declaring
 * new {@link HttpEndpoint} provider.
 *
 * Use this response type if you want to perform redirect.
 *
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class Redirect extends Model implements Value<URI> {

  private URI redirectUri;
  private int httpStatus;

  public Redirect(URI redirectUri) {
    this.redirectUri = redirectUri;
    this.httpStatus = 302;
  }

  public Redirect(URI redirectUri, int httpStatus) {
    this.redirectUri = redirectUri;
    this.httpStatus = httpStatus;
  }

  @Override
  public URI getValue() {
    return redirectUri;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

}
