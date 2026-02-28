package controller;

import javafx.fxml.FXML;
import tests.MainFxml;

public class WalletHomeController {

    @FXML
    private void goWallet() {
        // Ouvrir le wallet normalement
        MainFxml.getInstance().openWalletLayoutPopup();
    }

    @FXML
    private void goCards() {
        // Ouvrir directement la section cartes
        MainFxml.getInstance().openWalletLayoutWithSection("CARDS");
    }

    @FXML
    private void goServices() {
        MainFxml.getInstance().openServiceClientPopup();
    }

    @FXML
    private void goReclamations() {
        MainFxml.getInstance().openReclamationUserPopup();
    }

    @FXML
    private void goAssurances() {
        MainFxml.getInstance().openPopup(
                "/assurance/AfficherAssuranceClient.fxml",
                "Assurances",
                1100, 700,
                true
        );
    }

    @FXML
    private void goFriends() {
        MainFxml.getInstance().openFriendsListPopup();
    }

    @FXML
    private void logout() {
        MainFxml.getInstance().logout();
    }
}