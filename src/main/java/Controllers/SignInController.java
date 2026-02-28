package Controllers;

import entities.Role;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ServiceUser;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SignInController {

    private static final Logger logger = Logger.getLogger(SignInController.class.getName());
    private static final Preferences prefs = Preferences.userNodeForPackage(SignInController.class);

    private static final String PREF_EMAIL    = "saved_email";
    private static final String PREF_PASSWORD = "saved_password";
    private static final String PREF_REMEMBER = "remember_me";

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisible;
    @FXML private Button togglePasswordBtn;
    @FXML private CheckBox rememberCheck;

    private boolean isPasswordVisible = false;
    private final ServiceUser userService;

    public SignInController() {
        userService = new ServiceUser();
    }

    @FXML
    private void initialize() {
        // bind visible password to hidden password
        passwordVisible.textProperty().bindBidirectional(passwordField.textProperty());
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        boolean remembered = prefs.getBoolean(PREF_REMEMBER, false);
        if (remembered) {
            emailField.setText(prefs.get(PREF_EMAIL, ""));
            passwordField.setText(prefs.get(PREF_PASSWORD, ""));
            rememberCheck.setSelected(true);
        }

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean wasRemembered = prefs.getBoolean(PREF_REMEMBER, false);
            String savedEmail     = prefs.get(PREF_EMAIL, "");
            String savedPassword  = prefs.get(PREF_PASSWORD, "");

            if (wasRemembered && newVal.trim().equals(savedEmail) && !savedPassword.isEmpty()) {
                passwordField.setText(savedPassword);
            } else if (!newVal.trim().equals(savedEmail)) {
                passwordField.clear();
            }
        });
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
    private void handleLogin() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter both email and password.");
            return;
        }

        try {
            User user = userService.login(email, password);

            if (user == null) {
                passwordField.clear();
                showAlert(Alert.AlertType.ERROR, "Login Failed",
                        "Invalid email or password, or account is not active.");
                return;
            }

            Session.setCurrentUser(user);

            // Redirection selon le rôle
            if (user.isAdmin()) {  // Vérifie si l'utilisateur est ADMIN
                System.out.println("✅ Admin detected, redirecting to Admin Dashboard");
                MainFxml.getInstance().showDashboard();  // Ouvre le dashboard admin
            } else {
                System.out.println("✅ User detected, redirecting to User Wallet");
                MainFxml.getInstance().showWalletHome();  // Ouvre le wallet user
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during login", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while trying to log in.");
        }
    }

    @FXML
    private void handleSignup() {
        MainFxml.getInstance().showSignUp();
    }

    @FXML
    private void handleForgotPassword() {
        MainFxml.getInstance().showForgotPassword();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}