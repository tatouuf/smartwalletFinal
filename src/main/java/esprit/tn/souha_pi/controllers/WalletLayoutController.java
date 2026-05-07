package esprit.tn.souha_pi.controllers;

import entities.User;
import esprit.tn.souha_pi.controllers.loan.LoanDetailsController;
import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Notification;
import esprit.tn.souha_pi.services.NotificationService;
import esprit.tn.souha_pi.utils.DialogUtil;
import esprit.tn.souha_pi.utils.EventBus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.List;

public class WalletLayoutController {

    @FXML private StackPane contentArea;
    @FXML private Button adminDashboardBtn;
    @FXML private Label notifBadge;  // Ajouté

    public static WalletLayoutController instance;

    private User currentUser;
    private NotificationService notificationService = new NotificationService(); // Ajouté

    @FXML
    public void initialize(){
        instance = this;
        System.out.println("✅ WalletLayoutController.initialize() appelé - instance=" + this);

        // S'abonner aux événements de navigation
        EventBus.getInstance().subscribe("NAVIGATE_TO_SEND", data -> {
            javafx.application.Platform.runLater(() -> {
                System.out.println("📨 Événement NAVIGATE_TO_SEND reçu avec data: " + data);
                if (data instanceof BankCard) {
                    SendController.setCarteSource((BankCard) data);
                }
                goSend();
            });
        });

        EventBus.getInstance().subscribe("NAVIGATE_TO_RECEIVE", data -> {
            javafx.application.Platform.runLater(() -> {
                goReceive();
            });
        });

        EventBus.getInstance().subscribe("NAVIGATE_TO_HISTORY", data -> {
            javafx.application.Platform.runLater(() -> {
                goHistory();
            });
        });

        // Cacher le bouton admin par défaut
        if (adminDashboardBtn != null) {
            adminDashboardBtn.setVisible(false);
            adminDashboardBtn.setManaged(false);
        }

        if (notifBadge != null) {
            notifBadge.setVisible(false);
        }

        // NE PAS CHARGER signin.fxml ICI
        // La page sera chargée en fonction de l'état de connexion
        // Cette ligne est la cause du problème :
        javafx.application.Platform.runLater(() -> {
           loadPage("signin.fxml");
         });
    }

    // Modifiez setCurrentUser pour charger la bonne page
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateMenuVisibility();
        mettreAJourBadgeNotifications();

        // Charger la page appropriée
        if (user != null) {
            // Utilisateur connecté → charger le dashboard
            System.out.println("👤 Utilisateur connecté, chargement du dashboard");
            loadPage("/fxml/wallet/dashboard.fxml");
        } else {
            // Utilisateur déconnecté → page de connexion
            System.out.println("🔒 Utilisateur déconnecté, chargement de SignIn");
            loadPage("/SignIn.fxml");
        }
    }



    public User getCurrentUser() {
        return currentUser;
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

    // Dans WalletLayoutController.java, modifiez loadPage
    public void loadPage(String page){
        try{
            String path = page;

            if (!path.startsWith("/")) {
                path = "/fxml/" + path;
            }

            System.out.println("📂 Chargement de: " + path);

            URL resourceUrl = WalletLayoutController.class.getResource(path);
            if(resourceUrl == null){
                System.out.println("❌ FXML INTROUVABLE: " + path);

                // Chemins alternatifs
                if (path.equals("/fxml/signin.fxml") || path.equals("signin.fxml")) {
                    path = "/SignIn.fxml";
                    resourceUrl = WalletLayoutController.class.getResource(path);
                } else if (path.equals("/fxml/InscriptionWallet.fxml")) {
                    path = "/LandingPage.fxml";
                    resourceUrl = WalletLayoutController.class.getResource(path);
                }

                if (resourceUrl == null) {
                    Label errorLabel = new Label("Page introuvable: " + page);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    contentArea.getChildren().setAll(errorLabel);
                    return;
                }
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

        }catch(Exception e){
            e.printStackTrace();
            Label errorLabel = new Label("Erreur de chargement: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            contentArea.getChildren().setAll(errorLabel);
        }
    }

    @FXML
    public void openInscription() {

        loadPage("InscriptionWallet.fxml");  // Au lieu de InscriptionWallet.fxml
    }

    @FXML
    public void logout() {
        boolean confirm = DialogUtil.confirm(
                "Déconnexion",
                "Voulez-vous vraiment vous déconnecter ?"
        );

        if (confirm) {
            setCurrentUser(null);
            loadPage("signin.fxml");
            DialogUtil.success("Déconnexion", "Vous avez été déconnecté avec succès.");
        }
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