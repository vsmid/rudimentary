package hr.yeti.rudimentary.http.content;

import hr.yeti.rudimentary.validation.Constraints;

/**
 * <pre>
 * Base class for all request/response content types.
 *
 * All already available content types extend this class and if you would like to create a custom
 * content type you should do it by extending this class.
 *
 * Custom model types will be treated as JSON for both request body and response.
 * This means that content type header would be application/json and accept header
 * would be application/json.
 *
 * XML type is currently not supported.
 * </pre>
 *
 * @author vedransmid@yeti-it.hr
 */
public abstract class Model {

  /**
   * Each class extending model gains a feature of defining validation constraints on each of the
   * properties defined in the class.
   *
   * Example:
   *
   * <pre>
   * public class Person extends Model {
   *
   *    private String name;
   *    private String description;
   *
   *    &#64;Override
   *    public Constraints constraints() {
   *      return new Constraints() {
   *        {
   *          o(name, NOT_NULL);
   *          o(description, NOT_NULL, NOT_EMPTY);
   *        }
   *      };
   *    }
   * }
   * </pre>
   *
   * @return {@link Constraints}
   */
  public Constraints constraints() {
    return new Constraints();
  }

}
