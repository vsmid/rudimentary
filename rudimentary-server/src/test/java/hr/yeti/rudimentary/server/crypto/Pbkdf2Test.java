package hr.yeti.rudimentary.server.crypto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pbkdf2Test {

  @Test
  public void test_password_generation() {
    // setup:
    Pbkdf2 pbkdf2 = new Pbkdf2();
    String hashedPassword;

    when:
    hashedPassword = pbkdf2.generate("my_password".toCharArray());

    then:
    assertTrue(pbkdf2.verify("my_password".toCharArray(), hashedPassword));
  }

}
