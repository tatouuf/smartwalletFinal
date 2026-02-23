package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.UserService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserService userService = new UserService();
    private WalletService walletService = new WalletService();

    @FXML
    private void login() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        User user = userService.getByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            errorLabel.setText("Email ou mot de passe incorrect");
            return;
        }

        // Vérifier le statut
        if ("EN_ATTENTE".equals(user.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Compte en attente");
            alert.setHeaderText("⏳ Validation en cours");

            TextArea content = new TextArea(
                    "Cher(e) " + user.getFullname() + ",\n\n" +
                            "Votre compte est en cours de vérification.\n" +
                            "Un administrateur va valider votre inscription sous 24h.\n\n" +
                            "Vous recevrez un email de confirmation dès activation."
            );
            content.setWrapText(true);
            content.setEditable(false);
            content.setStyle("-fx-font-size: 14px;");

            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setMinHeight(200);
            alert.showAndWait();
            return;
        }

        if ("REJETE".equals(user.getStatus())) {
            DialogUtil.error("Compte rejeté",
                    "Votre demande a été rejetée. Contactez l'administrateur.");
            return;
        }

        // Connexion réussie
        errorLabel.setText("");
        WalletLayoutController.instance.setCurrentUser(user);

        // Vérifier si l'utilisateur a un wallet
        try {
            Wallet wallet = walletService.getByUserId(user.getId());
            // A un wallet → Dashboard normal
            if ("ADMIN".equals(user.getRole())) {
                WalletLayoutController.instance.goAdminDashboard();
                DialogUtil.success("Bienvenue Admin", "Connexion réussie");
            } else {
                WalletLayoutController.instance.goDashboard();
                DialogUtil.success("Bienvenue " + user.getFullname(), "Connexion réussie");
            }
        } catch (Exception e) {
            // Pas de wallet → Rediriger vers création de wallet
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Création de wallet");
            alert.setHeaderText("💰 Bienvenue " + user.getFullname());
            alert.setContentText(
                    "Votre compte a été validé !\n\n" +
                            "Pour commencer, vous devez créer votre wallet."
            );
            alert.showAndWait();

            WalletLayoutController.instance.openInscription();
        }
    }

    @FXML
    private void goToSignup() {  // ← CORRIGÉ: s'appelle goToSignup, pas goToInscription
        WalletLayoutController.instance.loadPage("signup.fxml");
    }
}