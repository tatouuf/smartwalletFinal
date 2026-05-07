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
        // Hardcoded for now as requested
        this.userEmail = "mohamed@esprit.tn";
        this.userName = "Mohamed";
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
