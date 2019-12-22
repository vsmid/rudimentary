package hr.yeti.rudimentary.test.validation;

import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.ValidationResult;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConstraintTest {

    @Test
    @DisplayName("NOT_NULL constraint should report failure for null values")
    public void test_NOT_NULL_constraint_fail() {
        ValidationResult result;

        when:   result = Constraint.NOT_NULL.apply(null);

        then:   assertNotNull(result);
        assertFalse(result.isValid());
    }

    @Test
    @DisplayName("NOT_NULL constraint should report success for not null values")
    public void test_NOT_NULL_constraint_succeed() {
        ValidationResult result;

        when:   result = Constraint.NOT_NULL.apply(1);

        then:   assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("NOT_EMPTY constraint should report failure for empty values")
    public void test_NOT_EMPTY_constraint_fail() {
        ValidationResult result;

        when:   result = Constraint.NOT_EMPTY.apply("");

        then:   assertNotNull(result);
        assertFalse(result.isValid());
    }

    @Test
    @DisplayName("NOT_EMPTY constraint should report success for not empty values")
    public void test_NOT_EMPTY_constraint_succeed() {
        ValidationResult result;

        when:   result = Constraint.NOT_NULL.apply("Rudimentary");

        then:   assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    public void test_MIN_constraint() {
        expect: assertFalse(Constraint.MIN(2).apply(1).isValid());
        assertTrue(Constraint.MIN(2).apply(5).isValid());
        assertTrue(Constraint.MIN(2).apply(2).isValid());
    }

    @Test
    public void test_MAX_constraint() {
        expect: assertTrue(Constraint.MAX(2).apply(1).isValid());
        assertFalse(Constraint.MAX(2).apply(5).isValid());
        assertTrue(Constraint.MAX(2).apply(2).isValid());
    }

    @Test
    public void test_PATTERN_constraint() {
        expect: assertTrue(Constraint.REGEX(Pattern.compile("B_\\w+_E")).apply("B_WORD_E").isValid());
        assertFalse(Constraint.REGEX(Pattern.compile("B_\\w+_E")).apply("Test123").isValid());
    }

}
