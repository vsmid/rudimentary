package hr.yeti.rudimentary.mvc;

import hr.yeti.rudimentary.mvc.spi.ViewEngine;

/**
 * An exception thrown during {@link ViewEngine#render(hr.yeti.rudimentary.http.content.View)}
 * method execution if something goes wrong.
 *
 * @author vedransmid@yeti-it.hr
 */
public class ViewEngineException extends RuntimeException {

  public ViewEngineException(String message) {
    super(message);
  }

}
