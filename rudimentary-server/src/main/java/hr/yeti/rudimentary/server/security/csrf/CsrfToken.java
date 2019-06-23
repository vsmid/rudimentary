package hr.yeti.rudimentary.server.security.csrf;

import hr.yeti.rudimentary.server.crypto.Hash;
import java.time.Instant;

public class CsrfToken {

  private String value;
  private Instant created;

  public CsrfToken() {
    this.value = Hash.generateRandomSHA256();
    this.created = Instant.now();
  }

  public String getValue() {
    return value;
  }

  public Instant getCreated() {
    return created;
  }

  @Override
  public String toString() {
    return this.value;
  }

}
