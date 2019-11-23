package hr.yeti.rudimentary.http;

// TODO Should this be used for 404?
/**
 * <pre>
 * Exception type used when requested static resource is not found.
 * This is typically used in MVC.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
