package hr.yeti.rudimentary.validation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Definition of the constraint as a function. Each constraint definition must return
 * {@link ValidationResult}. These constraints can later be applied to any given value. Default
 * constraints are defined in this class. You can freely extends this interface and create your own
 * set of custom constraints.
 *
 * @author vedransmid@yeti-it.hr
 */
public interface Constraint extends Function<Object, ValidationResult> {

  static Constraint NOT_NULL = (o) -> new ValidationResult(Objects.nonNull(o), Optional.of(o + " can not be null."));
  static Constraint NOT_EMPTY = (o) -> new ValidationResult(Objects.nonNull(o) ? !o.equals("") : true, Optional.of(o + " can not be empty."));

}
