package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ServicePasswordReset;
import tests.MainFxml;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForgotResetController {

    private static final Logger logger = Logger.getLogger(ForgotResetController.class.getName());

    // Forgot Password page
    @FXML private TextField emailField;
    @FXML private Button btnSendLink;

    // Reset Password page
    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordVisible;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordVisible;
    @FXML private Button toggleNewPasswordBtn;
    @FXML private Button toggleConfirmPasswordBtn;
    @FXML private Button btnReset;

    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private final ServicePasswordReset passwordResetService;
    private static String currentResetToken; // Store token from URL parameter

    public ForgotResetController() {
        passwordResetService = new ServicePasswordReset();
    }

    @FXML
    private void initialize() {
        if (newPasswordField != null && newPasswordVisible != null) {
            newPasswordVisible.textProperty().bindBidirectional(newPasswordField.textProperty());
            newPasswordVisible.setVisible(false);
            newPasswordVisible.setManaged(false);
        }

        if (confirmPasswordField != null && confirmPasswordVisible != null) {
            confirmPasswordVisible.textProperty().bindBidirectional(confirmPasswordField.textProperty());
            confirmPasswordVisible.setVisible(false);
            confirmPasswordVisible.setManaged(false);
        }
    }

    // ========== FORGOT PASSWORD ==========
    @FXML
    private void handleSendResetLink() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter your email address.");
            return;
        }

        // Disable button to prevent double-click
        btnSendLink.setDisable(true);
        btnSendLink.setText("Sending...");

        try {
            boolean success = passwordResetService.sendPasswordResetEmail(email);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Email Sent",
                        "A password reset link has been sent to your email address.\n\n" +
                                "Please check your inbox and click the link to reset your password.\n\n" +
                                "⚠️ The link will expire in 1 hour.");

                // Navigate back to sign in
                MainFxml.getInstance().showSignIn();
            } else {
                showAlert(Alert.AlertType.ERROR, "Email Not Found",
                        "No account found with this email address.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during forgot password", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred. Please try again later.");
        } finally {
            btnSendLink.setDisable(false);
            btnSendLink.setText("Send Reset Link");
        }
    }

    // ========== RESET PASSWORD ==========
    @FXML
    private void handleResetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please fill in both password fields.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Password must be at least 6 characters long.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords Don't Match",
                    "The passwords you entered do not match. Please try again.");
            confirmPasswordField.clear();
            return;
        }

        try {
            // Check if we have a token (from URL or stored)
            if (currentResetToken == null || currentResetToken.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Request",
                        "No valid reset token found. Please request a new password reset.");
                MainFxml.getInstance().showForgotPassword();
                return;
            }

            boolean success = passwordResetService.resetPasswordWithToken(currentResetToken, newPassword);

            if (success) {
                currentResetToken = null; // Clear token
                showAlert(Alert.AlertType.INFORMATION, "Password Reset Successful",
                        "Your password has been reset successfully!\n\nYou can now sign in with your new password.");
                MainFxml.getInstance().showSignIn();
            } else {
                showAlert(Alert.AlertType.ERROR, "Reset Failed",
                        "The reset link is invalid or has expired.\n\nPlease request a new password reset.");
                MainFxml.getInstance().showForgotPassword();
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during reset password", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while resetting your password. Please try again.");
        }
    }

    // Set token from URL parameter (called from MainFxml when opening reset page)
    public static void setResetToken(String token) {
        currentResetToken = token;
    }

    @FXML
    private void handleBackToSignIn() {
        MainFxml.getInstance().showSignIn();
    }

    @FXML
    private void handleToggleNewPassword() {
        isNewPasswordVisible = !isNewPasswordVisible;

        if (isNewPasswordVisible) {
            newPasswordVisible.setVisible(true);
            newPasswordVisible.setManaged(true);
            newPasswordField.setVisible(false);
            newPasswordField.setManaged(false);
            toggleNewPasswordBtn.setText("🙈");
        } else {
            newPasswordField.setVisible(true);
            newPasswordField.setManaged(true);
            newPasswordVisible.setVisible(false);
            newPasswordVisible.setManaged(false);
            toggleNewPasswordBtn.setText("👁");
        }
    }

    @FXML
    private void handleToggleConfirmPassword() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;

        if (isConfirmPasswordVisible) {
            confirmPasswordVisible.setVisible(true);
            confirmPasswordVisible.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            toggleConfirmPasswordBtn.setText("🙈");
        } else {
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmPasswordVisible.setVisible(false);
            confirmPasswordVisible.setManaged(false);
            toggleConfirmPasswordBtn.setText("👁");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}