package Controllers;

import entities.Role;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ServiceUser;
import tests.MainFxml;
import utils.PasswordUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUpController {

    private static final Logger logger = Logger.getLogger(SignUpController.class.getName());

    // ── FXML fields ──────────────────────────────────────────────────────────
    @FXML private TextField     nomField;
    @FXML private TextField     prenomField;
    @FXML private TextField     telephoneField;
    @FXML private TextField     emailField;

    @FXML private ComboBox<String> nationaliteCombo;
    @FXML private ComboBox<String> paysResidenceCombo;

    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisible;
    @FXML private Button        togglePasswordBtn;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField     confirmPasswordVisible;
    @FXML private Button        toggleConfirmBtn;

    @FXML private Label         messageLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private boolean isPasswordVisible        = false;
    private boolean isConfirmPasswordVisible = false;

    private final ServiceUser userService;

    public SignUpController() {
        userService = new ServiceUser();
    }

    // ── INIT ─────────────────────────────────────────────────────────────────

    @FXML
    private void initialize() {
        // Sync the plain-text mirror fields with the password fields
        passwordVisible.textProperty().bindBidirectional(passwordField.textProperty());
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        confirmPasswordVisible.textProperty().bindBidirectional(confirmPasswordField.textProperty());
        confirmPasswordVisible.setVisible(false);
        confirmPasswordVisible.setManaged(false);

        populateCountryLists();
    }

    // ── COUNTRY LISTS ─────────────────────────────────────────────────────────

    private void populateCountryLists() {
        String[] countries = {
                "", "Afghanistan","Afrique du Sud","Albanie","Algérie","Allemagne","Angola",
                "Arabie Saoudite","Argentine","Australie","Autriche","Azerbaïdjan","Bahreïn",
                "Bangladesh","Belgique","Bénin","Bolivie","Brésil","Bulgarie","Burkina Faso",
                "Cambodge","Cameroun","Canada","Chili","Chine","Colombie","Congo","Corée du Sud",
                "Côte d'Ivoire","Croatie","Cuba","Danemark","Djibouti","Égypte",
                "Émirats arabes unis","Espagne","Éthiopie","États-Unis","Finlande","France",
                "Gabon","Ghana","Grèce","Guatemala","Guinée","Hongrie","Inde","Indonésie",
                "Irak","Iran","Irlande","Islande","Israël","Italie","Jamaïque","Japon",
                "Jordanie","Kazakhstan","Kenya","Koweït","Liban","Libye","Luxembourg",
                "Madagascar","Malaisie","Mali","Maroc","Mauritanie","Mexique","Mongolie",
                "Mozambique","Myanmar","Niger","Nigéria","Norvège","Nouvelle-Zélande","Oman",
                "Ouganda","Pakistan","Palestine","Panama","Pays-Bas","Pérou","Philippines",
                "Pologne","Portugal","Qatar","République tchèque","Roumanie","Russie","Rwanda",
                "Sénégal","Serbie","Singapour","Slovaquie","Somalie","Soudan","Sri Lanka",
                "Suède","Suisse","Syrie","Taïwan","Tanzanie","Tchad","Thaïlande","Togo",
                "Tunisie","Turquie","Ukraine","Uruguay","Venezuela","Vietnam","Yémen",
                "Zambie","Zimbabwe"
        };
        nationaliteCombo.getItems().addAll(countries);
        paysResidenceCombo.getItems().addAll(countries);
    }

    // ── TOGGLE PASSWORD VISIBILITY ────────────────────────────────────────────

    @FXML
    private void handleTogglePassword() {
        isPasswordVisible = !isPasswordVisible;
        passwordVisible.setVisible(isPasswordVisible);
        passwordVisible.setManaged(isPasswordVisible);
        passwordField.setVisible(!isPasswordVisible);
        passwordField.setManaged(!isPasswordVisible);
        togglePasswordBtn.setText(isPasswordVisible ? "🙈" : "👁");
    }

    @FXML
    private void handleToggleConfirm() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        confirmPasswordVisible.setVisible(isConfirmPasswordVisible);
        confirmPasswordVisible.setManaged(isConfirmPasswordVisible);
        confirmPasswordField.setVisible(!isConfirmPasswordVisible);
        confirmPasswordField.setManaged(!isConfirmPasswordVisible);
        toggleConfirmBtn.setText(isConfirmPasswordVisible ? "🙈" : "👁");
    }

    // ── SIGN UP ───────────────────────────────────────────────────────────────

    @FXML
    private void handleSignUp() {
        clearMessage();

        String nom            = nomField.getText().trim();
        String prenom         = prenomField.getText().trim();
        String telephone      = telephoneField.getText().trim();
        String email          = emailField.getText().trim();
        String password       = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String nationalite    = nationaliteCombo.getValue();
        String paysResidence  = paysResidenceCombo.getValue();

        // ── Mandatory field validation ────────────────────────────────────────
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()
                || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Veuillez remplir tous les champs obligatoires (*).", true);
            return;
        }

        if (nom.length() < 2 || prenom.length() < 2) {
            showMessage("Le nom et le prénom doivent avoir au moins 2 caractères.", true);
            return;
        }

        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showMessage("Adresse email invalide.", true);
            return;
        }

        if (!telephone.matches("\\d{8,15}")) {
            showMessage("Numéro de téléphone invalide (8 à 15 chiffres).", true);
            return;
        }

        if (password.length() < 6) {
            showMessage("Le mot de passe doit contenir au moins 6 caractères.", true);
            passwordField.clear();
            confirmPasswordField.clear();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Les mots de passe ne correspondent pas.", true);
            confirmPasswordField.clear();
            return;
        }

        // ── DB checks & save ─────────────────────────────────────────────────
        try {
            if (userService.isEmailTaken(email)) {
                showMessage("Cette adresse email est déjà utilisée.", true);
                emailField.clear();
                return;
            }

            if (userService.telephoneExiste(telephone)) {
                showMessage("Ce numéro de téléphone est déjà utilisé.", true);
                telephoneField.clear();
                return;
            }

            User newUser = new User();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setTelephone(telephone);
            newUser.setEmail(email);
            newUser.setPassword(PasswordUtils.hashPassword(password));
            newUser.setRole(Role.USER);
            newUser.setStatus("PENDING");
            newUser.setIs_actif(false);
            newUser.setDate_creation(LocalDateTime.now());
            newUser.setDate_update(LocalDateTime.now());

            // Optional fields
            if (nationalite   != null && !nationalite.isEmpty())   newUser.setNationalite(nationalite);
            if (paysResidence != null && !paysResidence.isEmpty()) newUser.setPaysResidence(paysResidence);

            userService.ajouter(newUser);

            showSuccess();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during registration", e);
            showMessage("Une erreur est survenue. Veuillez réessayer.", true);
        }
    }

    @FXML
    private void handleLogin() {
        MainFxml.getInstance().showSignIn();
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inscription réussie");
        alert.setHeaderText(null);
        alert.setContentText(
                "✅ Votre compte a été créé avec succès !\n\n" +
                        "Il est en attente de validation par un administrateur.\n" +
                        "Vous recevrez un accès une fois approuvé."
        );
        alert.showAndWait();
        MainFxml.getInstance().showSignIn();
    }

    private void showMessage(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setStyle(isError
                ? "-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: 600;"
                : "-fx-text-fill: #16a34a; -fx-font-size: 12px; -fx-font-weight: 600;");
        messageLabel.setVisible(true);
    }

    private void clearMessage() {
        messageLabel.setVisible(false);
        messageLabel.setText("");
    }
}