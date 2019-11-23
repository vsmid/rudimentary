package hr.yeti.rudimentary.validation;

import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.http.spi.HttpEndpoint;
import java.util.List;

/**
 * Validator class used to validate constraints.
 * <p>
 * Notable usages of this validator are {@link HttpEndpoint#constraints(hr.yeti.rudimentary.http.content.Model, java.util.Map, java.util.Map, com.sun.net.httpserver.Headers)} and
 * {@link Model#constraints()}. Even though Validator usage it is not visible in the above classes it is used internally by the module <b>rudimentary/rudimentary-server</b> during HTTP request
 * processing.
 *
 * @see Constraint
 *
 * @author vedransmid@yeti-it.hr
 */
public class Validator {

    /**
     * Executes validation of the given constraints.
     *
     * @param constraints An array of {@link Constraints} to be validated.
     * @return {@link ConstraintViolations}.
     */
    public static ConstraintViolations validate(Constraints... constraints) {
        ConstraintViolations constraintViolations = new ConstraintViolations();

        for (Constraints constraint : constraints) {
            constraint.getConstraints().forEach((constraintDefinition) -> {
                List.of(constraintDefinition.getConstraints())
                        .forEach(
                                c -> {
                                    ValidationResult validationResult = c.apply(constraintDefinition.getValue());
                                    if (!validationResult.isValid()) {
                                        constraintViolations.add(validationResult);
                                    }
                                });
            });
        }

        return constraintViolations;
    }

}
