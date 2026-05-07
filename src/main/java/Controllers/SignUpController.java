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

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisible;
    @FXML private Button togglePasswordBtn;

    private boolean isPasswordVisible = false;
    private final ServiceUser userService;

    public SignUpController() {
        userService = new ServiceUser();
    }

    @FXML
    private void initialize() {
        passwordVisible.textProperty().bindBidirectional(passwordField.textProperty());
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);
    }

    @FXML
    private void handleTogglePassword() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordVisible.setVisible(true);
            passwordVisible.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordBtn.setText("🙈");
        } else {
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisible.setVisible(false);
            passwordVisible.setManaged(false);
            togglePasswordBtn.setText("👁");
        }
    }

    @FXML
    private void handleSignUp() {
        String nom       = nomField.getText().trim();
        String prenom    = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String email     = emailField.getText().trim();
        String password  = passwordField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()
                || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please fill in all fields.");
            return;
        }

        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Password must be at least 6 characters long.");
            passwordField.clear();
            return;
        }

        if (!telephone.matches("\\d{8,15}")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter a valid phone number (8-15 digits).");
            return;
        }

        try {
            if (userService.isEmailTaken(email)) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "This email is already registered.");
                emailField.clear();
                return;
            }

            User newUser = new User();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setTelephone(telephone);
            newUser.setEmail(email);
            newUser.setPassword(PasswordUtils.hashPassword(password));
            newUser.setRole(Role.USER);
            newUser.setDate_creation(LocalDateTime.now());
            newUser.setDate_update(LocalDateTime.now());
            newUser.setIs_actif(true);

            userService.ajouter(newUser);

            showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                    "Account created! You can now sign in.");

            MainFxml.getInstance().showSignIn();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during registration", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
        }
    }

    @FXML
    private void handleLogin() {
        MainFxml.getInstance().showSignIn();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}