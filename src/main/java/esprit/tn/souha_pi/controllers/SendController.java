package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SendController {

    @FXML private TextField ribField;
    @FXML private TextField amountField;
    @FXML private Label balanceLabel;
    @FXML private Label infoLabel;

    private WalletService walletService = new WalletService();
    private BankCardService cardService = new BankCardService();
    private TransactionService transactionService = new TransactionService();

    private static BankCard carteSource;
    private int currentUserId;
    private User currentUser;

    public static void setCarteSource(BankCard carte) {
        carteSource = carte;
    }

    @FXML
    public void initialize() {
        currentUser = WalletLayoutController.instance.getCurrentUser();

        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        currentUserId = currentUser.getId();

        try {
            double balance = walletService.getByUserId(currentUserId).getBalance();
            balanceLabel.setText(String.format("Solde: %.2f TND", balance));

            if (carteSource != null) {
                infoLabel.setText("Envoi depuis: " + carteSource.getCardType() + " - " +
                        carteSource.getCardNumber().substring(12) +
                        "\nRIB: " + carteSource.getRib());
            } else {
                infoLabel.setText("Sélectionnez une carte depuis le dashboard pour envoyer de l'argent");
            }
        } catch (Exception e) {
            balanceLabel.setText("Solde: 0.00 TND");
        }
    }

    @FXML
    private void send() {
        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        if (carteSource == null) {
            DialogUtil.error("Erreur", "Veuillez sélectionner une carte source depuis le dashboard");
            return;
        }

        String rib = ribField.getText().trim();
        String amountStr = amountField.getText().trim();

        if (rib.isEmpty() || amountStr.isEmpty()) {
            DialogUtil.error("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                DialogUtil.error("Erreur", "Le montant doit être positif");
                return;
            }

            // Vérifier le solde
            double balance = walletService.getByUserId(currentUserId).getBalance();
            if (balance < amount) {
                DialogUtil.error("Erreur", "Solde insuffisant");
                return;
            }

            // Rechercher la carte destinataire par RIB
            BankCard destinataire = cardService.getByRib(rib);

            if (destinataire == null) {
                DialogUtil.error("Erreur", "RIB invalide ou inexistant");
                return;
            }

            if (destinataire.getUserId() == currentUserId) {
                DialogUtil.error("Erreur", "Vous ne pouvez pas vous envoyer de l'argent à vous-même");
                return;
            }

            // Effectuer le transfert
            walletService.transfer(currentUserId, destinataire.getUserId(), amount);

            // Enregistrer la transaction pour l'expéditeur
            transactionService.add(new Transaction(
                    currentUserId,
                    "SEND",
                    -amount,
                    "Transfert vers " + destinataire.getCardHolder() + " (RIB: " + rib + ")"
            ));

            // Enregistrer la transaction pour le destinataire
            transactionService.add(new Transaction(
                    destinataire.getUserId(),
                    "RECEIVE",
                    amount,
                    "Réception de " + currentUser.getFullname()
            ));

            DialogUtil.success("Succès",
                    String.format("✅ Transfert de %.2f TND effectué vers %s",
                            amount, destinataire.getCardHolder()));

            // Mettre à jour l'affichage
            initialize();
            ribField.clear();
            amountField.clear();
            carteSource = null;

        } catch (NumberFormatException e) {
            DialogUtil.error("Erreur", "Montant invalide");
        } catch (Exception e) {
            DialogUtil.error("Erreur", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        carteSource = null;
        WalletLayoutController.instance.goDashboard();
    }
}