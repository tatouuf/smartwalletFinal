package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hash a password for storage
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Verify that a plain text password matches a stored hash
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            System.err.println("Password or hash is null");
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("BCrypt check error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}