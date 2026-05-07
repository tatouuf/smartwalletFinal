package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.BankCardService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.Session;
import esprit.tn.souha_pi.utils.DialogUtil;

public class CardAddController {

    @FXML private TextField holderField;
    @FXML private TextField numberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private Label cardCountLabel;

    private BankCardService cardService = new BankCardService();
    private static final int MAX_CARDS = 5;

    @FXML
    public void initialize() {
        // Remplir le ChoiceBox avec les types de cartes
        typeChoice.getItems().addAll("Visa", "MasterCard", "American Express", "Visa Electron", "Maestro");
        typeChoice.setValue("Visa"); // Valeur par défaut

        // Afficher le nombre de cartes actuelles
        afficherCompteurCartes();
    }

    private void afficherCompteurCartes() {
        try {
            int userId = Session.getCurrentUser().getId();
            int nombreCartes = cardService.getAllByUser(userId).size();
            cardCountLabel.setText("Cartes actuelles : " + nombreCartes + "/" + MAX_CARDS);
        } catch (Exception e) {
            cardCountLabel.setText("Cartes actuelles : 0/" + MAX_CARDS);
        }
    }

    @FXML
    private void save() {
        try {
            // Vérifier d'abord la limite
            int userId = Session.getCurrentUser().getId();
            int nombreCartes = cardService.getAllByUser(userId).size();

            if (nombreCartes >= MAX_CARDS) {
                DialogUtil.error("Limite atteinte",
                        "Vous avez déjà atteint la limite maximale de " + MAX_CARDS + " cartes.\n" +
                                "Supprimez une carte existante avant d'en ajouter une nouvelle.");
                fermerFenetre();
                return;
            }

            // Valider les champs
            if (!validerChamps()) {
                return;
            }

            // Créer la nouvelle carte
            BankCard nouvelleCarte = new BankCard();
            nouvelleCarte.setCardHolder(holderField.getText().trim());
            nouvelleCarte.setCardNumber(numberField.getText().trim().replace(" ", ""));
            nouvelleCarte.setExpiryDate(expiryField.getText().trim());
            nouvelleCarte.setCvv(cvvField.getText().trim());
            nouvelleCarte.setCardType(typeChoice.getValue());
            nouvelleCarte.setUserId(userId);

            // Générer un RIB (exemple simple)
            String rib = "TN59" + System.currentTimeMillis() % 1000000;
            nouvelleCarte.setRib(rib);

            // Sauvegarder
            cardService.add(nouvelleCarte);

            DialogUtil.success("Succès", "Carte ajoutée avec succès !");

            // Rafraîchir le dashboard
            DashboardController.refreshStatic();

            // Fermer la fenêtre
            fermerFenetre();

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Erreur", "Erreur lors de l'ajout de la carte: " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        // Vérifier que les champs ne sont pas vides
        if (holderField.getText().trim().isEmpty()) {
            DialogUtil.error("Erreur de validation", "Le nom du titulaire est requis");
            return false;
        }

        if (numberField.getText().trim().isEmpty()) {
            DialogUtil.error("Erreur de validation", "Le numéro de carte est requis");
            return false;
        }

        // Valider le format du numéro de carte (16 chiffres)
        String numero = numberField.getText().trim().replace(" ", "");
        if (!numero.matches("\\d{16}")) {
            DialogUtil.error("Erreur de validation", "Le numéro de carte doit contenir 16 chiffres");
            return false;
        }

        if (expiryField.getText().trim().isEmpty()) {
            DialogUtil.error("Erreur de validation", "La date d'expiration est requise");
            return false;
        }

        // Valider le format de la date (MM/YY)
        String expiry = expiryField.getText().trim();
        if (!expiry.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            DialogUtil.error("Erreur de validation", "Le format de la date doit être MM/YY (ex: 12/25)");
            return false;
        }

        if (cvvField.getText().trim().isEmpty()) {
            DialogUtil.error("Erreur de validation", "Le CVV est requis");
            return false;
        }

        // Valider le CVV (3 ou 4 chiffres)
        String cvv = cvvField.getText().trim();
        if (!cvv.matches("\\d{3,4}")) {
            DialogUtil.error("Erreur de validation", "Le CVV doit contenir 3 ou 4 chiffres");
            return false;
        }

        return true;
    }

    private void fermerFenetre() {
        Stage stage = (Stage) holderField.getScene().getWindow();
        stage.close();
    }
}