package services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import utils.EmailConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    private static final Gson gson = new Gson();
    private static final Random random = new Random();

    private static final Map<String, VerificationCode> verificationCodes = new HashMap<>();

    public static String generateVerificationCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public static boolean sendVerificationCode(String toEmail, String userName) {
        try {
            String code = generateVerificationCode();

            long expirationTime = System.currentTimeMillis() +
                    (EmailConfig.CODE_EXPIRATION_MINUTES * 60 * 1000);
            verificationCodes.put(toEmail.toLowerCase(), new VerificationCode(code, expirationTime));

            // Prepare email data with PRIVATE KEY
            JsonObject emailData = new JsonObject();
            emailData.addProperty("service_id", EmailConfig.EMAILJS_SERVICE_ID);
            emailData.addProperty("template_id", EmailConfig.EMAILJS_TEMPLATE_ID);
            emailData.addProperty("user_id", EmailConfig.EMAILJS_PUBLIC_KEY);
            emailData.addProperty("accessToken", EmailConfig.EMAILJS_PRIVATE_KEY); // Private Key for JavaFX

            JsonObject templateParams = new JsonObject();
            templateParams.addProperty("user_name", userName);
            templateParams.addProperty("to_email", toEmail);
            templateParams.addProperty("verification_code", code);
            emailData.add("template_params", templateParams);

            System.out.println("═══════════════════════════════════════");
            System.out.println("📧 SENDING EMAIL WITH PRIVATE KEY");
            System.out.println("═══════════════════════════════════════");
            System.out.println("Service ID: " + EmailConfig.EMAILJS_SERVICE_ID);
            System.out.println("Template ID: " + EmailConfig.EMAILJS_TEMPLATE_ID);
            System.out.println("To Email: " + toEmail);
            System.out.println("Code: " + code);
            System.out.println("═══════════════════════════════════════");

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(EmailConfig.EMAILJS_API_URL);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(gson.toJson(emailData)));

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    int statusCode = response.getCode();

                    // Read response body - Simple way without EntityUtils
                    String responseBody = "";
                    if (response.getEntity() != null) {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(response.getEntity().getContent()))) {
                            responseBody = reader.lines().collect(Collectors.joining("\n"));
                        } catch (Exception e) {
                            responseBody = "Unable to read response";
                        }
                    }

                    System.out.println("Response Status: " + statusCode);
                    System.out.println("Response Body: " + responseBody);
                    System.out.println("═══════════════════════════════════════");

                    if (statusCode == 200) {
                        logger.info("✅ Verification code sent successfully to: " + toEmail);
                        System.out.println("✅ Email sent! Code: " + code + " (DEV MODE)");
                        return true;
                    } else {
                        logger.warning("❌ Failed to send email. Status: " + statusCode);
                        logger.warning("Response: " + responseBody);
                        return false;
                    }
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "❌ Error sending verification email", e);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean verifyCode(String email, String enteredCode) {
        String emailKey = email.toLowerCase();

        if (!verificationCodes.containsKey(emailKey)) {
            logger.warning("❌ No verification code found for: " + email);
            return false;
        }

        VerificationCode storedCode = verificationCodes.get(emailKey);

        if (System.currentTimeMillis() > storedCode.expirationTime) {
            logger.warning("❌ Verification code expired for: " + email);
            verificationCodes.remove(emailKey);
            return false;
        }

        if (storedCode.code.equals(enteredCode)) {
            logger.info("✅ Verification code matched for: " + email);
            return true;
        } else {
            logger.warning("❌ Verification code mismatch for: " + email);
            return false;
        }
    }

    public static void clearVerificationCode(String email) {
        verificationCodes.remove(email.toLowerCase());
        logger.info("🗑️ Verification code cleared for: " + email);
    }

    public static boolean hasValidCode(String email) {
        String emailKey = email.toLowerCase();
        if (!verificationCodes.containsKey(emailKey)) {
            return false;
        }

        VerificationCode storedCode = verificationCodes.get(emailKey);
        boolean isValid = System.currentTimeMillis() <= storedCode.expirationTime;

        if (!isValid) {
            logger.info("⏰ Code expired for: " + email);
            verificationCodes.remove(emailKey);
        }

        return isValid;
    }

    // Inner class to store verification code with expiration
    private static class VerificationCode {
        String code;
        long expirationTime;

        VerificationCode(String code, long expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }
}