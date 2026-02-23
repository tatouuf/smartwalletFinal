package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.controllers.loan.LoanDetailsController;
import esprit.tn.souha_pi.entities.Notification;
import esprit.tn.souha_pi.entities.User;
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

import java.util.List;

public class WalletLayoutController {

    @FXML private StackPane contentArea;
    @FXML private Button adminDashboardBtn;
    @FXML private Label notifBadge;  // Ajouté

    public static WalletLayoutController instance;

    entities.User currentUser = Session.getCurrentUser();
    private NotificationService notificationService = new NotificationService(); // Ajouté

    @FXML
    public void initialize(){
        instance = this;

        // Cacher le bouton admin par défaut
        if (adminDashboardBtn != null) {
            adminDashboardBtn.setVisible(false);
            adminDashboardBtn.setManaged(false);
        }

        // Cacher le badge de notifications par défaut
        if (notifBadge != null) {
            notifBadge.setVisible(false);
        }

        // Charger la page de connexion au démarrage
        javafx.application.Platform.runLater(() -> {
            loadPage("login.fxml");
        });
    }




    private void updateMenuVisibility() {
        if (adminDashboardBtn != null) {
            if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
                adminDashboardBtn.setVisible(true);
                adminDashboardBtn.setManaged(true);
            } else {
                adminDashboardBtn.setVisible(false);
                adminDashboardBtn.setManaged(false);
            }
        }
    }

    public void loadPage(String page){
        try{
            String path = "/fxml/" + page;

            FXMLLoader loader = new FXMLLoader(
                    WalletLayoutController.class.getResource(path)
            );

            if(loader.getLocation() == null){
                System.out.println("FXML INTROUVABLE: " + path);
                return;
            }

            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void loadPage2(String page){
        try{
            String path = "/" + page;

            FXMLLoader loader = new FXMLLoader(
                    WalletLayoutController.class.getResource(path)
            );

            if(loader.getLocation() == null){
                System.out.println("FXML INTROUVABLE: " + path);
                return;
            }

            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void logout() {

    }

    @FXML
    public void openInscription() {

        loadPage("inscription_wallet.fxml");
    }

    @FXML
    public void goAdminDashboard() {
        loadPage("admin/admin_dashboard.fxml");
    }

    // ======================= GESTION DES NOTIFICATIONS =======================

    @FXML
    private void showNotifications() {
        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté pour voir vos notifications");
            return;
        }

        try {
            List<Notification> notifs = notificationService.getNotificationsUtilisateur(currentUser.getId());

            if (notifs.isEmpty()) {
                DialogUtil.info("Notifications", "📭 Aucune notification pour le moment");
                return;
            }

            // Créer un dialogue personnalisé
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("🔔 Mes notifications");
            dialog.setHeaderText("Historique des notifications");

            // Créer le contenu
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.setPrefWidth(500);
            content.setPrefHeight(400);

            // Ajouter un ScrollPane
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            VBox notifsBox = new VBox(15);
            notifsBox.setPadding(new Insets(10));

            for (Notification n : notifs) {
                VBox notifCard = new VBox(5);
                notifCard.setStyle(
                        "-fx-background-color: #f8fafc;" +
                                "-fx-padding: 15;" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: #e2e8f0;" +
                                "-fx-border-radius: 10;"
                );

                // En-tête avec type et date
                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);

                String emoji = getEmojiForType(n.getType());
                Label typeLabel = new Label(emoji + " " + n.getType());
                typeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

                Label dateLabel = new Label(n.getCreatedAt().toLocalDate().toString());
                dateLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                header.getChildren().addAll(typeLabel, spacer, dateLabel);

                // Titre
                Label titleLabel = new Label(n.getTitle());
                titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

                // Message
                Label msgLabel = new Label(n.getMessage());
                msgLabel.setWrapText(true);
                msgLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");

                // Statut
                Label statusLabel = new Label("● " + n.getStatus());
                statusLabel.setStyle(getStatusStyle(n.getStatus()));

                notifCard.getChildren().addAll(header, titleLabel, msgLabel, statusLabel);
                notifsBox.getChildren().add(notifCard);
            }

            scrollPane.setContent(notifsBox);
            content.getChildren().add(scrollPane);

            // Bouton Fermer
            ButtonType fermer = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(fermer);
            dialog.getDialogPane().setContent(content);

            dialog.showAndWait();

        } catch (Exception e) {
            DialogUtil.error("Erreur", "Impossible de charger les notifications: " + e.getMessage());
        }
    }

    private String getEmojiForType(String type) {
        switch (type) {
            case "EMAIL": return "📧";
            case "SMS": return "📱";
            case "RAPPEL": return "⏰";
            case "PUSH": return "🔔";
            default: return "📌";
        }
    }

    private String getStatusStyle(String status) {
        if ("envoyé".equals(status)) {
            return "-fx-text-fill: #22c55e; -fx-font-size: 11px;";
        } else if ("en_attente".equals(status)) {
            return "-fx-text-fill: #f59e0b; -fx-font-size: 11px;";
        } else {
            return "-fx-text-fill: #ef4444; -fx-font-size: 11px;";
        }
    }

    public void mettreAJourBadgeNotifications() {
        if (currentUser == null || notifBadge == null) return;

        try {
            List<Notification> nonLues = notificationService.getNotificationsUtilisateur(currentUser.getId())
                    .stream()
                    .filter(n -> "en_attente".equals(n.getStatus()))
                    .toList();

            if (!nonLues.isEmpty()) {
                notifBadge.setText(String.valueOf(nonLues.size()));
                notifBadge.setVisible(true);
                notifBadge.setManaged(true);
            } else {
                notifBadge.setVisible(false);
                notifBadge.setManaged(false);
            }
        } catch (Exception e) {
            notifBadge.setVisible(false);
        }
    }

    // ======================= AUTRES MÉTHODES =======================

    public void goCards(){
        loadPage("wallet/cards.fxml");
    }

    public void goRequestLoan(){
        loadPage("loan/request.fxml");
    }

    public void openLoanDetails(int loanId){
        try{
            FXMLLoader loader = new FXMLLoader(
                    WalletLayoutController.class.getResource("/fxml/loan/loandetails.fxml")
            );
            Parent view = loader.load();
            LoanDetailsController controller = loader.getController();
            controller.loadLoan(loanId);
            contentArea.getChildren().setAll(view);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void goToSignup() {
        loadPage("signup.fxml");
    }

    public void goMyLoans(){
        loadPage("loan/myloans.fxml");
    }

    public void goDashboard(){
        loadPage("wallet/dashboard.fxml");
    }

    public void goLoanRequests(){
        loadPage("loan/requests.fxml");
    }

    public void goSend(){
        loadPage("wallet/send.fxml");
    }

    public void goReceive(){
        loadPage("wallet/receive.fxml");
    }

    public void goHistory(){
        loadPage("wallet/history.fxml");
    }
}