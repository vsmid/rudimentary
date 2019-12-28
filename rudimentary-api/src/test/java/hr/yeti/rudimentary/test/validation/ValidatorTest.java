package hr.yeti.rudimentary.test.validation;

import static hr.yeti.rudimentary.validation.Constraint.NOT_EMPTY;
import static hr.yeti.rudimentary.validation.Constraint.NOT_NULL;
import hr.yeti.rudimentary.validation.ConstraintViolations;
import hr.yeti.rudimentary.validation.Constraints;
import hr.yeti.rudimentary.validation.Validator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ValidatorTest {

    @Test
    @DisplayName("Should validate constraints")
    public void test_validate() {
        ConstraintViolations constraintViolations;
        String value = null;

        when:
        constraintViolations = Validator.validate(new Constraints() {
            {
                o(value, NOT_NULL);
                o("test2", NOT_EMPTY);
            }
        });

        then:
        assertNotNull(constraintViolations);
        assertTrue(!constraintViolations.getList().isEmpty());
        assertEquals("null can not be null.", constraintViolations.getList().get(0).getReason().get());
    }

}
