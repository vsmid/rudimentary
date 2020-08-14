package hr.yeti.rudimentary.http.content.handler;

public class ContentHandlerException extends RuntimeException {

    public ContentHandlerException(String message) {
        super(message);
    }

    public ContentHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

}
