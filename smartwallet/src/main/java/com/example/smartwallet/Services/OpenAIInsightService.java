package com.example.smartwallet.Services;

import com.example.smartwallet.entities.DashboardResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAIInsightService {

    private final String apiKey;
    private final String model;
    private final HttpClient http;
    private final Gson gson;

    public OpenAIInsightService(String apiKey) {
        this(apiKey, "gpt-4o-mini");
    }

    public OpenAIInsightService(String apiKey, String model) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("OpenAI API key is missing. Provide a non-empty key.");
        }
        this.apiKey = apiKey.trim();
        this.model = (model == null || model.isBlank()) ? "gpt-4o-mini" : model.trim();

        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.gson = new Gson();
    }

    // =========================
    // 1) API CALL: prompt -> text
    // =========================
    public String generateInsights(String prompt) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", model);

            JsonArray messages = new JsonArray();

            JsonObject sys = new JsonObject();
            sys.addProperty("role", "system");
            sys.addProperty("content",
                    "You are a personal finance assistant. " +
                            "Answer with short, actionable bullet points. " +
                            "No long paragraphs. Max 6 bullets.");
            messages.add(sys);

            JsonObject user = new JsonObject();
            user.addProperty("role", "user");
            user.addProperty("content", prompt);
            messages.add(user);

            body.add("messages", messages);
            body.addProperty("temperature", 0.4);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() >= 400) {
                return "AI Error (" + res.statusCode() + "): " + safeShort(res.body(), 300);
            }

            JsonObject json = gson.fromJson(res.body(), JsonObject.class);

            // choices[0].message.content
            JsonArray choices = json.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) {
                return "AI Error: empty response.";
            }

            JsonObject first = choices.get(0).getAsJsonObject();
            JsonObject message = first.getAsJsonObject("message");
            if (message == null) return "AI Error: invalid response format.";

            String content = message.get("content").getAsString();
            return (content == null || content.isBlank()) ? "AI: no content." : content.trim();

        } catch (IOException | InterruptedException e) {
            return "AI Error: " + e.getMessage();
        } catch (Exception e) {
            return "AI Error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }

    // =========================
    // 2) FIX: DashboardResult + lang -> prompt -> API
    // =========================
    public String generateInsights(DashboardResult r, String lang) {
        if (r == null) return "AI: dashboard data is missing.";

        String language = (lang == null || lang.isBlank()) ? "French" : lang.trim();

        String prompt =
                "Language: " + language + "\n" +
                        "Give 5 actionable tips based on the dashboard below. " +
                        "Use bullet points only. Mention numbers.\n\n" +
                        "DASHBOARD:\n" +
                        "- monthlyBudget: " + r.getMonthlyBudget() + " TND\n" +
                        "- savingsGoal: " + r.getSavingsGoal() + " TND\n" +
                        "- score: " + r.getScore() + "/100\n" +
                        "- riskLevel: " + r.getRiskLevel() + "\n" +
                        "- recurringMonthly: " + r.getRecurringMonthly() + " TND\n" +
                        "- remainingThisMonth: " + r.getRemainingThisMonth() + " TND\n";

        return generateInsights(prompt);
    }

    private String safeShort(String s, int max) {
        if (s == null) return "";
        s = s.replaceAll("\\s+", " ").trim();
        return (s.length() <= max) ? s : s.substring(0, max) + "...";
    }
}