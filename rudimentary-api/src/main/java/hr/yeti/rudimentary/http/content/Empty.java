package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.http.spi.HttpEndpoint;

/**
 * <pre>
 * Class used to describe http request/response type when declaring
 * new {@link HttpEndpoint} provider.
 *
 * This class marks request which has no request body or empty response.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
final public class Empty extends Model {

    /**
     * Convenient static access to default empty instance.
     * This should be used when returning empty response.
     */
    public static final Empty INSTANCE = new Empty();
}
