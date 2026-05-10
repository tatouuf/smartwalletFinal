package esprit.tn.chayma.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONObject;

public class AIChatService {

    // Remplacez par votre clé API DeepSeek
    private static final String API_KEY = System.getenv("DEEPSEEK_API_KEY");
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    public static String ask(String userMessage, String financialContext) {
        // Fallback : si la clé n'est pas configurée ou si la simulation est activée
        if (API_KEY == null || API_KEY.isBlank()) {
            System.out.println("Aucune clé API DeepSeek trouvée. Utilisation du mode simulation.");
            return simulateSmartAnswer(userMessage, financialContext);
        }

        // Appel réel à DeepSeek
        try {
            return callDeepSeekAPI(userMessage, financialContext);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à l'API DeepSeek: " + e.getMessage());
            // En cas d'erreur, on bascule sur le fallback intelligent
            return simulateSmartAnswer(userMessage, financialContext);
        }
    }

    // Cette méthode appelle l'API DeepSeek
    private static String callDeepSeekAPI(String userMessage, String financialContext) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        // Construction du prompt (enrichi avec le contexte)
        String systemPrompt = "Tu es un conseiller financier expert, spécialisé dans la gestion de budget personnel. "
                + "Tu analyses les données fournies et réponds de manière précise, utile et concise.";

        String fullPrompt = systemPrompt + "\n\n"
                + "Voici la situation financière réelle de l'utilisateur :\n" + financialContext + "\n\n"
                + "Question de l'utilisateur : " + userMessage;

        // Construction du corps de la requête JSON pour DeepSeek
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "Tu es un expert financier."))
                .put(new JSONObject().put("role", "user").put("content", fullPrompt)));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 500);

        // Création et envoi de la requête HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Analyse de la réponse JSON
        JSONObject jsonResponse = new JSONObject(response.body());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } else {
            String errorMsg = jsonResponse.optJSONObject("error") != null
                    ? jsonResponse.getJSONObject("error").optString("message", "Erreur inconnue")
                    : "Code d'erreur HTTP: " + response.statusCode();
            throw new Exception("Erreur API: " + errorMsg);
        }
    }

    // Cette méthode est le cerveau du fallback : elle analyse les données pour générer une réponse
    private static String simulateSmartAnswer(String userMessage, String financialContext) {
        System.out.println("Mode simulation activé. Génération d'une réponse à partir des données.");
        StringBuilder advice = new StringBuilder();
        advice.append("🤖 **Assistant Finance (Mode Hors-Ligne)**\n\n");

        // Extraction des données de Dépenses et Budget
        double totalDepenses = extractNumericValue(financialContext, "Dépenses totales");
        double totalBudget = extractNumericValue(financialContext, "Budget total");

        if (totalDepenses > 0 || totalBudget > 0) {
            // Cas 1: L'utilisateur demande le montant de ses dépenses
            if (userMessage.toLowerCase().contains("dépenses") || userMessage.toLowerCase().contains("combien")) {
                advice.append(String.format("💰 D'après vos données, le total de vos dépenses s'élève actuellement à **%.2f DT**.\n", totalDepenses));
                if (totalBudget > 0) {
                    double pourcentage = (totalDepenses / totalBudget) * 100;
                    advice.append(String.format("Cela représente **%.1f%%** de votre budget total (**%.2f DT**).\n", pourcentage, totalBudget));
                    if (pourcentage > 80) advice.append("⚠️ **Alerte :** Vous avez utilisé plus de 80% de votre budget total. Il est prudent de surveiller les dépenses à venir.\n");
                    else if (pourcentage > 50) advice.append("ℹ️ Vous avez dépassé la moitié de votre budget. Soyez attentif pour le reste du mois.\n");
                    else advice.append("✅ Bonne gestion pour l'instant ! Continuez ainsi.\n");
                }
            }
            // Cas 2: L'utilisateur pose une question générique sur le budget
            else if (userMessage.toLowerCase().contains("budget")){
                advice.append(String.format("📊 Votre budget total est de **%.2f DT**.\n", totalBudget));
                if (totalDepenses > 0) advice.append(String.format("Pour l'instant, vous avez dépensé **%.1f%%** de ce budget.\n", (totalDepenses / totalBudget) * 100));
            }
            // Cas 3: Toute autre question, on fournit une analyse générale
            else {
                advice.append(String.format("📈 **Analyse Rapide :**\n- 💰 Dépenses totales: **%.2f DT**\n- 📊 Budget total: **%.2f DT**\n", totalDepenses, totalBudget));
                if (totalBudget > 0) advice.append(String.format("- 📉 Taux d'utilisation: **%.1f%%**\n", (totalDepenses / totalBudget) * 100));
                String contextualAdvice = getContextualAdvice(financialContext);
                if (!contextualAdvice.isEmpty()) advice.append("\n💡 **Conseil :** ").append(contextualAdvice);
                else advice.append("\n💡 Essayez de poser une question plus précise sur vos dépenses ou votre budget pour une analyse plus fine.");
            }
        } else {
            advice.append("Je suis votre assistant financier intelligent. Pour que je puisse vous aider, veuillez d'abord ajouter des dépenses (section '📝 Dépenses') et définir un budget (section '💰 Budget').");
        }
        return advice.toString();
    }

    // Cette fonction utilitaire extrait des valeurs numériques d'une chaîne de texte
    private static double extractNumericValue(String text, String label) {
        int startIndex = text.indexOf(label);
        if (startIndex != -1) {
            String afterLabel = text.substring(startIndex + label.length()).trim();
            if (afterLabel.startsWith(":")) afterLabel = afterLabel.substring(1).trim();
            String[] parts = afterLabel.split(" ");
            try {
                // On prend le premier token qui ressemble à un nombre
                for (String part : parts) {
                    part = part.replace(",", ".");
                    if (part.matches("\\d+(\\.\\d+)?")) {
                        return Double.parseDouble(part);
                    } else if (part.matches("\\d+")) {
                        return Double.parseDouble(part);
                    }
                }
            } catch (NumberFormatException e) { /* Ignorer */ }
        }
        return 0.0;
    }

    // Cette fonction donne des conseils basés sur les catégories les plus dépensières
    private static String getContextualAdvice(String context) {
        String[] lines = context.split("\n");
        for (String line : lines) {
            if (line.contains("%") && (line.contains("⚠️"))) {
                return "La catégorie " + line.trim() + ". " + "Envisagez de réduire ces dépenses ou d'ajuster le budget correspondant.";
            }
        }
        return "";
    }
}