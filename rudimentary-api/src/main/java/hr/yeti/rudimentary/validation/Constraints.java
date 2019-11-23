package hr.yeti.rudimentary.validation;

import hr.yeti.rudimentary.http.content.Json;
import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class holding defined constraints. Each constraint is defined as a {@link ConstraintTuple}.
 *
 * Usages of this class can be seen in {@link HttpEndpoint#constraints(hr.yeti.rudimentary.http.content.Model, java.util.Map, java.util.Map, com.sun.net.httpserver.Headers)} and
 * {@link Model#constraints()}.
 *
 * e.g. Usage example in {@link HttpEndpoint}:
 *
 * <pre>
 * {@code
 *  &#64;Override
 *  public Constraints constraints(
 *    Text body,
 *    Map<String, String> pathVariables,
 *    Map<String, String> queryParameters,
 *    Headers httpHeaders
 *  ){
 *    return new Constraints() {
 *      {
 *        o(body.getValue(), Constraint.NOT_EMPTY);
 *      }
 *    };
 *   }
 * }
 * </pre>
 *
 * @see HttpEndpoint
 * @see Model
 *
 * @author vedransmid@yeti-it.hr
 */
public class Constraints {

    private List<ConstraintTuple> constraints;

    public Constraints() {
        this.constraints = new LinkedList<>();
    }

    /**
     * This constructor should be used when you have {@link Json} request body type and you have an exact type to which you can convert receiving json data. Works for both json arrays and objects.
     *
     * @param json Json data.
     * @param type Type to which json will be converted.
     */
    public Constraints(Json json, Class<? extends Model> type) {
        if (json.isArray()) {
            constraints = json.asListOf(type)
                    .stream()
                    .map(Model::constraints)
                    .map(Constraints::getConstraints)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } else {
            constraints = json.as(type)
                    .constraints()
                    .getConstraints();
        }
    }

    /**
     * Sets constraints for a given value.
     *
     * @param value Value for which an array of constraints will be defined.
     * @param constraint An array of constraints.
     * @return Current constraints instance.
     */
    public Constraints o(Object value, Constraint... constraint) {
        constraints.add(new ConstraintTuple(value, constraint));
        return this;
    }

    /**
     * Sets constraints for a given supplier.
     *
     * @param value for which an array of constraints will be defined.
     * @param constraint An array of constraints.
     * @return Current constraints instance.
     */
    public Constraints o(Supplier value, Constraint... constraint) {
        constraints.add(new ConstraintTuple(value.get(), constraint));
        return this;
    }

    /**
     * Gets a list of defined constraints.
     *
     * @return A list of defined constraints.
     */
    public List<ConstraintTuple> getConstraints() {
        return constraints;
    }

}
