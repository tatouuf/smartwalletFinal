package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFxml extends Application {

    private static final Logger logger = Logger.getLogger(MainFxml.class.getName());
    private static MainFxml instance;
    private Stage primaryStage;

    public MainFxml() {
        instance = this;
    }

    public static MainFxml getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("SmartWallet - AI-Powered Financial Management");
        primaryStage.setResizable(false);

        // ========== START REST API SERVER ==========
        try {
            api.APIServer.start();
            logger.info("REST API Server started successfully");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to start API server", e);
        }

        // ========== SHOW INITIAL SCREEN ==========
        showSignIn();
        primaryStage.show();

        // ========== SHUTDOWN HOOKS ==========
        primaryStage.setOnCloseRequest(event -> {
            logger.info("Application shutting down...");

            try {
                api.APIServer.stop();
                logger.info("API Server stopped");
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error stopping API server", e);
            }

            utils.Session.clearSession();
            System.exit(0);
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    // ==================== AUTHENTICATION PAGES ====================

    public void showSignIn() { loadScene("/SignIn.fxml"); }
    public void showSignUp() { loadScene("/SignUp.fxml"); }
    public void showForgotPassword() { loadScene("/ForgotPassword.fxml"); }
    public void showResetPassword() { loadScene("/ResetPassword.fxml"); }

    // ==================== USER PAGES (KEEP AS IS) ====================

    public void showFriendsList() { loadScene("/FriendsList.fxml"); }
    public void showAmitie() { loadScene("/Amitie.fxml"); }
    public void showReclamationUser() { loadScene("/ReclamationUser.fxml"); }
    public void showReclamationDetail() { loadScene("/ReclamationDetails.fxml"); }
    public void showNotifications() { loadScene("/Notifications.fxml"); }

    // ==================== ADMIN PAGES ====================

    public void showDashboard() { loadScene("/DashboardAdmin.fxml"); }
    public void showReclamationAdmin() { loadScene("/ReclamationAdmin.fxml"); }

    public void showManageUsers() {
        showAlert("Feature Coming Soon",
                "User Management interface will be available in the next update.");
    }

    // ==================== SERVICES MODULE (KEEP AS IS) ====================

    public void showServiceAdmin() { loadScene("/acceuilservices/AcceuilService.fxml"); }
    public void showWalletLayout() { loadScene("/fxml/layout/wallet_layout.fxml"); }
    public void showServiceClient() { loadScene("/acceuilservices/AcceuilServiceClient.fxml"); }

    // ==================== LOGOUT ====================

    public void showWalletHome() { loadScene("/LandingPage.fxml"); }

    public void logout() {
        utils.Session.clearSession();
        showSignIn();
        logger.info("User logged out successfully");
    }

    // =====================================================================
    // ✅ NEW: POPUP METHODS (DO NOT BREAK OLD CODE)
    // Use these from LandingPage & FriendsList to avoid replacing main scene
    // =====================================================================

    public Stage openFriendsListPopup() {
        return openPopup("/FriendsList.fxml", "Friends & Invitations", 900, 650, true);
    }

    public Stage openAmitiePopup() {
        return openPopup("/Amitie.fxml", "Amitié", 850, 600, true);
    }

    public Stage openReclamationUserPopup() {
        return openPopup("/ReclamationUser.fxml", "Mes Réclamations", 1000, 700, true);
    }

    public Stage openReclamationDetailPopup() {
        return openPopup("/ReclamationDetail.fxml", "Détail Réclamation", 1000, 700, true);
    }

    public Stage openNotificationsPopup() {
        return openPopup("/Notifications.fxml", "Notifications", 900, 650, true);
    }

    public Stage openServiceClientPopup() {
        return openPopup("/acceuilservices/AcceuilServiceClient.fxml", "Services - Client", 1100, 700, true);
    }

    public Stage openServiceAdminPopup() {
        return openPopup("/acceuilservices/AcceuilService.fxml", "Services - Admin", 1100, 700, true);
    }

    public Stage openWalletLayoutPopup() {

        return openPopup("/fxml/layout/wallet_layout.fxml", "My Wallet", 1100, 700, true);
    }

    /**
     * Generic popup opener (modal by default).
     * modal=true -> blocks main window until closed
     */
    public Stage openPopup(String fxmlPath, String title, double width, double height, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            // Load CSS
            URL css = getClass().getResource("/css/theme.css");
            if (css != null) {
                root.getStylesheets().add(css.toExternalForm());
            } else {
                System.out.println("CSS NOT FOUND");
            }
            Scene scene = (width > 0 && height > 0) ? new Scene(root, width, height) : new Scene(root);
            stage.setScene(scene);
            stage.setResizable(true);

            // owner = main landing window
            stage.initOwner(primaryStage);

            if (modal) {
                stage.initModality(Modality.WINDOW_MODAL);
            }

            stage.centerOnScreen();
            stage.show();
            return stage;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to open popup: " + fxmlPath, e);
            showAlert("Error Loading Popup",
                    "Failed to open popup.\n\nFXML: " + fxmlPath + "\n\nError: " + e.getMessage());
            return null;
        }
    }

    // ==================== SCENE LOADER ====================

    private void loadScene(String fxmlPath) {
        try {
            logger.info("Loading scene: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

            logger.info("Scene loaded successfully: " + fxmlPath);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load scene: " + fxmlPath, e);
            showAlert("Error Loading Page",
                    "Failed to load the requested page.\n\nError: " + e.getMessage() +
                            "\n\nPlease check the console for details.");
        }
    }

    // ==================== ALERT HELPER ====================

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        System.out.println("SMARTWALLET starting...");
        launch(args);
    }
}