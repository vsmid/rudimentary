package hr.yeti.rudimentary.validation;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains a list of violated constraints. Violation is represented with {@link ValidationResult}.
 * Constraint violations are returned upon executing
 * {@link Validator#validate(hr.yeti.rudimentary.validation.Constraints...)} method.
 *
 * @author vedransmid@yeti-it.hr
 */
public class ConstraintViolations {

  /**
   * A list of violated constraints.
   */
  private List<ValidationResult> validationResults;

  public ConstraintViolations() {
    this.validationResults = new LinkedList<>();
  }

  /**
   * Adds validation result to the list of violated constraints.
   *
   * @param validationResult {@link ValidationResult}.
   */
  public void add(ValidationResult validationResult) {
    if (!validationResult.isValid()) {
      this.validationResults.add(validationResult);
    }
  }

  /**
   * Gets a list of violated constraints.
   *
   * @return A list of {@link ValidationResult}.
   */
  public List<ValidationResult> getList() {
    return validationResults;
  }

}
