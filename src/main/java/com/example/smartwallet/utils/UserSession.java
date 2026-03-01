package com.example.smartwallet.utils;

/**
 * Singleton to manage the current user session.
 * This class will be integrated with the User module later.
 */
public class UserSession {
    private static UserSession instance;
    private String userEmail;
    private String userName;

    private UserSession() {
        // Load from environment variables or use default
        this.userEmail = System.getenv("DEFAULT_USER_EMAIL") != null ? System.getenv("DEFAULT_USER_EMAIL") : "user@example.com";
        this.userName = System.getenv("DEFAULT_USER_NAME") != null ? System.getenv("DEFAULT_USER_NAME") : "User";
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
