package esprit.tn.souha_pi.services;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    // REMPLACEZ PAR VOTRE VRAIE ADRESSE GMAIL
    private static final String EMAIL_EXPEDITEUR = "souha.said@sesame.com.tn";

    // REMPLACEZ PAR LE MOT DE PASSE D'APPLICATION GÉNÉRÉ
    private static final String EMAIL_PASSWORD = "lqfx kgdz kdvb kyah";

    public boolean envoyerEmail(String destinataire, String sujet, String contenuHTML) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.sesame.com.tn"); // À vérifier
        props.put("mail.smtp.port", "587"); // ou 465, 25
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true"); // Pour voir les détails

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_EXPEDITEUR, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_EXPEDITEUR));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setContent(contenuHTML, "text/html; charset=utf-8");

            System.out.println("📤 Envoi d'email réel à " + destinataire + "...");
            Transport.send(message);
            System.out.println("✅ Email RÉEL envoyé avec succès à " + destinataire);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi email réel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}