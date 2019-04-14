package hr.yeti.rudimentary.server.security.crypto;

import hr.yeti.rudimentary.config.ConfigProperty;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Pbkdf2 {

  private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

  private final int ALGORITHM_TOKEN = 0;
  private final int ITERATION_COUNT_TOKEN = 1;
  private final int SALT_TOKEN = 2;
  private final int HASH_TOKEN = 3;

  private final SecureRandom random = new SecureRandom();

  private ConfigProperty saltLength = new ConfigProperty("saltSize", "32");
  private ConfigProperty keyLength = new ConfigProperty("keySize", "32");
  private ConfigProperty iterationCount = new ConfigProperty("iterations", "2048");

  public String generate(char[] password) {
    byte[] salt = new byte[saltLength.asInt()];
    random.nextBytes(salt);

    byte[] hash = pbkdf2(password, salt, keyLength.asInt());
    return encode(hash, salt);
  }

  public boolean verify(char[] password, String passwordHash) {
    Object[] decodedPasswordHash = decode(passwordHash);
    byte[] hashToVerify = pbkdf2(password, (byte[]) decodedPasswordHash[SALT_TOKEN], ((byte[]) decodedPasswordHash[HASH_TOKEN]).length);
    return compare(hashToVerify, (byte[]) decodedPasswordHash[HASH_TOKEN]);
  }

  private byte[] pbkdf2(char[] password, byte[] salt, int keyLength) {
    try {
      return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(new PBEKeySpec(password, salt, iterationCount.asInt(), keyLength * 8)).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException(e);
    }
  }

  private boolean compare(byte[] array1, byte[] array2) {
    int diff = array1.length ^ array2.length;
    for (int i = 0; i < array1.length; i++) {
      diff |= array1[i] ^ array2[i % array2.length];
    }
    return diff == 0;
  }

  private Object[] decode(String passwordHash) {
    String[] tokens = passwordHash.split(":");

    assert tokens.length == 4;
    assert ALGORITHM.equals(tokens[ALGORITHM_TOKEN]);

    try {

      return new Object[]{
        tokens[ALGORITHM_TOKEN],
        Integer.parseInt(tokens[ITERATION_COUNT_TOKEN]),
        Base64.getDecoder().decode(tokens[SALT_TOKEN]),
        Base64.getDecoder().decode(tokens[HASH_TOKEN])
      };
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Bad hash", e);
    }
  }

  private String encode(byte[] hash, byte[] salt) {
    return String.format("%s:%d:%s:%s",
        ALGORITHM,
        iterationCount.asInt(),
        Base64.getEncoder().encodeToString(salt),
        Base64.getEncoder().encodeToString(hash)
    );
  }

}
