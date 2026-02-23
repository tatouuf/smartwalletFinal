package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SignupController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private UserService userService = new UserService();

    @FXML
    private void signup() {
        // Récupérer les valeurs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validations
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() ||
                telephone.isEmpty() || adresse.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Tous les champs sont obligatoires");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            errorLabel.setText("Email invalide");
            return;
        }

        // Validation du téléphone (8 chiffres pour la Tunisie)
        if (!telephone.matches("\\d{8}")) {
            errorLabel.setText("Le téléphone doit contenir 8 chiffres");
            return;
        }

        try {
            // Vérifier si l'email existe déjà
            User existingUser = userService.getByEmail(email);
            if (existingUser != null) {
                errorLabel.setText("Cet email est déjà utilisé");
                return;
            }

            // Vérifier si le téléphone existe déjà
            // Vous devez ajouter cette méthode dans UserService
            if (userService.telephoneExiste(telephone)) {
                errorLabel.setText("Ce numéro de téléphone est déjà utilisé");
                return;
            }

            // Créer le nouvel utilisateur
            User newUser = new User();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setFullname(nom + " " + prenom);
            newUser.setEmail(email);
            newUser.setTelephone(telephone);
            // newUser.setAdresse(adresse); // À ajouter dans User.java si nécessaire
            newUser.setPassword(password);
            newUser.setRole("UTILISATEUR");

            User createdUser = userService.inscrire(newUser, 0.0);

            // Message de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inscription réussie");
            alert.setHeaderText("✅ Compte créé avec succès !");

            TextArea content = new TextArea(
                    "Cher(e) " + nom + " " + prenom + ",\n\n" +
                            "Votre compte a été créé avec succès.\n\n" +
                            "⏳ Il est actuellement en attente de validation par un administrateur.\n" +
                            "👑 Vous recevrez un email de confirmation dès que votre compte sera activé.\n\n" +
                            "Merci de votre patience !"
            );
            content.setWrapText(true);
            content.setEditable(false);
            content.setStyle("-fx-font-size: 14px;");

            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setMinHeight(250);
            alert.getDialogPane().setMinWidth(400);
            alert.showAndWait();

            // Rediriger vers la page de connexion
            goToLogin();

        } catch (Exception e) {
            errorLabel.setText("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() {
        WalletLayoutController.instance.loadPage("login.fxml");
    }
}