package hr.yeti.rudimentary.http.content;

/**
 * <pre>
 * Class used to describe http request/response type when declaring
 * new {@link HttpEndpoint} provider.
 *
 * This class marks http request which would have
 * content type header (request) set to text/html or accept header (response) set to text/html.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public final class Html extends Model implements ContentValue<String> {

    private String value;

    /**
     * @param value Value received through request body or value to be sent in a response.
     */
    public Html(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }

}
