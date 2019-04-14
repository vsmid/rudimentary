package hr.yeti.rudimentary.server.crypto;

import hr.yeti.rudimentary.config.spi.Config;
import hr.yeti.rudimentary.server.ContextMock;
import hr.yeti.rudimentary.server.security.crypto.Pbkdf2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Pbkdf2Test {

  @Test
  public void test_password_generation() {
    // setup:
    ContextMock contextMock = new ContextMock(new CustomConfigProvider());

    Pbkdf2 pbkdf2 = new Pbkdf2();
    String hashedPassword;

    when:
    hashedPassword = pbkdf2.generate("my_password".toCharArray());

    then:
    assertTrue(pbkdf2.verify("my_password".toCharArray(), hashedPassword));
  }

  public class CustomConfigProvider extends Config {

  }

}
