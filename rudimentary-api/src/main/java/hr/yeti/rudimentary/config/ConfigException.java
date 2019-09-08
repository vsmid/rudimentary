package hr.yeti.rudimentary.config;

/**
 * Exception thrown if something goes wrong during configuration setup or usage.
 *
 * @author vedransmid@yeti-it.hr
 */
public class ConfigException extends RuntimeException {

  public ConfigException(Throwable cause) {
    super(cause);
  }

  public ConfigException(String message, Throwable cause) {
    super(message, cause);
  }

}
