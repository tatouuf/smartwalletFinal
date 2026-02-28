package utils;

public class AIConfig {

    // OpenAI API Configuration
    public static final String OPENAI_API_KEY = "your-api-key-here"; // Get from https://platform.openai.com/api-keys
    public static final String OPENAI_MODEL = "gpt-3.5-turbo";
    public static final int MAX_TOKENS = 500;
    public static final double TEMPERATURE = 0.7;

    // Sentiment Analysis Thresholds
    public static final double URGENT_THRESHOLD = 0.7;
    public static final double NEGATIVE_THRESHOLD = 0.5;

    // Friend Suggestion Settings
    public static final int MAX_SUGGESTIONS = 5;
    public static final double SIMILARITY_THRESHOLD = 0.3;

    // Fraud Detection Thresholds
    public static final double SUSPICIOUS_AMOUNT = 5000.0; // DT
    public static final int MAX_DAILY_TRANSACTIONS = 10;

    private AIConfig() {} // Prevent instantiation
}