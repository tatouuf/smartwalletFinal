package com.example.smartwallet.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HuggingFaceAiService {

    // Choose a routed chat/instruct model
    private static final String MODEL = "mistralai/Mistral-7B-Instruct-v0.3";
    private static final String ENDPOINT = "https://router.huggingface.co/v1/chat/completions";

    private final String token;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    // Default: read from environment/system properties
    public HuggingFaceAiService() {
        this(resolveToken());
    }

    // Allow passing token explicitly (recommended from controller)
    public HuggingFaceAiService(String token) {
        if (token == null || token.isBlank() || token.startsWith("PUT_")) {
            throw new IllegalStateException("Hugging Face token is missing. Set HF_TOKEN (or HUGGINGFACE_API_KEY) or pass it to HuggingFaceAiService(token).");
        }
        this.token = token.trim();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // ✅ This is the method your DashboardController calls
    public String generateFinancialTip(String prompt) throws Exception {
        return chat(prompt);
    }

    // Optional alias (if later you want ai.generateTip())
    public String generateTip(String prompt) throws Exception {
        return chat(prompt);
    }

    private String chat(String userPrompt) throws Exception {
        String body = """
        {
          "model": "%s",
          "messages": [
            {"role": "system", "content": "You are a professional personal finance assistant. Give concise actionable tips."},
            {"role": "user", "content": %s}
          ],
          "temperature": 0.6,
          "max_tokens": 220
        }
        """.formatted(MODEL, mapper.writeValueAsString(userPrompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("HF Router error " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode content = root.path("choices").path(0).path("message").path("content");
        if (!content.isMissingNode()) return content.asText().trim();

        // Fallback
        return response.body();
    }

    private static String resolveToken() {
        // Env candidates
        String[] envCandidates = new String[] {
                System.getenv("HUGGINGFACE_API_KEY"),
                System.getenv("HF_TOKEN"),
                System.getenv("HUGGINGFACEHUB_API_TOKEN"),
                System.getenv("HUGGING_FACE_HUB_TOKEN"),
                System.getenv("HF_API_KEY")
        };
        for (String k : envCandidates) {
            if (k != null && !k.isBlank()) return k.trim();
        }
        // JVM property candidates
        String[] propCandidates = new String[] {
                System.getProperty("huggingface.api.key"),
                System.getProperty("hf.token"),
                System.getProperty("huggingfacehub.api.token"),
                System.getProperty("HF_TOKEN")
        };
        for (String k : propCandidates) {
            if (k != null && !k.isBlank()) return k.trim();
        }
        return null;
    }
}