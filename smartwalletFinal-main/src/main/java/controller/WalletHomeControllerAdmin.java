package controller;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tests.MainFxml;
import utils.Session;

public class WalletHomeControllerAdmin {

    @FXML private Button adminNameBtn;

    @FXML
    public void initialize() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            adminNameBtn.setText("👤 " + currentUser.getPrenom() + " " + currentUser.getNom());
        }
    }

    @FXML
    private void openProfile() {
        Stage profileStage = MainFxml.getInstance().openPopup(
                "/Profile.fxml", "Mon Profil", 520, 640, true
        );
        if (profileStage != null) {
            profileStage.setOnHidden(e -> {
                User updated = Session.getCurrentUser();
                if (updated != null) {
                    adminNameBtn.setText("👤 " + updated.getPrenom() + " " + updated.getNom());
                }
            });
        }
    }

    // ======================================================
    // LANDING MENU -> ALWAYS OPEN POPUPS (DO NOT REPLACE MAIN SCENE)
    // ======================================================

    @FXML
    private void goWallet() {
        MainFxml.getInstance().openWalletLayoutPopup();
    }

    @FXML
    private void logout() {
        MainFxml.getInstance().logout();
    }

    @FXML
    private void goCards() {
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/dashboard.fxml",
                "Credit Cards",
                1000, 650,
                true
        );
    }

    @FXML
    private void goServices() {
        MainFxml.getInstance().openServiceClientPopup();
        MainFxml.getInstance().openPopup("/services/AfficherService.fxml", "Services", 1100, 700, true);
    }

    @FXML
    private void goAssurances() {
        MainFxml.getInstance().openPopup(
                "/assurance/AfficherAssurance.fxml",
                "Assurances",
                1100, 700,
                true
        );
    }

    @FXML
    private void goFriends() {
        MainFxml.getInstance().openFriendsListPopup();
    }
}