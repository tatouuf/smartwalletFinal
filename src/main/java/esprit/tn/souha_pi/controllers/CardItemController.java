package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.WalletService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CardItemController {

    @FXML private Label numberLabel;
    @FXML private Label holderLabel;
    @FXML private Label expiryLabel;
    @FXML private Label typeLabel;
    @FXML private Label balanceLabel;
    @FXML private VBox rootCard;

    private BankCard card;
    private final BankCardService service = new BankCardService();
    private final WalletService walletService = new WalletService();

    private CardListController parentController;

    // 0 = masked, 1 = show number, 2 = show balance
    private int viewState = 0;

    public void setParentController(CardListController parent){
        this.parentController = parent;
    }

    public void setData(BankCard c){
        card = c;

        numberLabel.setText(format(c.getCardNumber()));
        holderLabel.setText(c.getCardHolder());
        expiryLabel.setText(c.getExpiryDate());
        typeLabel.setText(c.getCardType());

        // withdraw when clicking card
        rootCard.setOnMouseClicked(e -> {
            parentController.withdrawFromCard(card);
        });
    }

    private String format(String num){
        return "**** **** **** " + num.substring(num.length()-4);
    }

    // 👁️ EYE BUTTON LOGIC
    @FXML
    private void toggleCardView(){

        // state 0 -> show full card number
        if(viewState == 0){
            numberLabel.setText(formatFull(card.getCardNumber()));
            balanceLabel.setVisible(false);
            viewState = 1;
            return;
        }

        // state 1 -> show balance
        if(viewState == 1){
            Wallet w = walletService.getWallet();
            balanceLabel.setText("Balance : " + String.format("%.2f TND", w.getBalance()));
            balanceLabel.setVisible(true);
            numberLabel.setText("•••• •••• •••• ••••");
            viewState = 2;
            return;
        }

        // state 2 -> back to masked
        if(viewState == 2){
            numberLabel.setText(format(card.getCardNumber()));
            balanceLabel.setVisible(false);
            viewState = 0;
        }
    }

    private String formatFull(String num){

        if(num == null || num.length() != 16){
            return "Invalid Card";
        }

        return num.substring(0,4)+" "+
                num.substring(4,8)+" "+
                num.substring(8,12)+" "+
                num.substring(12,16);
    }


    @FXML
    private void topUp(){
        TopUpController.selectedCard = card;
        WalletLayoutController.instance.loadPage("wallet/topup.fxml");
    }

    @FXML
    private void delete(){
        service.delete(card.getId());
        WalletLayoutController.instance.loadPage("wallet/cards.fxml");
    }
}
