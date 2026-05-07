// utils/PasswordUtils.java
package utils;

import org.mindrot.jbcrypt.BCrypt;
import java.security.MessageDigest;

public class PasswordUtils {

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            System.err.println("❌ Password or hash is null");
            return false;
        }

        try {
            if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2y$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$")) {
                // 🔧 CORRECTION IMPORTANTE : Convertir $2y$ en $2a$
                String bcryptHash = hashedPassword;
                if (hashedPassword.startsWith("$2y$")) {
                    bcryptHash = "$2a$" + hashedPassword.substring(4);
                    System.out.println("🔄 Converted hash from $2y$ to $2a$");
                }

                System.out.println("Original hash: " + hashedPassword);
                System.out.println("Using hash: " + bcryptHash);

                return BCrypt.checkpw(plainPassword, bcryptHash);
            } else {
                // Fallback for plain hashes (e.g., SHA-256 used in Symfony)
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedHash = digest.digest(plainPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : encodedHash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                String generatedHash = hexString.toString();
                System.out.println("Using SHA-256 fallback");
                return generatedHash.equalsIgnoreCase(hashedPassword);
            }

        } catch (Exception e) {
            System.err.println("❌ Password check error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
}