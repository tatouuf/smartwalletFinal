package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.WalletService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class CardListController {

    @FXML
    private FlowPane cardsContainer;

    private final BankCardService service = new BankCardService();
    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();

    @FXML
    public void initialize(){
        loadCards();
    }

    private void loadCards(){

        cardsContainer.getChildren().clear();
        List<BankCard> list = service.getAllByUser(1);

        for(BankCard c:list){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/wallet/card_item.fxml"));
                Parent cardUI = loader.load();

                CardItemController controller = loader.getController();
                controller.setData(c);
                controller.setParentController(this); // 🔥 LIAISON

                cardsContainer.getChildren().add(cardUI);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void withdrawFromCard(BankCard card){

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdraw Money");
        dialog.setHeaderText("Transfer to card **** " + card.getCardNumber().substring(card.getCardNumber().length()-4));
        dialog.setContentText("Enter amount:");

        dialog.showAndWait().ifPresent(amountStr -> {

            try{
                double amount = Double.parseDouble(amountStr);
                processWithdraw(card, amount);
            }catch(Exception ex){
                showAlert("Invalid amount!");
            }

        });
    }

    private void processWithdraw(BankCard card, double amount){

        Wallet wallet = walletService.getWallet();

        if(amount <= 0){
            showAlert("Amount must be greater than 0");
            return;
        }

        if(wallet.getBalance() < amount){
            showAlert("Insufficient balance!");
            return;
        }

        // 💰 retirer argent
        wallet.setBalance(wallet.getBalance() - amount);
        walletService.updateWallet(wallet);

        // 🧾 enregistrer transaction
        Transaction t = new Transaction(
                wallet.getUserId(),
                "WITHDRAW",
                amount,
                card.getCardNumber()
        );

        transactionService.addTransaction(t);

        showAlert("Transfer Successful!");

        // 🔥 refresh dashboard
        DashboardController.refreshStatic();
    }

    private void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void addCard(){
        WalletLayoutController.instance.loadPage("wallet/card_add.fxml");
    }
}
