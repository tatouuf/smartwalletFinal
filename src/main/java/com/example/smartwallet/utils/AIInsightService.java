package com.example.smartwallet.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.CompletableFuture;

public class AIInsightService {

    // Load API key from environment variable or system property
    private static final String API_KEY = System.getenv("HF_API_KEY") != null ? System.getenv("HF_API_KEY") : "";
    private static final String MODEL_URL = "https://router.huggingface.co/v1/chat/completions";

    public static CompletableFuture<String> getRiskAnalysis(double income, double savingsGoal, double recurringTotal, double remaining) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();

                String prompt = String.format(
                    "Context: User monthly income is %.2f TND, savings goal is %.2f TND, " +
                    "total fixed recurring payments are %.2f TND, and remaining budget is %.2f TND. " +
                    "Task: Provide a concise, professional financial risk analysis (under 3 sentences). " +
                    "Focus on potential risks and one specific recommendation. Language: English.",
                    income, savingsGoal, recurringTotal, remaining
                );

                JSONObject payload = new JSONObject();
                // We use meta-llama/Meta-Llama-3-8B-Instruct as it is highly likely to be supported by the router
                payload.put("model", "meta-llama/Meta-Llama-3-8B-Instruct");
                
                JSONArray messages = new JSONArray();
                JSONObject userMessage = new JSONObject();
                userMessage.put("role", "user");
                userMessage.put("content", prompt);
                messages.put(userMessage);
                payload.put("messages", messages);
                
                payload.put("max_tokens", 150);
                payload.put("stream", false);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(MODEL_URL))
                        .header("Authorization", "Bearer " + API_KEY)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JSONObject jsonResp = new JSONObject(response.body());
                    return jsonResp.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
                } else if (response.statusCode() == 503) {
                    return "AI model is warming up... Please refresh in a few seconds. 🔥";
                } else {
                    return "AI Access Error (" + response.statusCode() + "): The new Hugging Face Router might require a different model or permission. Body: " + (response.body().length() > 50 ? response.body().substring(0, 50) : response.body());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error generating AI Insights: " + e.getMessage();
            }
        });
    }
}
