package hr.yeti.rudimentary.demo.endpoint;

import hr.yeti.rudimentary.http.content.Model;
import static hr.yeti.rudimentary.validation.Constraint.NOT_EMPTY;
import static hr.yeti.rudimentary.validation.Constraint.NOT_NULL;
import hr.yeti.rudimentary.validation.Constraints;

public class OkModel extends Model {

  private String name;
  private String description;

  @Override
  public Constraints constraints() {
    return new Constraints() {
      {
        o(name, NOT_NULL);
        o(description, NOT_NULL, NOT_EMPTY);
      }
    };
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
