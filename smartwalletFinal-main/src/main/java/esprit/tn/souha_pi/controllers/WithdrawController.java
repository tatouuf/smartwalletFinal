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

public class WithdrawController {

    @FXML private TextField amountField;
    @FXML private TextField ribField;
    @FXML private Label balanceLabel;

    private BankCardService cardService = new BankCardService();
    private TransactionService transactionService = new TransactionService();
    private entities.User currentUser;
    private BankCard currentCard;

    @FXML
    public void initialize() {
        currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        // Optionnel: Charger la première carte de l'utilisateur par défaut
        try {
            var cartes = cardService.getAllByUser(currentUser.getId());
            if (!cartes.isEmpty()) {
                currentCard = cartes.get(0);
                ribField.setText(currentCard.getRib());
                ribField.setEditable(false); // Rendre le RIB non modifiable
                balanceLabel.setText(String.format("Solde disponible: %.2f TND", currentCard.getBalance()));
            }
        } catch (Exception e) {
            // Ignorer, l'utilisateur devra saisir le RIB manuellement
        }
    }

    @FXML
    private void withdraw() {
        try {
            // Validation des champs
            String amountStr = amountField.getText().trim();
            String rib = ribField.getText().trim();

            if (amountStr.isEmpty()) {
                DialogUtil.error("Erreur", "Veuillez saisir un montant");
                return;
            }

            if (rib.isEmpty()) {
                DialogUtil.error("Erreur", "Veuillez saisir le RIB de votre carte");
                return;
            }

            // Validation du montant
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    DialogUtil.error("Erreur", "Le montant doit être supérieur à 0");
                    return;
                }
                if (amount > 10000) {
                    DialogUtil.error("Erreur", "Le montant maximum par retrait est de 10 000 TND");
                    return;
                }
            } catch (NumberFormatException e) {
                DialogUtil.error("Erreur", "Montant invalide");
                return;
            }

            // Récupérer la carte
            BankCard card;
            if (currentCard != null && currentCard.getRib().equals(rib)) {
                card = currentCard;
            } else {
                card = cardService.getByRib(rib);
            }

            if (card == null) {
                DialogUtil.error("Erreur", "Carte introuvable avec ce RIB");
                return;
            }

            // Vérifier que la carte appartient à l'utilisateur connecté
            if (card.getUserId() != currentUser.getId()) {
                DialogUtil.error("Erreur", "Cette carte ne vous appartient pas");
                return;
            }

            // Vérifier le solde
            if (card.getBalance() < amount) {
                DialogUtil.error("Erreur",
                        String.format("Solde insuffisant. Solde disponible: %.2f TND", card.getBalance()));
                return;
            }

            // Demander confirmation
            boolean confirmed = DialogUtil.confirm(
                    "Confirmation de retrait",
                    String.format("Vous allez retirer %.2f TND de votre carte.\n\n" +
                                    "Carte: %s\n" +
                                    "RIB: %s\n\n" +
                                    "Nouveau solde: %.2f TND\n\n" +
                                    "Confirmez-vous cette opération ?",
                            amount,
                            card.getCardNumber().substring(card.getCardNumber().length() - 4),
                            card.getRib(),
                            card.getBalance() - amount)
            );

            if (!confirmed) {
                return;
            }

            // Effectuer le retrait
            card.setBalance(card.getBalance() - amount);
            cardService.update(card);

            // Enregistrer la transaction
            Transaction transaction = new Transaction(
                    currentUser.getId(),
                    "WITHDRAW",
                    -amount,
                    "Retrait d'argent - Carte: " + card.getCardNumber().substring(card.getCardNumber().length() - 4)
            );
            transactionService.add(transaction);

            // Succès
            DialogUtil.success("Retrait effectué",
                    String.format("✅ Retrait de %.2f TND effectué avec succès", amount));

            // Rafraîchir le dashboard
            DashboardController.refreshStatic();

            // Fermer la fenêtre
            fermerFenetre();

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Erreur", "Erreur lors du retrait: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        try {
            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            // Ignorer
        }
    }
}