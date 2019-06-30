package hr.yeti.rudimentary.server.security.auth.loginform;

import hr.yeti.rudimentary.config.ConfigProperty;
import hr.yeti.rudimentary.http.HttpMethod;
import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.http.content.Form;
import hr.yeti.rudimentary.http.content.Redirect;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.net.URI;

public class RedirectAfterSuccessfulLoginEndpoint implements HttpEndpoint<Form, Redirect> {

  private ConfigProperty redirectAfterSuccessfulLoginURI = new ConfigProperty("security.loginform.redirectAfterSuccessfulLoginURI");
  private ConfigProperty landingViewURI = new ConfigProperty("security.loginform.landingViewURI");

  @Override
  public HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @Override
  public URI path() {
    return URI.create(redirectAfterSuccessfulLoginURI.value());
  }

  @Override
  public Redirect response(Request<Form> request) {
    String redirectToURI = landingViewURI.value().length() == 0
        ? request.getSession().getAttributes().getOrDefault("sessionCreatingURI", "/").toString() : landingViewURI.value();
    return new Redirect(URI.create(redirectToURI));
  }

}
