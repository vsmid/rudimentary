package hr.yeti.rudimentary.server.security.auth.loginform;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Empty;
import hr.yeti.rudimentary.http.content.Html;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

public class DefaultLoginFormEndpoint implements HttpEndpoint<Empty, Html> {

    private ConfigProperty enabled = new ConfigProperty("security.loginForm.enabled");
    private ConfigProperty loginURI = new ConfigProperty("security.loginForm.loginURI");
    private ConfigProperty redirectAfterSuccessfulLoginURI = new ConfigProperty("security.loginForm.redirectAfterSuccessfulLoginURI");
    private ConfigProperty usernameFieldName = new ConfigProperty("security.loginForm.usernameFieldName");
    private ConfigProperty passwordFieldName = new ConfigProperty("security.loginForm.passwordFieldName");

    @Override
    public String path() {
        return loginURI.value();
    }

    @Override
    public Html response(Request<Empty> request) {
        return new Html("<html>\n"
            + "  <head>\n"
            + "    <title>Login</title>\n"
            + "    <meta charset=\"UTF-8\">\n"
            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            + "  </head>\n"
            + "  <body>\n"
            + "    <form action=\"" + redirectAfterSuccessfulLoginURI.value() + "\" method=\"POST\">\n"
            + "      <input type=\"text\" name=\"" + usernameFieldName.value() + "\">\n"
            + "      <input type=\"password\" name=\"" + passwordFieldName.value() + "\">\n"
            + "      <input type=\"submit\">\n"
            + "    </form>\n"
            + "  </body>\n"
            + "</html>");
    }

    @Override
    public boolean conditional() {
        return enabled.asBoolean();
    }

}
