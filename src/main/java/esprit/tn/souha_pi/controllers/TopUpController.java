package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.WalletService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TopUpController {

    public static BankCard selectedCard;

    @FXML private Label cardLabel;
    @FXML private TextField amountField;

    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();

    @FXML
    public void initialize(){
        cardLabel.setText("Card : **** **** **** " +
                selectedCard.getCardNumber().substring(12));
    }

    @FXML
    private void confirm(){

        double amount = Double.parseDouble(amountField.getText());

        // 1️⃣ update wallet balance
        walletService.addBalance(1, amount);

        // 2️⃣ save transaction history
        transactionService.add(
                new Transaction(
                        1,
                        "DEPOSIT",
                        amount,
                        "CARD "+selectedCard.getCardNumber().substring(12)
                )
        );

        WalletLayoutController.instance.loadPage("wallet/dashboard.fxml");
    }

    @FXML
    private void cancel(){
        WalletLayoutController.instance.loadPage("wallet/cards.fxml");
    }
}
