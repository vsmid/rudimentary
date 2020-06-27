package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;
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
public class Redirect extends Model implements ContentValue<URI> {

    private URI redirectUri;
    private int httpStatus;

    public Redirect(String redirectUri) {
        this.redirectUri = URI.create(redirectUri);
        this.httpStatus = 302;
    }

    public Redirect(String redirectUri, int httpStatus) {
        this.redirectUri = URI.create(redirectUri);
        this.httpStatus = httpStatus;
    }

    @Override
    public URI get() {
        return redirectUri;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

}
