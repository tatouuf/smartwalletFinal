package utils;

import com.stripe.Stripe;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StripeConfig {

    private static final String PROPERTIES_FILE = "/application.properties";
    private static Properties properties = new Properties();
    private static boolean initialized = false;
    private static String publicKey;
    private static String secretKey;

    static {
        loadProperties();
    }

    /**
     * Charge la configuration Stripe
     */
    private static void loadProperties() {
        try (InputStream input = StripeConfig.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
                System.out.println("✅ Fichier de configuration chargé: " + PROPERTIES_FILE);

                // Récupérer les clés
                secretKey = getSecretKey();
                publicKey = getPublicKey();

                System.out.println("   🔑 Mode: " + getMode());
                if (publicKey != null) {
                    System.out.println("   🔑 Clé publique: " + maskKey(publicKey));
                }
                if (secretKey != null) {
                    System.out.println("   🔑 Clé secrète: " + maskKey(secretKey));
                }

            } else {
                System.err.println("⚠️ Fichier de configuration non trouvé: " + PROPERTIES_FILE);
                System.err.println("   Créez le fichier src/main/resources/application.properties");
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur chargement configuration: " + e.getMessage());
        }
    }

    /**
     * Initialise Stripe avec la clé secrète
     */
    public static void initializeStripe() {
        if (initialized) {
            return;
        }

        secretKey = getSecretKey();

        if (secretKey == null || secretKey.isEmpty()) {
            System.err.println("❌ ERREUR: Clé secrète Stripe non configurée!");
            System.err.println("   Ajoutez stripe.secret.key dans application.properties");
            return;
        }

        if (!secretKey.startsWith("sk_test_") && !secretKey.startsWith("sk_live_")) {
            System.err.println("⚠️ ATTENTION: La clé secrète semble invalide!");
            System.err.println("   Elle devrait commencer par sk_test_ ou sk_live_");
        }

        Stripe.apiKey = secretKey;
        initialized = true;

        System.out.println("✅ Stripe initialisé avec succès!");
        System.out.println("   Mode: " + getMode().toUpperCase());
        System.out.println("   Version API: " + Stripe.API_VERSION);
    }

    /**
     * Récupère la clé secrète
     */
    private static String getSecretKey() {
        // Priorité: variable d'environnement > fichier properties
        String envKey = System.getenv("STRIPE_SECRET_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        return properties.getProperty("stripe.secret.key");
    }

    /**
     * Récupère la clé publique
     */
    private static String getPublicKey() {
        String envKey = System.getenv("STRIPE_PUBLIC_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        return properties.getProperty("stripe.public.key");
    }

    /**
     * Récupère la clé publique (méthode publique)
     */
    public static String getPublicKeyString() {
        if (publicKey == null) {
            publicKey = getPublicKey();
        }
        return publicKey;
    }

    /**
     * Masque une clé pour l'affichage
     */
    private static String maskKey(String key) {
        if (key == null || key.length() < 12) return "***";
        return key.substring(0, 9) + "..." + key.substring(key.length() - 4);
    }

    /**
     * Récupère le mode (test/live)
     */
    public static String getMode() {
        return properties.getProperty("stripe.mode", "test");
    }

    /**
     * Récupère la devise par défaut
     */
    public static String getDefaultCurrency() {
        return properties.getProperty("stripe.default.currency", "eur");
    }

    /**
     * Vérifie si Stripe est initialisé
     */
    public static boolean isInitialized() {
        return initialized && Stripe.apiKey != null;
    }

    /**
     * Récupère la description par défaut
     */
    public static String getDefaultDescription() {
        return properties.getProperty("stripe.default.description", "Paiement SmartWallet");
    }

    /**
     * Affiche les cartes de test disponibles
     */
    public static void printTestCards() {
        System.out.println("\n🧪 CARTES DE TEST STRIPE:");
        System.out.println("   ✅ Succès:");
        System.out.println("      • Visa: 4242 4242 4242 4242");
        System.out.println("      • Mastercard: 5555 5555 5555 4444");
        System.out.println("      • Amex: 3782 822463 10005");
        System.out.println("   ❌ Erreurs:");
        System.out.println("      • Refus: 4000 0000 0000 0002");
        System.out.println("      • Fonds insuffisants: 4000 0000 0000 9995");
        System.out.println("      • Carte expirée: 4000 0000 0000 0069");
        System.out.println("   📅 Date d'expiration: 12/34 (ou future)");
        System.out.println("   🔢 CVV: 123 (3 chiffres) ou 1234 (Amex)\n");
    }
}