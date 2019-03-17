package hr.yeti.rudimentary.exception;

import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;

/**
 * <pre>
 * Simple model describing how to respond in case
 * of application exception being thrown.
 * <pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class ExceptionInfo {

  /**
   * Error response http status.
   */
  private int httpStatus;

  /**
   * Error response description message.
   */
  private String description;

  /**
   * Error response details.
   */
  private Object details;

  /**
   * Flag indicating whether default interface implementation of
   * {@link HttpEndpoint#onException(java.lang.Exception)} has been overriden or not.
   * {@link HttpEndpoint#onException(java.lang.Exception)} has greater priority than the one
   * implemented through {@link ExceptionHandler#onException(java.lang.Exception)} if overriden by
   * the given http endpoint provider class.
   */
  private boolean override;

  public ExceptionInfo(int httpStatus, String description) {
    this.httpStatus = httpStatus;
    this.description = description;
    this.override = true;
  }

  public ExceptionInfo(int httpStatus, String description, Object details) {
    this.httpStatus = httpStatus;
    this.description = description;
    this.details = details;
    this.override = true;
  }

  private ExceptionInfo() {
    this.httpStatus = 500;
    this.description = "Internal Server Error.";
    this.override = false;
  }

  /**
   * This method is not intended to be used except internally.
   *
   * @return Default exception info.
   */
  public static ExceptionInfo defaultExceptionInfo() {
    return new ExceptionInfo();
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public String getDescription() {
    return description;
  }

  public Object getDetails() {
    return details;
  }

  public boolean isOverride() {
    return override;
  }

}
