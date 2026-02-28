package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.EmailService;
import services.ServiceUser;
import tests.MainFxml;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForgotResetController {

    private static final Logger logger = Logger.getLogger(ForgotResetController.class.getName());

    // ==================== FORGOT PASSWORD PAGE ====================
    @FXML private TextField txtEmailForgot;
    @FXML private Button btnSendCode;
    @FXML private Button btnBackToLogin;
    @FXML private Label lblStatus;

    // ==================== VERIFY CODE PAGE (6 FIELDS) ====================
    @FXML private TextField codeField1;
    @FXML private TextField codeField2;
    @FXML private TextField codeField3;
    @FXML private TextField codeField4;
    @FXML private TextField codeField5;
    @FXML private TextField codeField6;
    @FXML private Button btnVerifyCode;
    @FXML private Hyperlink btnResendCode;
    @FXML private Label lblCodeStatus;
    @FXML private Label lblEmail;

    // ==================== RESET PASSWORD PAGE ====================
    @FXML private TextField txtNewPassword;
    @FXML private TextField txtConfirmPassword;
    @FXML private Button btnResetPassword;

    private final ServiceUser userService;
    private static String verifiedEmail = null;
    private static String pendingEmail = null;

    public ForgotResetController() {
        userService = new ServiceUser();
    }

    @FXML
    private void initialize() {
        // Setup code field auto-focus and auto-advance
        if (codeField1 != null) {
            setupCodeFields();
        }

        // Set email label on verify code page
        if (lblEmail != null && pendingEmail != null) {
            lblEmail.setText(pendingEmail);
        }
    }

    // ==================== SETUP CODE FIELDS AUTO-ADVANCE ====================
    private void setupCodeFields() {
        TextField[] fields = {codeField1, codeField2, codeField3, codeField4, codeField5, codeField6};

        for (int i = 0; i < fields.length; i++) {
            final int index = i;
            TextField currentField = fields[i];

            // Limit to 1 digit per field
            currentField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1) {
                    currentField.setText(newVal.substring(0, 1));
                }
                // Only allow digits
                if (!newVal.matches("\\d*")) {
                    currentField.setText(oldVal);
                }
                // Auto-advance to next field
                if (newVal.length() == 1 && index < fields.length - 1) {
                    fields[index + 1].requestFocus();
                }
            });

            // Handle backspace to go to previous field
            currentField.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("BACK_SPACE") &&
                        currentField.getText().isEmpty() && index > 0) {
                    fields[index - 1].requestFocus();
                }
            });
        }

        // Auto-focus first field
        codeField1.requestFocus();
    }

    // ==================== FORGOT PASSWORD - SEND CODE ====================
    @FXML
    private void handleSendCode() {
        String email = txtEmailForgot.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter your email address.");
            return;
        }

        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please enter a valid email address.");
            return;
        }

        try {
            if (!userService.isEmailTaken(email)) {
                showAlert(Alert.AlertType.ERROR, "Email Not Found",
                        "No account found with this email address.");
                return;
            }

            entities.User user = userService.recuperer().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Unable to retrieve user information.");
                return;
            }

            btnSendCode.setDisable(true);
            if (lblStatus != null) {
                lblStatus.setText("Sending verification code...");
            }

            boolean sent = EmailService.sendVerificationCode(email, user.getPrenom() + " " + user.getNom());

            if (sent) {
                pendingEmail = email.toLowerCase();
                showAlert(Alert.AlertType.INFORMATION, "Code Sent",
                        "A 6-digit verification code has been sent to " + email + "\n\n" +
                                "Please check your email and enter the code.\n" +
                                "The code will expire in 10 minutes.");

                MainFxml.getInstance().showVerifyCode();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to send verification code. Please check your internet connection.");
                btnSendCode.setDisable(false);
                if (lblStatus != null) {
                    lblStatus.setText("");
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
            btnSendCode.setDisable(false);
            if (lblStatus != null) {
                lblStatus.setText("");
            }
        }
    }

    @FXML
    private void handleBackToLogin() {
        MainFxml.getInstance().showSignIn();
    }

    // ==================== VERIFY CODE (FROM 6 FIELDS) ====================
    @FXML
    private void handleVerifyCode() {
        // Concatenate all 6 fields
        String code = codeField1.getText() + codeField2.getText() +
                codeField3.getText() + codeField4.getText() +
                codeField5.getText() + codeField6.getText();

        if (code.length() != 6) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Code",
                    "Please enter all 6 digits of the verification code.");
            codeField1.requestFocus();
            return;
        }

        if (!code.matches("\\d{6}")) {
            showAlert(Alert.AlertType.WARNING, "Invalid Code",
                    "Verification code must contain only digits.");
            clearCodeFields();
            return;
        }

        if (pendingEmail == null) {
            showAlert(Alert.AlertType.ERROR, "Session Expired",
                    "Your session has expired. Please start over.");
            MainFxml.getInstance().showForgotPassword();
            return;
        }

        btnVerifyCode.setDisable(true);
        lblCodeStatus.setText("Verifying...");

        boolean isValid = EmailService.verifyCode(pendingEmail, code);

        if (isValid) {
            verifiedEmail = pendingEmail;
            pendingEmail = null;
            showAlert(Alert.AlertType.INFORMATION, "✅ Success",
                    "Code verified successfully!\n\nYou can now reset your password.");
            MainFxml.getInstance().showResetPassword();
        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Invalid Code",
                    "The verification code is incorrect or has expired.\n\n" +
                            "Please try again or request a new code.");
            clearCodeFields();
            btnVerifyCode.setDisable(false);
            lblCodeStatus.setText("");
        }
    }

    @FXML
    private void handleResendCode() {
        if (pendingEmail == null) {
            showAlert(Alert.AlertType.ERROR, "Session Expired",
                    "Your session has expired. Please start over.");
            MainFxml.getInstance().showForgotPassword();
            return;
        }

        try {
            entities.User user = userService.recuperer().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(pendingEmail))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Unable to retrieve user information.");
                return;
            }

            btnResendCode.setDisable(true);
            lblCodeStatus.setText("Resending code...");

            boolean sent = EmailService.sendVerificationCode(pendingEmail,
                    user.getPrenom() + " " + user.getNom());

            if (sent) {
                showAlert(Alert.AlertType.INFORMATION, "📧 Code Resent",
                        "A new verification code has been sent to:\n" + pendingEmail);
                lblCodeStatus.setText("");
                clearCodeFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to resend code. Please check your internet connection.");
                lblCodeStatus.setText("");
            }

            btnResendCode.setDisable(false);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred. Please try again.");
            btnResendCode.setDisable(false);
            lblCodeStatus.setText("");
        }
    }

    @FXML
    private void handleBackToForgot() {
        clearCodeFields();
        pendingEmail = null;
        MainFxml.getInstance().showForgotPassword();
    }

    private void clearCodeFields() {
        if (codeField1 != null) {
            codeField1.clear();
            codeField2.clear();
            codeField3.clear();
            codeField4.clear();
            codeField5.clear();
            codeField6.clear();
            codeField1.requestFocus();
        }
    }



    // ==================== RESET PASSWORD ====================
    @FXML
    private void handleResetPassword() {
        // .getText() fonctionne aussi avec PasswordField
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please fill in all fields.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Password must be at least 6 characters long.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Passwords do not match.");
            return;
        }

        if (verifiedEmail == null) {
            showAlert(Alert.AlertType.ERROR, "Session Expired",
                    "Your session has expired. Please start over.");
            MainFxml.getInstance().showForgotPassword();
            return;
        }

        try {
            // Désactiver le bouton pendant le traitement
            btnResetPassword.setDisable(true);
            btnResetPassword.setText("⏳ Processing...");

            boolean success = userService.resetPassword(verifiedEmail, newPassword);

            if (success) {
                EmailService.clearVerificationCode(verifiedEmail);
                verifiedEmail = null;

                showAlert(Alert.AlertType.INFORMATION, "✅ Password Reset Successful",
                        "Your password has been reset successfully!\n\n" +
                                "You can now login with your new password.");
                MainFxml.getInstance().showSignIn();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to reset password. Please try again.");
                btnResetPassword.setDisable(false);
                btnResetPassword.setText("✅ Reset Password");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred. Please try again.");
            btnResetPassword.setDisable(false);
            btnResetPassword.setText("✅ Reset Password");
        }
    }

    // ==================== UTILITY ====================
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getVerifiedEmail() {
        return verifiedEmail;
    }

    public static String getPendingEmail() {
        return pendingEmail;
    }
}