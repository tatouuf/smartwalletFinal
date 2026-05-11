package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.Session;

public class SendController {

    @FXML private TextField ribField;
    @FXML private TextField amountField;
    @FXML private Label balanceLabel;
    @FXML private Label infoLabel;

    private BankCardService cardService = new BankCardService();
    private TransactionService transactionService = new TransactionService();

    private static BankCard carteSource;
    private entities.User currentUser;

    public static void setCarteSource(BankCard carte) {
        carteSource = carte;
    }

    @FXML
    public void initialize() {
        currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        if (carteSource != null) {
            infoLabel.setText("Envoi depuis: " + carteSource.getCardType() + " - " +
                    "**** **** **** " + carteSource.getCardNumber().substring(Math.max(0, carteSource.getCardNumber().length() - 4)) +
                    "\nRIB: " + carteSource.getRib() +
                    "\nSolde disponible: " + String.format("%.2f TND", carteSource.getBalance()));

            balanceLabel.setText(String.format("Solde carte: %.2f TND", carteSource.getBalance()));
            balanceLabel.setStyle("-fx-text-fill: #4f46e5; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            infoLabel.setText("❌ Aucune carte sélectionnée. Veuillez sélectionner une carte depuis le dashboard.");
            infoLabel.setStyle("-fx-text-fill: #ef4444; -fx-background-color: #fee2e2; -fx-padding: 10; -fx-background-radius: 10;");
            balanceLabel.setText("Solde: 0.00 TND");
        }
    }

    @FXML
    private void send() {
        try {
            // Vérifications de base
            if (currentUser == null) {
                DialogUtil.error("Erreur", "Vous devez être connecté");
                return;
            }

            if (carteSource == null) {
                DialogUtil.error("Erreur", "Aucune carte source sélectionnée");
                return;
            }

            String rib = ribField.getText().trim();
            String amountStr = amountField.getText().trim();

            if (rib.isEmpty()) {
                DialogUtil.error("Erreur", "Veuillez saisir le RIB du destinataire");
                return;
            }

            if (amountStr.isEmpty()) {
                DialogUtil.error("Erreur", "Veuillez saisir un montant");
                return;
            }

            // Validation du montant
            double montant;
            try {
                montant = Double.parseDouble(amountStr);
                if (montant <= 0) {
                    DialogUtil.error("Erreur", "Le montant doit être supérieur à 0");
                    return;
                }
                if (montant > 10000) {
                    DialogUtil.error("Erreur", "Le montant maximum par transaction est de 10 000 TND");
                    return;
                }
            } catch (NumberFormatException e) {
                DialogUtil.error("Erreur", "Montant invalide");
                return;
            }

            // Vérifier le solde de la carte source
            if (carteSource.getBalance() < montant) {
                DialogUtil.error("Erreur",
                        String.format("Solde insuffisant sur la carte. Solde disponible: %.2f TND",
                                carteSource.getBalance()));
                return;
            }

            // Vérifier que le RIB n'est pas celui de la carte source
            if (carteSource.getRib().equals(rib)) {
                DialogUtil.error("Erreur", "Vous ne pouvez pas envoyer de l'argent à votre propre carte");
                return;
            }

            // Rechercher la carte destinataire par RIB
            BankCard carteDestinataire = cardService.getByRib(rib);

            if (carteDestinataire == null) {
                DialogUtil.error("Erreur", "RIB introuvable. Vérifiez le RIB du destinataire.");
                return;
            }

            // Effectuer le transfert
            // 1. Débiter la carte source
            carteSource.setBalance(carteSource.getBalance() - montant);
            cardService.update(carteSource);

            // 2. Créditer la carte destinataire
            carteDestinataire.setBalance(carteDestinataire.getBalance() + montant);
            cardService.update(carteDestinataire);

            // 3. Enregistrer la transaction pour l'expéditeur
            Transaction transactionSource = new Transaction(
                    currentUser.getId(),
                    "DEBIT",
                    -montant,
                    "Transfert vers " + carteDestinataire.getCardHolder() + " (RIB: " + rib + ")"
            );
            transactionService.add(transactionSource);

            // 4. Enregistrer la transaction pour le destinataire
            Transaction transactionDest = new Transaction(
                    carteDestinataire.getUserId(),
                    "CREDIT",
                    montant,
                    "Réception de " + currentUser.getFullname() + " (Carte: " +
                            "****" + carteSource.getCardNumber().substring(Math.max(0, carteSource.getCardNumber().length() - 4)) + ")"
            );
            transactionService.add(transactionDest);

            // Message de succès
            DialogUtil.success("✅ Transfert réussi",
                    String.format("Transfert de %.2f TND effectué vers %s\nRIB: %s",
                            montant, carteDestinataire.getCardHolder(), rib));

            // Rafraîchir le dashboard
            DashboardController.refreshStatic();

            // Fermer la fenêtre
            fermerFenetre();

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Erreur", "Erreur lors du transfert: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) ribField.getScene().getWindow();
        stage.close();
    }
}