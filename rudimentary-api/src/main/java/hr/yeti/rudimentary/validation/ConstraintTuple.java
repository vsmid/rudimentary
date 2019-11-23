package hr.yeti.rudimentary.validation;

/**
 * Class used to define constraint pair consisting of a value and an array of constraints to be applied to the value.
 *
 * @see Constraints
 *
 * @author vedransmid@yeti-it.hr
 */
public class ConstraintTuple {

    private Object value;
    private Constraint[] constraints;

    /**
     * Constructs constraint tuple.
     *
     * @param value A value to which constraints will be applied to.
     * @param constraints Constraints to be applied to the value.
     */
    public ConstraintTuple(Object value, Constraint[] constraints) {
        this.value = value;
        this.constraints = constraints;
    }

    public Object getValue() {
        return value;
    }

    public Constraint[] getConstraints() {
        return constraints;
    }

}
