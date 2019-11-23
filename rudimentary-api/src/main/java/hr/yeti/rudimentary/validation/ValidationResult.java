package hr.yeti.rudimentary.validation;

import java.util.Optional;

/**
 * This is the type to be returned by {@link Constraint}.
 *
 * @author vedransmid@yeti-it.hr
 */
public class ValidationResult {

    private boolean valid;
    private Optional<String> reason;

    /**
     * Constructs validation result.
     *
     * @param valid true if constraint is violated, otherwise false.
     * @param reason If the constraint has been violated return the reason of violation.
     */
    public ValidationResult(boolean valid, Optional<String> reason) {
        this.valid = valid;
        this.reason = reason;
    }

    public boolean isValid() {
        return valid;
    }

    public Optional<String> getReason() {
        return reason;
    }

}
