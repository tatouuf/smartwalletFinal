package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tests.MainFxml;

public class WalletHomeController {

    // ======================================================
    // LANDING MENU -> ALWAYS OPEN POPUPS (DO NOT REPLACE MAIN SCENE)
    // ======================================================
    @FXML
    private void goWallet() {
        // Popup wallet layout (uses your MainFxml helper)
        MainFxml.getInstance().openWalletLayoutPopup();
    }

    @FXML
    private void goCards() {
        // Popup credit cards
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/dashboard.fxml",
                "Credit Cards",
                1000, 650,
                true
        );
    }

    @FXML
    private void goServices() {
        // Popup services client (you already have a dedicated method)
        MainFxml.getInstance().openServiceClientPopup();

        // or if you prefer directly:
        // MainFxml.getInstance().openPopup("/services/AfficherServiceClient.fxml","Services",1100,700,true);
    }

    @FXML
    private void goAssurances() {
        // Popup assurances
        MainFxml.getInstance().openPopup(
                "/assurance/AfficherAssuranceClient.fxml",
                "Assurances",
                1100, 700,
                true
        );
    }

    @FXML
    private void goFriends() {
        // Popup friends list (dedicated method)
        MainFxml.getInstance().openFriendsListPopup();
    }

    @FXML
    private void goExpenses() {
        // open the new expenses dashboard rather than history
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/wallet/dashboarddepe.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Dépenses");
            Scene scene = new Scene(root, 1000, 650);
            java.net.URL css = getClass().getResource("/css/theme.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setScene(scene);
            stage.initOwner(MainFxml.getInstance().getPrimaryStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
