package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import tests.MainFxml;  // AJOUT IMPORTANT
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import org.json.JSONObject;
public class CardItemController {

    @FXML private VBox rootCard;
    @FXML private Label typeLabel;
    @FXML private Label numberLabel;
    @FXML private Label ribLabel;
    @FXML private Label balanceLabel;
    @FXML private Label holderLabel;
    @FXML private Label expiryLabel;
    @FXML private Button eyeBtn;
    @FXML private ImageView qrImage;
    private BankCard card;
    private boolean isNumberVisible = true;
    private BankCardService cardService = new BankCardService();
    private WalletService walletService = new WalletService();

    public void setCard(BankCard card) {
        this.card = card;
        typeLabel.setText(card.getCardType());
        holderLabel.setText(card.getCardHolder());
        expiryLabel.setText(card.getExpiryDate());

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

        generateQR();
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
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/topup.fxml",
                "Recharger la carte",
                400, 400,
                true
        );
    }

    @FXML
    private void delete() {
        if (DialogUtil.confirm("Confirmation", "Voulez-vous vraiment supprimer cette carte ?")) {
            cardService.delete(card.getId());
            DialogUtil.success("Succès", "Carte supprimée");
            // Rafraîchir la liste
            DashboardController.refreshStatic();
        }
    }

    // NOUVEAU: Méthode pour envoyer depuis cette carte
    @FXML
    private void sendMoney() {
        // Passer la carte sélectionnée au SendController
        SendController.setCarteSource(card);

        // Ouvrir la popup d'envoi
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/send.fxml",
                "Envoyer de l'argent",
                500, 600,
                true
        );
    }
    private void generateQR() {
        try {

            double balance = walletService.getByUserId(card.getUserId()).getBalance();

            // Création JSON sécurisé
            JSONObject data = new JSONObject();
            data.put("rib", card.getRib());
            data.put("balance", balance);
            data.put("holder", card.getCardHolder());
            data.put("cardId", card.getId());

            String qrText = data.toString();

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(qrText, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "PNG", os);

            Image fxImage = new Image(new ByteArrayInputStream(os.toByteArray()));
            qrImage.setImage(fxImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}