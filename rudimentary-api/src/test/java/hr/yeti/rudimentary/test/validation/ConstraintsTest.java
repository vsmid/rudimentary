package hr.yeti.rudimentary.test.validation;

import static hr.yeti.rudimentary.validation.Constraint.NOT_EMPTY;
import static hr.yeti.rudimentary.validation.Constraint.NOT_NULL;
import hr.yeti.rudimentary.validation.Constraints;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConstraintsTest {

    @Test
    @DisplayName("Should add constraint to Constraints object")
    public void test_add_constraint() {
        Constraints constraints;

        final String field1 = "test1";
        final String field2 = "test2";

        when:   constraints = new Constraints() {
                    {
                        o(field1, NOT_NULL);
                        o(() -> field2, NOT_EMPTY);
                    }
                };

        then:   assertNotNull(constraints);
        assertTrue(!constraints.getConstraints().isEmpty());
        assertTrue(constraints.getConstraints().size() == 2);
        assertEquals("test1", constraints.getConstraints().get(0).getValue().toString());
        assertEquals("test2", constraints.getConstraints().get(1).getValue().toString());
    }

}
