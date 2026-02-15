package esprit.tn.souha_pi.controllers;


import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.TransactionService;
import esprit.tn.souha_pi.services.WalletService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class SendController {

    @FXML
    private TextField targetField;

    @FXML
    private TextField amountField;

    WalletService walletService=new WalletService();
    TransactionService transactionService=new TransactionService();

    @FXML
    public void send(){

        try{
            String target=targetField.getText();
            double amount=Double.parseDouble(amountField.getText());

            boolean ok=walletService.send(amount);

            if(!ok){
                alert("Insufficient balance");
                return;
            }

            Wallet w=walletService.getWallet();

            transactionService.add(
                    new Transaction(0,w.getId(),"SEND",amount,target,null)
            );

            alert("Money sent successfully!");
            WalletLayoutController.instance.goDashboard();

        }catch(Exception e){
            alert("Invalid input");
        }
    }

    private void alert(String msg){
        Alert a=new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
}
