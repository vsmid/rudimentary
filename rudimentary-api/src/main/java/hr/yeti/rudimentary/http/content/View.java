package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.context.spi.Instance;
import hr.yeti.rudimentary.mvc.spi.ViewEndpoint;
import hr.yeti.rudimentary.mvc.spi.ViewEngine;
import java.util.Map;

/**
 * <pre>
 * Class used to describe http response type when declaring
 * new {@link HttpEndpoint} provider.
 *
 * This class marks http request which would have accept header (response) set to text/html.
 *
 * This content type is heavily used in MVC because it sets
 * response type of {@link ViewEndpoint} provider to it by default.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public final class View extends Model implements ContentValue<String> {

    private String templatePath;
    private Map<String, Object> context;

    /**
     * @param templatePath Path to HTML template.
     */
    public View(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * @param templatePath Path to HTML template.
     * @param context A map of values to be applied against HTML template.
     */
    public View(String templatePath, Map<String, Object> context) {
        this.templatePath = templatePath;
        this.context = context;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * @return A string HTML representation of the view to be sent in a response. For this, an instance {@link ViewEngine] provider is used.
     */
    @Override
    public String get() {
        return Instance.of(ViewEngine.class)
            .render(this);
    }

}
