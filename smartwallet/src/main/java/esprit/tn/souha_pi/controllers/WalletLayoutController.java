package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.controllers.loan.LoanDetailsController;
import esprit.tn.souha_pi.entities.Notification;
import entities.User;
import esprit.tn.souha_pi.services.NotificationService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import utils.Session;

import java.net.URL;
import java.util.List;

public class WalletLayoutController {

    @FXML private StackPane contentArea;
    @FXML private Button adminDashboardBtn;
    @FXML private Label notifBadge;

    public static WalletLayoutController instance;

    private User currentUser = Session.getCurrentUser();
    private NotificationService notificationService = new NotificationService();

    @FXML
    public void initialize() {
        instance = this;

        if (adminDashboardBtn != null) {
            adminDashboardBtn.setVisible(false);
            adminDashboardBtn.setManaged(false);
        }

        if (notifBadge != null) {
            notifBadge.setVisible(false);
        }

        javafx.application.Platform.runLater(() -> loadPage("login.fxml"));
    }

    // ======================= LOAD PAGE =======================

    public void loadPage(String page) {
        try {
            System.out.println("📄 loadPage() called with: " + page);
            System.out.println("   contentArea is null? " + (contentArea == null));

            // Construire le chemin correct
            String path = "/fxml/" + page;
            if (page.startsWith("/")) {
                path = page;
            }

            System.out.println("   Trying to load from: " + path);

            // Chercher la ressource
            URL resource = getClass().getResource(path);
            if (resource == null) {
                System.out.println("   ❌ Resource NOT found at: " + path);
                // Essayer sans /fxml/
                path = "/" + page;
                resource = getClass().getResource(path);
                if (resource == null) {
                    System.out.println("   ❌ Resource NOT found at: " + path);
                    return;
                }
                System.out.println("   ✅ Found at alternative path: " + path);
            } else {
                System.out.println("   ✅ Found at: " + path);
            }

            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            System.out.println("   ✅ FXML loaded successfully");

            // S'assurer que contentArea existe
            if (contentArea == null) {
                System.out.println("   ❌ ERROR: contentArea is NULL - cannot add view");
                return;
            }

            // Ajouter la vue au contentArea
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            System.out.println("   ✅ View added to contentArea");
            System.out.println("✅ Page displayed successfully!");

        } catch (Exception e) {
            System.out.println("   ❌ ERROR loading page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // ======================= NAVIGATION =======================

    public void goDashboard() { loadPage("wallet/dashboard.fxml"); }
    public void goCards() { loadPage("wallet/cards.fxml"); }
    public void goSend() { loadPage("wallet/send.fxml"); }
    public void goReceive() { loadPage("wallet/receive.fxml"); }
    public void goHistory() { loadPage("wallet/history.fxml"); }
    public void goRequestLoan() { loadPage("loan/request.fxml"); }
    public void goLoanRequests() { loadPage("loan/requests.fxml"); }
    public void goMyLoans() { loadPage("loan/myloans.fxml"); }
    public void goAdminDashboard() { loadPage("admin/admin_dashboard.fxml"); }
    public void goToSignup() { loadPage("signup.fxml"); }
    public void openInscription() { loadPage("inscription_wallet.fxml"); }

    public void goAdvisor() {
        System.out.println("🤖 goAdvisor() called - Loading advisor page...");
        loadPage("wallet/advisor.fxml");
    }

    @FXML
    public void logout() {
        // à implémenter
    }

    // ======================= LOAN DETAILS =======================

    public void openLoanDetails(int loanId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/loan/loandetails.fxml")
            );
            Parent view = loader.load();
            LoanDetailsController controller = loader.getController();
            controller.loadLoan(loanId);
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================= NOTIFICATIONS =======================

    @FXML
    private void showNotifications() {

        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté.");
            return;
        }

        try {
            List<Notification> notifs =
                    notificationService.getNotificationsUtilisateur(currentUser.getId());

            if (notifs.isEmpty()) {
                DialogUtil.info("Notifications", "📭 Aucune notification.");
                return;
            }

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("🔔 Notifications");

            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            for (Notification n : notifs) {
                Label label = new Label(
                        n.getTitle() + "\n" +
                                n.getMessage() + "\n" +
                                n.getCreatedAt().toLocalDate()
                );
                label.setWrapText(true);
                label.setStyle("-fx-padding:10; -fx-background-color:#f1f5f9;");
                content.getChildren().add(label);
            }

            ScrollPane scroll = new ScrollPane(content);
            scroll.setFitToWidth(true);

            dialog.getDialogPane().setContent(scroll);
            dialog.getDialogPane().getButtonTypes()
                    .add(new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE));

            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}