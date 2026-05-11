package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.Session;

public class TopUpController {

    @FXML private Label cardLabel;
    @FXML private TextField amountField;

    private WalletService walletService = new WalletService();
    private TransactionService transactionService = new TransactionService();

    private static BankCard selectedCard;  // Carte sélectionnée
    entities.User currentUser = Session.getCurrentUser();


    @FXML
    public void initialize() {
        entities.User currentUser = Session.getCurrentUser();

        if (selectedCard != null) {
            cardLabel.setText("Carte: " + selectedCard.getCardType() + " - " +
                    selectedCard.getCardNumber().substring(12));
        } else {
            cardLabel.setText("Aucune carte sélectionnée");
        }
    }

    // Méthode statique pour définir la carte sélectionnée
    public static void setSelectedCard(BankCard card) {
        selectedCard = card;
    }

    @FXML
    private void confirm() {
        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        if (selectedCard == null) {
            DialogUtil.error("Erreur", "Aucune carte sélectionnée");
            return;
        }

        String amountStr = amountField.getText().trim();

        if (amountStr.isEmpty()) {
            DialogUtil.error("Erreur", "Veuillez saisir un montant");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                DialogUtil.error("Erreur", "Le montant doit être positif");
                return;
            }

            // Ajouter le montant au wallet
            walletService.addBalance(currentUser.getId(), amount);

            // Enregistrer la transaction
            transactionService.add(new Transaction(
                    currentUser.getId(),
                    "TOP_UP",
                    amount,
                    "Rechargement via carte " + selectedCard.getCardType()
            ));

            DialogUtil.success("Succès", String.format("✅ %.2f TND ajoutés à votre wallet", amount));

            // Retour au dashboard
            cancel();

        } catch (NumberFormatException e) {
            DialogUtil.error("Erreur", "Montant invalide");
        } catch (Exception e) {
            DialogUtil.error("Erreur", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        selectedCard = null;  // Réinitialiser la carte sélectionnée
        WalletLayoutController.instance.goDashboard();
    }
}