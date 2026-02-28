package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Transaction;
<<<<<<< HEAD
=======
import esprit.tn.souha_pi.entities.User;
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
<<<<<<< HEAD
import utils.Session;
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

public class TopUpController {

    @FXML private Label cardLabel;
    @FXML private TextField amountField;

    private WalletService walletService = new WalletService();
    private TransactionService transactionService = new TransactionService();

    private static BankCard selectedCard;  // Carte sélectionnée
<<<<<<< HEAD
    entities.User currentUser = Session.getCurrentUser();


    // Dans TopUpController.java - Petite amélioration
    @FXML
    public void initialize() {
        entities.User currentUser = Session.getCurrentUser();  // Déjà fait ligne 18

        if (selectedCard != null) {
            // Protection contre les cartes avec numéro trop court
            String cardNumber = selectedCard.getCardNumber();
            String lastDigits = cardNumber.length() > 4 ?
                    cardNumber.substring(cardNumber.length() - 4) : cardNumber;

            cardLabel.setText("Carte: " + selectedCard.getCardType() + " - **** " + lastDigits);
            System.out.println("✅ Carte sélectionnée: " + selectedCard.getCardType());
        } else {
            cardLabel.setText("Aucune carte sélectionnée");
            System.out.println("⚠️ Aucune carte sélectionnée");
=======
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = WalletLayoutController.instance.getCurrentUser();

        if (selectedCard != null) {
            cardLabel.setText("Carte: " + selectedCard.getCardType() + " - " +
                    selectedCard.getCardNumber().substring(12));
        } else {
            cardLabel.setText("Aucune carte sélectionnée");
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
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