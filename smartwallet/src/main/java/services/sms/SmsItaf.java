package services.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsItaf {

    // Vos identifiants Twilio (vérifiez qu'ils sont corrects)
    private static final String ACCOUNT_SID = "AC6624e663a718048631141a46f8ba914f";
    private static final String AUTH_TOKEN = "f195fd738d519116e5e337321d83d390";
    private static final String FROM_NUMBER = "+17754069238";

    // Flag pour savoir si l'initialisation a réussi
    private static boolean twilioInitialise = false;

    // Initialisation statique (une seule fois)
    static {
        try {
            System.out.println("\n🔧 Initialisation de Twilio...");
            System.out.println("   Account SID: " + masquerSid(ACCOUNT_SID));

            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            // Vérifier que l'authentification fonctionne
            com.twilio.rest.api.v2010.Account account =
                    com.twilio.rest.api.v2010.Account.fetcher(ACCOUNT_SID).fetch();

            System.out.println("   ✅ Twilio initialisé avec succès!");
            System.out.println("   📊 Compte: " + account.getFriendlyName());
            System.out.println("   💰 Statut: " + account.getStatus());

            twilioInitialise = true;

        } catch (Exception e) {
            System.err.println("   ❌ Échec de l'initialisation Twilio: " + e.getMessage());
            System.err.println("   ⚠️ Le système utilisera le mode simulation");
            twilioInitialise = false;
        }
    }

    // Méthode principale d'envoi
    public static boolean envoyer(String to, String message) {
        try {
            // Formater le numéro
            to = formaterNumero(to);

            System.out.println("\n📤 Envoi SMS à " + to + "...");

            // Si Twilio n'est pas initialisé, utiliser la simulation
            if (!twilioInitialise) {
                System.out.println("   ⚠️ Twilio non disponible - Mode simulation");
                return simulerEnvoi(to, message);
            }

            // Envoyer le SMS via Twilio
            Message sms = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(FROM_NUMBER),
                    message
            ).create();

            System.out.println("   ✅ SMS envoyé avec succès!");
            System.out.println("   📎 SID: " + sms.getSid());
            System.out.println("   📊 Statut: " + sms.getStatus());

            return true;

        } catch (com.twilio.exception.ApiException e) {
            System.err.println("   ❌ Erreur API Twilio: " + e.getMessage());
            System.err.println("   Code: " + e.getCode());

            // Gestion des erreurs spécifiques
            switch (e.getCode()) {
                case 20003:
                    System.err.println("   🔑 Erreur d'authentification - Vérifiez vos identifiants");
                    break;
                case 21211:
                    System.err.println("   📞 Numéro de téléphone invalide: " + to);
                    break;
                case 21408:
                    System.err.println("   🌍 Pays non supporté par votre compte Twilio");
                    break;
                case 21610:
                    System.err.println("   🚫 Ce numéro est sur liste noire");
                    break;
            }

            return simulerEnvoi(to, message);

        } catch (Exception e) {
            System.err.println("   ❌ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            return simulerEnvoi(to, message);
        }
    }

    // Formater le numéro de téléphone
    public static String formaterNumero(String telephone) {
        if (telephone == null) return null;

        // Nettoyer le numéro
        telephone = telephone.replaceAll("[^0-9+]", "");

        // Format tunisien
        if (telephone.startsWith("0")) {
            telephone = "+216" + telephone.substring(1);
        }
        if (!telephone.startsWith("+")) {
            telephone = "+216" + telephone;
        }

        return telephone;
    }

    // Simulation d'envoi
    private static boolean simulerEnvoi(String to, String message) {
        System.out.println("\n" + "📱".repeat(20));
        System.out.println("📱 SIMULATION SMS");
        System.out.println("📞 Destinataire: " + to);
        System.out.println("💬 Message: " + message);
        System.out.println("📱".repeat(20) + "\n");
        return true;
    }

    // Masquer une partie du SID pour la sécurité
    private static String masquerSid(String sid) {
        if (sid == null || sid.length() < 10) return sid;
        return sid.substring(0, 6) + "..." + sid.substring(sid.length() - 4);
    }

    // Méthode de test public
    public static boolean testConnexion() {
        return twilioInitialise;
    }
}