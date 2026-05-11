package tests;

import esprit.tn.souha_pi.controllers.WalletLayoutController;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFxml extends Application {

    private static final Logger logger = Logger.getLogger(MainFxml.class.getName());
    private static MainFxml instance;
    private Stage primaryStage;

    // 👇 AJOUTER CETTE VARIABLE STATIQUE
    private static HostServices hostServices;

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

        // 👇 INITIALISER HOSTSERVICES ICI
        hostServices = getHostServices();

        // souha.said@esprit.tn
        // souhasouha1234

        // ========== START REST API SERVER ==========
        try {
            api.APIServer.start();
            logger.info("REST API Server started successfully");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to start API server", e);
        }

        // ========== SHOW INITIAL SCREEN ==========
        showSignIn();
        testSymfonyIntegration();
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

    // ==================== WALLET PAGES ====================

    /**
     * Affiche la page d'inscription au wallet
     */
    public void showWalletInscription() {
        loadScene("/fxml/wallet/dashboard.fxml");
    }

    /**
     * Affiche le layout du wallet (après inscription)
     */
    public void showWalletLayout() {
        loadScene("/fxml/layout/wallet_layout.fxml");
    }

    // ==================== USER PAGES ====================

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

    // ==================== SERVICES MODULE ====================

    public void showServiceAdmin() { loadScene("/acceuilservices/AcceuilService.fxml"); }
    public void showServiceClient() { loadScene("/acceuilservices/AcceuilServiceClient.fxml"); }

    // ==================== LOGOUT ====================

    public void showWalletHome() { loadScene("/LandingPage.fxml"); }

    public void showFinancialDashboard() {
        loadScene("/com/example/smartwallet/dashboard-view.fxml");
    }

    public void logout() {
        utils.Session.clearSession();
        showSignIn();
        logger.info("User logged out successfully");
    }

    // ==================== POPUP METHODS ====================

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

    public Stage openWalletLayoutPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/layout/wallet_layout.fxml"));
            Parent root = loader.load();
            WalletLayoutController controller = loader.getController();

            // PASSER L'UTILISATEUR CONNECTÉ
            controller.setCurrentUser(Session.getCurrentUser());

            Stage stage = new Stage();
            stage.setTitle("SmartWallet");
            stage.setScene(new Scene(root, 1100, 700));
            stage.show();

            return stage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Stage openServiceAdminPopup() {
        return openPopup("/acceuilservices/AcceuilService.fxml", "Services - Admin", 1100, 700, true);
    }

    public Stage openBudgetExpensesPopup() {
        // Initialiser le service de notifications intelligent
        try {
            esprit.tn.chayma.services.NotificationInitializationService notifService = new esprit.tn.chayma.services.NotificationInitializationService();
            int userId = (utils.Session.getCurrentUser() != null) ? utils.Session.getCurrentUser().getId() : 1;
            notifService.initialize(userId);
            notifService.performCheck();
            notifService.startPeriodicChecks(30);
            logger.info("Intelligent Notification Service started for Budget & Expenses");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to initialize Intelligent Notification Service", e);
        }

        return openPopup("/fxml/dep/deplayoutLayout.fxml", "Budget & Expenses", 1200, 750, true, "/css/budget_theme.css");
    }

    // ==================== PROFILE POPUP ====================

    public Stage openProfilePopup() {
        return openPopup("/Profile.fxml", "Mon Profil", 520, 640, true);
    }

    // ==================== GENERIC POPUP OPENER ====================

    /**
     * Generic popup opener (modal by default).
     * modal=true -> blocks main window until closed
     */
    public Stage openPopup(String fxmlPath, String title, double width, double height, boolean modal) {
        return openPopup(fxmlPath, title, width, height, modal, "/css/theme.css");
    }

    public Stage openPopup(String fxmlPath, String title, double width, double height, boolean modal, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            // Load CSS
            URL css = getClass().getResource(cssPath);
            if (css != null) {
                root.getStylesheets().add(css.toExternalForm());
            } else {
                System.out.println("CSS NOT FOUND: " + cssPath);
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

    public void showVerifyCode() {
        loadScene("/VerifyCode.fxml");
    }

    // ==================== OPEN NEW WINDOW ====================
    private void loadScene(String fxmlPath) {
        try {
            logger.info("Opening new window: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1100, 720);

            Stage newStage = new Stage();
            newStage.setTitle("SmartWallet");
            newStage.setScene(scene);

            newStage.setMinWidth(950);
            newStage.setMinHeight(600);

            newStage.setWidth(1100);
            newStage.setHeight(720);

            newStage.setResizable(true);
            newStage.centerOnScreen();

            // Ouvre une fenêtre normale
            newStage.show();

            logger.info("New window opened successfully: " + fxmlPath);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to open window: " + fxmlPath, e);

            showAlert(
                    "Error Loading Page",
                    "Failed to load the requested page.\n\nError: " + e.getMessage() +
                            "\n\nPlease check the console for details."
            );
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

    // 👇 DÉPLACER CETTE MÉTHODE ICI (après showAlert)
    public static HostServices getHostServicesInstance() {
        return hostServices;
    }

    public static void main(String[] args) {
        System.out.println("SMARTWALLET starting...");
        launch(args);
    }

    public void testSymfonyIntegration() {
        try {
            URL url = new URL("http://127.0.0.1:8000/api/integration/health");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // IMPORTANT : empêcher Java de suivre la redirection vers /login
            conn.setInstanceFollowRedirects(false);

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 301 || responseCode == 302) {
                String location = conn.getHeaderField("Location");

                System.out.println("Symfony redirected to: " + location);

                javafx.scene.control.Alert alert =
                        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);

                alert.setTitle("Erreur Symfony");
                alert.setHeaderText("Symfony redirige vers login");
                alert.setContentText(
                        "JavaFX arrive à contacter Symfony, mais Symfony redirige vers :\n\n"
                                + location
                                + "\n\nSolution : autoriser /api/integration dans security.yaml"
                );
                alert.showAndWait();
                return;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            System.out.println("Symfony response code: " + responseCode);
            System.out.println("Symfony response: " + response);

            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);

            alert.setTitle("Intégration Symfony");
            alert.setHeaderText("Connexion Symfony réussie");
            alert.setContentText(response.toString());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();

            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);

            alert.setTitle("Erreur Symfony");
            alert.setHeaderText("Impossible de connecter JavaFX à Symfony");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}