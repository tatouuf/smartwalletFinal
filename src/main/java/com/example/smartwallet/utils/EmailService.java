package com.example.smartwallet.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    // Gmail Config - Placeholder
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    // IMPORTANT: LOAD FROM ENVIRONMENT VARIABLES
    private static final String USERNAME = System.getenv("EMAIL_USERNAME") != null ? System.getenv("EMAIL_USERNAME") : "";
    private static final String APP_PASSWORD = System.getenv("EMAIL_APP_PASSWORD") != null ? System.getenv("EMAIL_APP_PASSWORD") : "";

    public static void sendPaymentSuccessEmail(String toEmail, String transactionId, double amount, String currency) {
        // Run in background thread to avoid blocking UI
        new Thread(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, APP_PASSWORD);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(USERNAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Payment Successful - SmartWallet");

                String content = "<h1>Payment Confirmation</h1>" +
                        "<p>Hello,</p>" +
                        "<p>Your payment with Stripe was successful.</p>" +
                        "<ul>" +
                        "<li><b>Transaction ID:</b> " + transactionId + "</li>" +
                        "<li><b>Amount:</b> " + amount + " " + currency.toUpperCase() + "</li>" +
                        "</ul>" +
                        "<p>Thank you for using SmartWallet!</p>";

                message.setContent(content, "text/html; charset=utf-8");

                Transport.send(message);
                System.out.println("Email sent successfully to " + toEmail);

            } catch (MessagingException e) {
                e.printStackTrace();
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }).start();
    }
}
