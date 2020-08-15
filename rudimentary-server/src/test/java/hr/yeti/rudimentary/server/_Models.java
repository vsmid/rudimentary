package hr.yeti.rudimentary.server;

import hr.yeti.rudimentary.events.Event;
import hr.yeti.rudimentary.http.content.Pojo;
import hr.yeti.rudimentary.validation.Constraint;
import hr.yeti.rudimentary.validation.Constraints;
import java.util.regex.Pattern;

public class _Models {

    public static class _CarModel extends Pojo {

        private String manufacturer;

        public _CarModel() {
        }

        public _CarModel(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

    }

    public static class _PojoConstrained extends Pojo {

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

    public static class BlogPost extends Pojo implements Event {

        public String text;

        public BlogPost() {
        }
        
        public BlogPost(String text) {
            this.text = text;
        }

    }
}
