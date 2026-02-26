package controller;

import javafx.fxml.FXML;
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
    private void logout() {
        MainFxml.getInstance().logout();
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
    private void goReclamations() {
        // Popup réclamations
        MainFxml.getInstance().openReclamationUserPopup();
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
}