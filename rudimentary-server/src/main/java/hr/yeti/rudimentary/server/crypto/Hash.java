package hr.yeti.rudimentary.server.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hash {

    private static final System.Logger LOGGER = System.getLogger(Hash.class.getName());
        
    private static SecureRandom random = new SecureRandom();

    public static String generateRandomSHA256() {
        try {
            String val = Integer.toString(random.nextInt());
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            byte[] digest = instance.digest(val.getBytes());
            return hexToString(digest);
        } catch (NoSuchAlgorithmException ex) {
            // Should not happen.
            LOGGER.log(System.Logger.Level.ERROR, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    public static String generateSHA256(String data) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            byte[] digest = instance.digest(data.getBytes());
            return hexToString(digest);
        } catch (NoSuchAlgorithmException ex) {
            // Should not happen.
            LOGGER.log(System.Logger.Level.ERROR, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    public static String hexToString(byte[] input) {
        StringBuilder result = new StringBuilder();
        char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (int idx = 0; idx < input.length; ++idx) {
            byte b = input[idx];
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
        }
        return result.toString();
    }
}
