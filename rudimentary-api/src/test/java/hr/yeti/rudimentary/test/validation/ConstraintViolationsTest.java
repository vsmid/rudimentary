package hr.yeti.rudimentary.test.validation;

import hr.yeti.rudimentary.validation.ConstraintViolations;
import hr.yeti.rudimentary.validation.ValidationResult;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConstraintViolationsTest {

    @Test
    @DisplayName("Should add only failed validations")
    public void test_add_violation() {
        ConstraintViolations violations = new ConstraintViolations();

        when:
        violations.add(new ValidationResult(true, Optional.empty()));
        violations.add(new ValidationResult(false, Optional.empty()));

        then:
        assertTrue(violations.getList().size() == 1);
        assertFalse(violations.getList().get(0).isValid());

    }
}
