package esprit.tn.souha_pi.services;

import entities.User;
import esprit.tn.souha_pi.entities.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private EmailService emailService = new EmailService();
    private SMSService smsService = new SMSService();

    // Simulons une liste de notifications en mémoire
    private List<Notification> notificationsSimulees = new ArrayList<>();

    public void notifierApprobation(User user) {
        // SMS réel
        smsService.envoyerSMSApprobation(user);

        // Email simulé
        String contenuEmail = String.format(
                "<h2 style='color: #22c55e;'>✅ Compte approuvé !</h2>" +
                        "<p>Bonjour <strong>%s %s</strong>,</p>" +
                        "<p>Votre compte Souha Wallet a été approuvé avec succès.</p>",
                user.getPrenom(), user.getNom()
        );
        emailService.envoyerEmail(user.getEmail(), "✅ Compte approuvé", contenuEmail);

        // Ajouter aux notifications
        Notification notif = new Notification();
        notif.setId(notificationsSimulees.size() + 1);
        notif.setUserId(user.getId());
        notif.setTitle("✅ Compte approuvé");
        notif.setMessage("Votre compte a été approuvé avec succès");
        notif.setType("SUCCES");
        notif.setStatus("envoyé");
        notif.setCreatedAt(java.time.LocalDateTime.now());
        notificationsSimulees.add(notif);
    }

    public void notifierRejet(User user) {
        // SMS réel
        smsService.envoyerSMSRejet(user);

        // Email simulé
        String contenuEmail = String.format(
                "<h2 style='color: #ef4444;'>❌ Demande refusée</h2>" +
                        "<p>Bonjour <strong>%s %s</strong>,</p>" +
                        "<p>Votre demande d'inscription a été refusée.</p>",
                user.getPrenom(), user.getNom()
        );
        emailService.envoyerEmail(user.getEmail(), "❌ Demande refusée", contenuEmail);

        // Ajouter aux notifications
        Notification notif = new Notification();
        notif.setId(notificationsSimulees.size() + 1);
        notif.setUserId(user.getId());
        notif.setTitle("❌ Demande refusée");
        notif.setMessage("Votre demande d'inscription a été refusée");
        notif.setType("ERREUR");
        notif.setStatus("envoyé");
        notif.setCreatedAt(java.time.LocalDateTime.now());
        notificationsSimulees.add(notif);
    }

    // ======================= MÉTHODE MANQUANTE =======================

    public List<Notification> getNotificationsUtilisateur(int userId) {
        List<Notification> userNotifications = new ArrayList<>();
        for (Notification n : notificationsSimulees) {
            if (n.getUserId() == userId) {
                userNotifications.add(n);
            }
        }
        return userNotifications;
    }

    // ======================= POUR LE BADGE =======================

    public int getNombreNotificationsNonLues(int userId) {
        int count = 0;
        for (Notification n : notificationsSimulees) {
            if (n.getUserId() == userId && "en_attente".equals(n.getStatus())) {
                count++;
            }
        }
        return count;
    }
}