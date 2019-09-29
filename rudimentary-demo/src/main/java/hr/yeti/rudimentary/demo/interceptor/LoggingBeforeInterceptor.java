package hr.yeti.rudimentary.demo.interceptor;

import hr.yeti.rudimentary.http.Request;
import hr.yeti.rudimentary.interceptor.spi.BeforeInterceptor;

public class LoggingBeforeInterceptor implements BeforeInterceptor {

  @Override
  public int order() {
    return 1;
  }

  @Override
  public String applyToURI() {
    return "/_health";
  }

  @Override
  public void intercept(Request request) {
    System.out.println("Logging interceptor message: " + request.getUri().toString());
  }

}
