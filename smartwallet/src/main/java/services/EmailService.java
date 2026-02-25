package services;

import utils.EmailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    public static boolean sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            // Setup mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
            props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
            props.put("mail.smtp.ssl.trust", EmailConfig.SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            EmailConfig.EMAIL_USERNAME,
                            EmailConfig.EMAIL_PASSWORD
                    );
                }
            });

            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.EMAIL_USERNAME, EmailConfig.FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(EmailConfig.RESET_SUBJECT);

            // Create reset link
            String resetLink = EmailConfig.APP_URL + "/reset?token=" + resetToken;

            // HTML email body
            String htmlContent = createEmailTemplate(resetLink);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Send email
            Transport.send(message);

            logger.info("Password reset email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send email to: " + toEmail, e);
            return false;
        }
    }

    private static String createEmailTemplate(String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { text-align: center; color: #7c3aed; margin-bottom: 30px; }
                        .content { color: #333; line-height: 1.6; }
                        .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold; }
                        .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #999; text-align: center; }
                        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔐 SmartWallet</h1>
                            <h2>Password Reset Request</h2>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>We received a request to reset your SmartWallet account password.</p>
                            <p>Click the button below to reset your password:</p>
                            <center>
                                <a href="%s" class="button">Reset Password</a>
                            </center>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all; color: #7c3aed;"><a href="%s">%s</a></p>
                            <div class="warning">
                                <strong>⚠️ Security Notice:</strong> This link will expire in 1 hour. If you didn't request this reset, please ignore this email.
                            </div>
                        </div>
                        <div class="footer">
                            <p>This email was sent by SmartWallet</p>
                            <p>© 2026 SmartWallet. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetLink, resetLink, resetLink);
    }
}