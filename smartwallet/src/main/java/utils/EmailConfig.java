package utils;

public class EmailConfig {

    // Gmail SMTP Configuration (you can use other providers too)
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";

    // YOUR EMAIL CREDENTIALS (replace with your actual email)
    public static final String EMAIL_USERNAME = "your-email@gmail.com";
    public static final String EMAIL_PASSWORD = "your-app-password"; // NOT your Gmail password!

    // Application URL
    public static final String APP_URL = "http://localhost:8081";

    // Email templates
    public static final String FROM_NAME = "SmartWallet Support";
    public static final String RESET_SUBJECT = "SmartWallet - Password Reset Request";

    private EmailConfig() {} // Prevent instantiation
}