package esprit.tn.souha_pi.services;

import entities.User;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SMSService {

    private static final String API_URL = "https://openbank.stb.com.tn/api/students/subscription/sendsms";
    private static final String API_KEY = "55c87b41825244d7b0299f66e3bda7f6";

    public boolean envoyerSMS(String telephone, String message) {
        HttpURLConnection connection = null;
        try {
            String mobile = telephone.replaceAll("[^0-9]", "");

            String jsonInput = String.format(
                    "{\"message\":\"%s\",\"mobile\":\"%s\"}",
                    message, mobile
            );

            System.out.println("📤 Envoi SMS à " + mobile + "...");
            System.out.println("📦 JSON: " + jsonInput);

            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", API_KEY);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("📨 Code réponse: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_CREATED) {

                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(connection.getInputStream()))) {
                    String response = reader.lines().reduce("", (acc, line) -> acc + line);
                    System.out.println("✅ Réponse API: " + response);
                }

                System.out.println("✅ SMS envoyé avec succès à " + telephone);
                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println("❌ Exception SMS: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public void envoyerSMSApprobation(User user) {
        String message = String.format(
                "✅ %s %s, votre compte Souha Wallet a été approuvé ! Connectez-vous.",
                user.getPrenom(), user.getNom()
        );
        envoyerSMS(user.getTelephone(), message);
    }

    public void envoyerSMSRejet(User user) {
        String message = String.format(
                "❌ %s %s, votre demande a été refusée. Contactez le support.",
                user.getPrenom(), user.getNom()
        );
        envoyerSMS(user.getTelephone(), message);
    }
}