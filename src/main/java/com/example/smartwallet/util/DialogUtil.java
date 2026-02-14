package com.example.smartwallet.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DialogUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Affiche une alerte d'information
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'erreur
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'avertissement
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de confirmation
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Affiche une boîte de dialogue d'entrée de texte
     */
    public static Optional<String> showTextInput(String title, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }

    /**
     * Formate un montant en monnaie
     */
    public static String formatMontant(double montant) {
        return String.format("%.2f DT", montant);
    }

    /**
     * Formate une date
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }

    /**
     * Parse une date en texte
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Valide un montant
     */
    public static boolean isValidMontant(String montantStr) {
        try {
            double montant = Double.parseDouble(montantStr);
            return montant > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valide un pourcentage
     */
    public static boolean isValidPercentage(String percentageStr) {
        try {
            int percentage = Integer.parseInt(percentageStr);
            return percentage >= 0 && percentage <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Retourne le nom du mois
     */
    public static String getMonthName(int mois) {
        String[] months = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };
        return mois >= 1 && mois <= 12 ? months[mois - 1] : "";
    }

    /**
     * Retourne le nom court du mois
     */
    public static String getMonthShortName(int mois) {
        String[] months = {
            "Jan", "Fév", "Mar", "Avr", "Mai", "Jun",
            "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"
        };
        return mois >= 1 && mois <= 12 ? months[mois - 1] : "";
    }

    /**
     * Calcule le pourcentage
     */
    public static double calculatePercentage(double valeur, double total) {
        if (total == 0) return 0;
        return (valeur / total) * 100;
    }

    /**
     * Arrondit un nombre à 2 décimales
     */
    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Retourne la saison en fonction du mois
     */
    public static String getSeason(int mois) {
        if (mois >= 3 && mois <= 5) return "Printemps";
        if (mois >= 6 && mois <= 8) return "Été";
        if (mois >= 9 && mois <= 11) return "Automne";
        return "Hiver";
    }

    /**
     * Calcule le nombre de jours entre deux dates
     */
    public static long daysBetween(LocalDate date1, LocalDate date2) {
        return java.time.temporal.ChronoUnit.DAYS.between(date1, date2);
    }

    /**
     * Retourne la date au format lisible
     */
    public static String getFormattedDateWithDay(LocalDate date) {
        if (date == null) return "";
        String[] dayNames = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        int dayOfWeek = date.getDayOfWeek().getValue() - 1;
        return dayNames[dayOfWeek % 7] + " " + formatDate(date);
    }

    /**
     * Valide une chaîne de texte (non vide)
     */
    public static boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Valide une adresse email
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Valide un numéro de téléphone
     */
    public static boolean isValidPhone(String phone) {
        String phoneRegex = "^[+]?[0-9]{10,}$";
        return phone != null && phone.matches(phoneRegex);
    }

    /**
     * Obtient la couleur hexadécimale correspondant à une catégorie
     */
    public static String getCategoryColor(String categorie) {
        switch (categorie) {
            case "Alimentation": return "#FF6B6B";
            case "Transport": return "#4ECDC4";
            case "Logement": return "#45B7D1";
            case "Santé": return "#96CEB4";
            case "Loisirs": return "#FFEAA7";
            case "Éducation": return "#DDA15E";
            case "Autre": return "#BC6C25";
            default: return "#212121";
        }
    }

    /**
     * Retourne une description basée sur le statut
     */
    public static String getStatusDescription(String statut) {
        switch (statut) {
            case "En cours": return "🔄 En cours";
            case "Terminé": return "✓ Terminé";
            case "Suspendu": return "⏸ Suspendu";
            case "Annulé": return "✗ Annulé";
            default: return statut;
        }
    }
}

