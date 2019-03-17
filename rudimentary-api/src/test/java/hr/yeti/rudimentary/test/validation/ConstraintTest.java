package hr.yeti.rudimentary.test.validation;

import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.ValidationResult;
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

    when:
    result = Constraint.NOT_NULL.apply(null);

    then:
    assertNotNull(result);
    assertFalse(result.isValid());
  }

  @Test
  @DisplayName("NOT_NULL constraint should report success for not null values")
  public void test_NOT_NULL_constraint_succeed() {
    ValidationResult result;

    when:
    result = Constraint.NOT_NULL.apply(1);

    then:
    assertNotNull(result);
    assertTrue(result.isValid());
  }

  @Test
  @DisplayName("NOT_EMPTY constraint should report failure for empty values")
  public void test_NOT_EMPTY_constraint_fail() {
    ValidationResult result;

    when:
    result = Constraint.NOT_EMPTY.apply("");

    then:
    assertNotNull(result);
    assertFalse(result.isValid());
  }

  @Test
  @DisplayName("NOT_EMPTY constraint should report success for not empty values")
  public void test_NOT_EMPTY_constraint_succeed() {
    ValidationResult result;

    when:
    result = Constraint.NOT_NULL.apply("Rudimentary");

    then:
    assertNotNull(result);
    assertTrue(result.isValid());
  }
}
