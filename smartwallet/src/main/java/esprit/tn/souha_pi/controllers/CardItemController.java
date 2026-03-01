package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class CardItemController {

    @FXML private VBox rootCard;
    @FXML private Label typeLabel;
    @FXML private Label numberLabel;
    @FXML private Label ribLabel;  // NOUVEAU
    @FXML private Label balanceLabel;
    @FXML private Label holderLabel;
    @FXML private Label expiryLabel;
    @FXML private Button eyeBtn;

    private BankCard card;
    private boolean isNumberVisible = true;
    private BankCardService cardService = new BankCardService();
    private WalletService walletService = new WalletService();

    public void setCard(BankCard card) {
        this.card = card;
        typeLabel.setText(card.getCardType());
        holderLabel.setText(card.getCardHolder());
        expiryLabel.setText(card.getExpiryDate());

        // Afficher le RIB
        if (ribLabel != null) {
            ribLabel.setText("RIB: " + card.getRib());
        }

        updateNumberVisibility();

        try {
            double balance = walletService.getByUserId(card.getUserId()).getBalance();
            balanceLabel.setText(String.format("%.2f TND", balance));
        } catch (Exception e) {
            balanceLabel.setText("0.00 TND");
        }
    }

    @FXML
    private void toggleCardView() {
        isNumberVisible = !isNumberVisible;
        updateNumberVisibility();
    }

    private void updateNumberVisibility() {
        if (isNumberVisible) {
            String numero = card.getCardNumber();
            String numeroMasque = "**** **** **** " + numero.substring(numero.length() - 4);
            numberLabel.setText(numeroMasque);
            eyeBtn.setText("👁");
        } else {
            numberLabel.setText(card.getCardNumber());
            eyeBtn.setText("🔒");
        }
    }

    @FXML
    private void topUp() {
        // Ouvrir la page de rechargement
        TopUpController.setSelectedCard(card);
        WalletLayoutController.instance.loadPage("wallet/topup.fxml");
    }

    @FXML
    private void delete() {
        if (DialogUtil.confirm("Confirmation", "Voulez-vous vraiment supprimer cette carte ?")) {
            cardService.delete(card.getId());
            DialogUtil.success("Succès", "Carte supprimée");
            // Retourner à la liste des cartes
            WalletLayoutController.instance.goCards();
        }
    }
}