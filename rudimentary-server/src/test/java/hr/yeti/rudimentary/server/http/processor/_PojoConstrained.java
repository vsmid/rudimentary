package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.content.Model;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import java.util.regex.Pattern;

public class _PojoConstrained extends Model {

    final Pattern onlyLetters = Pattern.compile("[a-zA-Z]+");

    private String name;

    @Override
    public Constraints constraints() {
        return new Constraints() {
            {
                o(name, Constraint.NOT_EMPTY, Constraint.REGEX(onlyLetters));
            }
        };
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
