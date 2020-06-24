package hr.yeti.rudimentary.server.config;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import hr.yeti.rudimentary.server.resources.ClasspathResource;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.TreeMap;

public class ConfigEndpoint implements HttpEndpoint<Empty, Html> {

    private String configHTML;

    @Override
    public void initialize() {
        try {
            this.configHTML = new String(
                new ClasspathResource("/templates/config.html")
                    .get()
                    .readAllBytes()
            );
        } catch (IOException ex) {
            logger().log(Level.ERROR, "Failed to load config.html template.", ex);
        }
    }

    @Override
    public HttpMethod httpMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String path() {
        return "/_config";
    }

    @Override
    public int httpStatus() {
        return 200;
    }

    @Override
    public Html response(Request<Empty> request) {
        return new Html(
            String.format(configHTML,
                rowsHTMLGenerator()
            )
        );
    }

    @Override
    public String description() {
        return "Lists current application configuration properties.";
    }

    private String rowsHTMLGenerator() {
        StringBuilder rowsHTML = new StringBuilder();
        TreeMap props = new TreeMap(Config.provider().getProperties());

        props.forEach((key, value) -> {
            rowsHTML.append(String.format("<tr><td>%s</td><td>%s</td></tr>", key, value));
        });

        return rowsHTML.toString();
    }

}
