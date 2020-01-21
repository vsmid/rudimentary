package hr.yeti.rudimentary.validation;

import hr.yeti.rudimentary.i18n.I18n;
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

    static Constraint NOT_NULL = (o) -> new ValidationResult(
        Objects.nonNull(o),
        Optional.of(
            I18n.text("constraint.notNull", "Can not be null.", o))
    );

    static Constraint NOT_EMPTY = (o) -> new ValidationResult(
        Objects.nonNull(o) && !o.equals(""),
        Optional.of(
            I18n.text("constraint.notEmpty", "Can not be empty.", o))
    );

    static Constraint MIN(int value) {
        return (o) -> new ValidationResult(
            Objects.nonNull(o) && Integer.valueOf(o.toString()) >= value,
            Optional.of(
                I18n.text("constraint.min", "{0} < {1} .", o, value))
        );
    }

    static Constraint MAX(int value) {
        return (o) -> new ValidationResult(
            Objects.nonNull(o) && Integer.valueOf(o.toString()) <= value,
            Optional.of(
                I18n.text("constraint.max", "{0} > {1} .", o, value))
        );
    }

    static Constraint MIN_LENGTH(int value) {
        return (o) -> new ValidationResult(
            Objects.nonNull(o) && o.toString().length() >= value,
            Optional.of(
                I18n.text("constraint.minLength", "String length is {0}. Minimum allowed length is {1}.", o.toString().length(), value))
        );
    }

    static Constraint MAX_LENGTH(int value) {
        return (o) -> new ValidationResult(
            Objects.nonNull(o) && o.toString().length() <= value,
            Optional.of(
                I18n.text("constraint.maxLength", "String length is {0}. Maximum allowed length is {1}.", o.toString().length(), value))
        );
    }

    static Constraint REGEX(Pattern pattern) {
        return (o) -> new ValidationResult(
            pattern.matcher(o.toString()).matches(),
            Optional.of(
                I18n.text("constraint.pattern", "{0} does not match pattern of {1}", o, pattern.pattern()))
        );
    }
}
