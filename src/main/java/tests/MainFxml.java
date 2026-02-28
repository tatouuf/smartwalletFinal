package tests;

import esprit.tn.souha_pi.controllers.SendController;
import esprit.tn.souha_pi.controllers.WalletLayoutController;
import esprit.tn.souha_pi.entities.BankCard;
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

    // Wallet préchargé
    private WalletLayoutController preloadedWalletController;
    private Parent preloadedWalletRoot;
    private boolean isWalletPreloaded = false;

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

        // Précharger WalletLayout en arrière-plan
        preloadWalletLayout();

        // Démarrer le serveur API
        try {
            api.APIServer.start();
            logger.info("REST API Server started successfully");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to start API server", e);
        }

        // Afficher l'écran de connexion
        showSignIn();
        primaryStage.show();

        // Hook d'arrêt
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

    /**
     * Précharger WalletLayout pour un accès instantané
     */


    /**
     * Ouvrir le wallet et naviguer directement vers la page d'envoi avec une carte
     */
    public void openSendWithCard(BankCard carte) {
        System.out.println("📨 Ouverture de send avec carte: " + carte.getCardNumber());

        // Sauvegarder la carte dans SendController
        SendController.setCarteSource(carte);

        // Ouvrir le wallet et naviguer vers send
        openWalletLayoutWithSection("SEND", carte);
    }
    // Dans tests.MainFxml.java - Ajoutez cette méthode
    /**
     * Afficher la popup des services client
     */
    public void showServiceClientPopup() {
        openServiceClientPopup(); // Appelle la méthode existante
    }



    /**
     * Ouvrir le wallet et naviguer vers une section spécifique
     */
    public void openWalletLayoutWithSection(String section) {
        openWalletLayoutWithSection(section, null);
    }

    /**
     * Ouvrir le wallet et naviguer vers une section avec des données
     */

    public void openWalletLayoutWithSection(String section, Object data) {
        try {
            System.out.println("🔄 Création d'une nouvelle instance du wallet");

            // NE PAS UTILISER le wallet préchargé - toujours créer une nouvelle instance
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/layout/wallet_layout.fxml"));
            Parent root = loader.load();  // ← Nouvelle instance à chaque fois
            WalletLayoutController controller = loader.getController();

            // Stocker le contrôleur dans les propriétés du root
            root.getProperties().put("controller", controller);

            Stage stage = new Stage();
            stage.setTitle("My Wallet");

            Scene scene = new Scene(root, 1100, 700);

            // Charger le CSS
            URL css = getClass().getResource("/css/theme.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setScene(scene);
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);

            // Si une section est demandée, naviguer après l'ouverture
            if (section != null) {
                stage.setOnShown(event -> {
                    javafx.application.Platform.runLater(() -> {
                        naviguerDansWallet(controller, section, data);
                    });
                });
            }

            stage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur à l'ouverture du wallet", e);
            e.printStackTrace();
        }
    }

    // SUPPRIMEZ ou commentez preloadWalletLayout
    private void preloadWalletLayout() {
        // NE PLUS PRÉCHARGER pour éviter les conflits
        System.out.println("✅ Préchargement désactivé");
    }

    /**
     * Naviguer dans le wallet
     */
    private void naviguerDansWallet(WalletLayoutController controller, String section, Object data) {
        if (controller == null) return;

        System.out.println("🚀 Navigation dans wallet vers: " + section);

        // Passer les données si nécessaire
        if (data instanceof BankCard && "SEND".equals(section)) {
            SendController.setCarteSource((BankCard) data);
        }

        // Naviguer
        switch (section) {
            case "SEND":
                controller.goSend();
                break;
            case "RECEIVE":
                controller.goReceive();
                break;
            case "HISTORY":
                controller.goHistory();
                break;
            case "CARDS":
                controller.goCards();
                break;
            case "DASHBOARD":
                controller.goDashboard();
                break;
            default:
                controller.goDashboard();
                break;
        }
    }

    /**
     * Ouvrir le wallet en popup (méthode existante modifiée)
     */
    public Stage openWalletLayoutPopup() {
        openWalletLayoutWithSection(null, null);
        return null;
    }

    // ==================== AUTHENTICATION PAGES ====================

    public void showSignIn() { loadScene("/SignIn.fxml"); }
    public void showSignUp() { loadScene("/SignUp.fxml"); }
    public void showForgotPassword() { loadScene("/ForgotPassword.fxml"); }
    public void showResetPassword() { loadScene("/ResetPassword.fxml"); }
    public void showVerifyCode() { loadScene("/VerifyCode.fxml"); }

    // ==================== USER PAGES ====================

    public void showWalletHome() { loadScene("/LandingPage.fxml"); }
    public void showFriendsList() { loadScene("/FriendsList.fxml"); }
    public void showAmitie() { loadScene("/Amitie.fxml"); }
    public void showReclamationUser() { loadScene("/ReclamationUser.fxml"); }
    public void showReclamationDetail() { loadScene("/ReclamationDetails.fxml"); }
    public void showNotifications() { loadScene("/Notifications.fxml"); }

    // ==================== ADMIN PAGES ====================

    public void showDashboard() { loadScene("/DashboardAdmin.fxml"); }
    public void showReclamationAdmin() { loadScene("/ReclamationAdmin.fxml"); }
    public void showServiceAdmin() { loadScene("/acceuilservices/AcceuilService.fxml"); }

    // ==================== SERVICES MODULE ====================

    public void showServiceClient() { loadScene("/acceuilservices/AcceuilServiceClient.fxml"); }

    // ==================== LOGOUT ====================

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

    public Stage openServiceAdminPopup() {
        return openPopup("/acceuilservices/AcceuilService.fxml", "Services - Admin", 1100, 700, true);
    }

    public Stage openPopup(String fxmlPath, String title, double width, double height, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);

            // Charger le CSS
            URL css = getClass().getResource("/css/theme.css");
            if (css != null) {
                root.getStylesheets().add(css.toExternalForm());
            }

            Scene scene = (width > 0 && height > 0) ? new Scene(root, width, height) : new Scene(root);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.initOwner(primaryStage);

            if (modal) {
                stage.initModality(Modality.WINDOW_MODAL);
            }

            stage.centerOnScreen();
            stage.show();
            return stage;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to open popup: " + fxmlPath, e);
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
        }
    }

    public static void main(String[] args) {
        System.out.println("SMARTWALLET starting...");
        launch(args);
    }
}