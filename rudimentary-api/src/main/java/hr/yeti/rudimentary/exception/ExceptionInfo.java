package hr.yeti.rudimentary.exception;

import hr.yeti.rudimentary.exception.spi.ExceptionHandler;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.nio.charset.StandardCharsets;

/**
 * <pre>
 * Simple model describing how to respond in case
 * of application exception being thrown.
 * </pre>
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
    private byte[] content;

    /**
     * Flag indicating whether default interface implementation of {@link HttpEndpoint#onException(java.lang.Exception)} has been overridden or not.
     * {@link HttpEndpoint#onException(java.lang.Exception)} has greater priority than the one implemented through {@link ExceptionHandler#onException(java.lang.Exception)} if overridden by the given
     * http endpoint provider class.
     */
    private boolean override;

    public ExceptionInfo(int httpStatus, byte[] content) {
        this.httpStatus = httpStatus;
        this.content = content;
        this.override = true;
    }

    private ExceptionInfo() {
        this.httpStatus = 500;
        this.content = "Internal Server Error.".getBytes(StandardCharsets.UTF_8);
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

    public byte[] getContent() {
        return content;
    }

    public boolean isOverride() {
        return override;
    }

}
