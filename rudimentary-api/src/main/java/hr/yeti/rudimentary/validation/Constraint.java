package hr.yeti.rudimentary.validation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

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

    static Constraint NOT_EMPTY = (o) -> new ValidationResult(Objects.nonNull(o) && !o.equals(""), Optional.of(o + " can not be empty."));

    static Constraint MIN(int value) {
        return (o) -> new ValidationResult(Objects.nonNull(o) && Integer.valueOf(o.toString()) >= value, Optional.of(o + " < " + value + "."));
    }

    static Constraint MAX(int value) {
        return (o) -> new ValidationResult(Objects.nonNull(o) && Integer.valueOf(o.toString()) <= value, Optional.of(o + " > " + value + "."));
    }

    static Constraint MIN_LENGTH(int value) {
        return (o) -> new ValidationResult(Objects.nonNull(o) && o.toString().length() >= value, Optional.of("String length is " + o.toString().length() + ". Minimum allowed length is " + value + "."));
    }

    static Constraint MAX_LENGTH(int value) {
        return (o) -> new ValidationResult(Objects.nonNull(o) && o.toString().length() <= value, Optional.of("String length is " + o.toString().length() + ". Maximum allowed value is " + value + "."));
    }

    static Constraint REGEX(Pattern pattern) {
        return (o) -> new ValidationResult(pattern.matcher(o.toString()).matches(), Optional.of(o + " does not match pattern of " + pattern.pattern()));
    }
}
