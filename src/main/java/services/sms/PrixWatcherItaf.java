package services.sms;

import entities.User;
import entities.service.Services;
import services.service.FavoriService;
import java.sql.SQLException;
import java.util.List;

public class PrixWatcherItaf {

    private final FavoriService favoriService = new FavoriService();

    public void notifierChangementService(Services service, float ancienPrix, float nouveauPrix) {
        String message = String.format(
                "🔔 ALERTE PRIX SMARTWALLET 🔔\n" +
                        "Service: %s\n" +
                        "Description: %s\n" +
                        "Ancien prix: %.2f DT\n" +
                        "Nouveau prix: %.2f DT",
                service.getType(),
                service.getDescription(),
                ancienPrix,
                nouveauPrix
        );

        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔔 NOTIFICATION DE CHANGEMENT DE PRIX");
        System.out.println("=".repeat(50));
        System.out.println("Service ID: " + service.getId());
        System.out.println("Type: " + service.getType());
        System.out.println("Description: " + service.getDescription());
        System.out.println("Ancien prix: " + ancienPrix + " DT");
        System.out.println("Nouveau prix: " + nouveauPrix + " DT");

        try {
            // Récupérer les utilisateurs qui ont ce service en favori
            List<User> utilisateurs = favoriService.getUtilisateursParFavori(service.getId());

            if (!utilisateurs.isEmpty()) {
                System.out.println("📱 Envoi SMS à " + utilisateurs.size() + " utilisateur(s)");

                for (User user : utilisateurs) {
                    if (user.getTelephone() != null && !user.getTelephone().isEmpty()) {
                        // Envoyer SMS via votre service Twilio
                        SmsItaf.envoyer(user.getTelephone(), message);
                    }
                }
            } else {
                System.out.println("ℹ️ Aucun utilisateur n'a ce service en favori");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des favoris: " + e.getMessage());
        }

        System.out.println("=".repeat(50));
    }
}