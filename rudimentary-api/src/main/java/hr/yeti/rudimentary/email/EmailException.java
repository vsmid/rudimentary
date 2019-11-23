package hr.yeti.rudimentary.email;

/**
 * Exception thrown if something goes wrong during {@link Email#send(hr.yeti.rudimentary.email.EmailMessage)} method execution.
 *
 * @see Email#send(hr.yeti.rudimentary.email.EmailMessage)
 *
 * @author vedransmid@yeti-it.hr
 */
public class EmailException extends RuntimeException {

    public EmailException(Throwable cause) {
        super(cause);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
