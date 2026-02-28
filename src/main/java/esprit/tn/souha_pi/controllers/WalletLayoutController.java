package esprit.tn.souha_pi.controllers;

<<<<<<< HEAD
import entities.User;
import esprit.tn.souha_pi.controllers.loan.LoanDetailsController;
import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Notification;
import esprit.tn.souha_pi.services.NotificationService;
import esprit.tn.souha_pi.utils.DialogUtil;
import esprit.tn.souha_pi.utils.EventBus;
=======
import esprit.tn.souha_pi.controllers.loan.LoanDetailsController;
import esprit.tn.souha_pi.entities.Notification;
import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.NotificationService;
import esprit.tn.souha_pi.utils.DialogUtil;
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

<<<<<<< HEAD
import java.net.URL;
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import java.util.List;

public class WalletLayoutController {

    @FXML private StackPane contentArea;
    @FXML private Button adminDashboardBtn;
    @FXML private Label notifBadge;

    public static WalletLayoutController instance;

    private User currentUser;
    private NotificationService notificationService = new NotificationService();

<<<<<<< HEAD
    // Dans WalletLayoutController.java - MODIFIEZ initialize()
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
=======
    @FXML
    public void initialize(){
        instance = this;

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        if (adminDashboardBtn != null) {
            adminDashboardBtn.setVisible(false);
            adminDashboardBtn.setManaged(false);
        }

        if (notifBadge != null) {
            notifBadge.setVisible(false);
        }

<<<<<<< HEAD
        // NE PAS charger SignIn.fxml ici
        // Cette ligne est la cause du problème
        // javafx.application.Platform.runLater(() -> {
        //     loadPage("/SignIn.fxml");
        // });
=======
        javafx.application.Platform.runLater(() -> {
            loadPage("login.fxml");
        });
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateMenuVisibility();
        mettreAJourBadgeNotifications();
<<<<<<< HEAD

        // Charger la page appropriée après connexion
        if (user != null) {
            loadPage("wallet/dashboard.fxml");
        }
    }

    public void loadDefaultPage() {
        if (currentUser != null) {
            // Si l'utilisateur est connecté, charger le dashboard
            loadPage("wallet/dashboard.fxml");
        } else {
            // Sinon, charger la page de connexion
            loadPage("/SignIn.fxml");
        }
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
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
<<<<<<< HEAD
    public void loadPage(String page){
        try{
            String path = page;

            // Log de la requête initiale
            System.out.println("🔍 Demande de chargement: " + page);

            // Si le chemin ne commence pas par "/", ajoutez le préfixe /fxml/
            if (!path.startsWith("/")) {
                path = "/fxml/" + path;
            }

            System.out.println("📂 Chargement de: " + path);

            // Vérifier que la ressource existe
            URL resourceUrl = WalletLayoutController.class.getResource(path);
            if(resourceUrl == null){
                System.out.println("❌ FXML INTROUVABLE: " + path);

                // Essayer des chemins alternatifs
                String[] alternatives = {
                        path.replace("/fxml/", "/"),                    // /wallet/cards.fxml
                        "/fxml" + path,                                  // /fxml/fxml/wallet/cards.fxml
                        "/" + path.replace("/fxml/", ""),               // /wallet/cards.fxml
                        path.substring(path.lastIndexOf("/")),           // /cards.fxml
                        "/fxml/wallet" + path.substring(path.lastIndexOf("/")) // /fxml/wallet/cards.fxml (si déjà bon)
                };

                boolean found = false;
                for (String alt : alternatives) {
                    if (alt.equals(path)) continue; // Éviter de réessayer le même chemin

                    System.out.println("🔄 Essai avec: " + alt);
                    resourceUrl = WalletLayoutController.class.getResource(alt);
                    if (resourceUrl != null) {
                        path = alt;
                        System.out.println("✅ Trouvé avec: " + alt);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Afficher la liste des ressources disponibles pour aider au débogage
                    System.err.println("❌ Aucun FXML trouvé pour: " + page);
                    System.err.println("🔍 Chemins essayés:");
                    System.err.println("   - " + path);
                    for (String alt : alternatives) {
                        System.err.println("   - " + alt);
                    }

                    Label errorLabel = new Label("Page introuvable: " + page + "\nVérifiez le chemin du fichier.");
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-alignment: center;");
                    errorLabel.setWrapText(true);
                    contentArea.getChildren().setAll(errorLabel);
                    return;
                }
            }

            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent view = loader.load();

            // Récupérer le contrôleur si nécessaire
            Object controller = loader.getController();
            if (controller != null) {
                System.out.println("✅ Contrôleur chargé: " + controller.getClass().getSimpleName());
            }

            // Mettre à jour l'affichage
            contentArea.getChildren().setAll(view);
            System.out.println("✅ Page chargée avec succès: " + path);

        } catch (Exception e){
            System.err.println("❌ Erreur lors du chargement de " + page + ": " + e.getMessage());
            e.printStackTrace();

            // Afficher l'erreur dans l'interface
            String errorMsg = "Erreur de chargement: " + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += "\nCause: " + e.getCause().getMessage();
            }

            Label errorLabel = new Label(errorMsg);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-alignment: center;");
            errorLabel.setWrapText(true);
            contentArea.getChildren().setAll(errorLabel);
=======

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
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        }
    }

    @FXML
<<<<<<< HEAD
    public void openInscription() {
        System.out.println("🏠 Retour à l'accueil");
        loadPage("/LandingPage.fxml");
    }

    @FXML
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    public void logout() {
        boolean confirm = DialogUtil.confirm(
                "Déconnexion",
                "Voulez-vous vraiment vous déconnecter ?"
        );

        if (confirm) {
            setCurrentUser(null);
<<<<<<< HEAD
            loadPage("/SignIn.fxml");  // ← CORRIGÉ
=======
            loadPage("login.fxml");
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            DialogUtil.success("Déconnexion", "Vous avez été déconnecté avec succès.");
        }
    }

    @FXML
<<<<<<< HEAD
=======
    public void openInscription() {
        loadPage("wallet/inscription_wallet.fxml");
    }

    @FXML
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    public void goAdminDashboard() {
        loadPage("admin/admin_dashboard.fxml");
    }

<<<<<<< HEAD
    // ======================= MÉTHODES DE NAVIGATION =======================

    @FXML
    public void goDashboard() {
        System.out.println("📋 Navigation vers Dashboard");
        loadPage("wallet/dashboard.fxml");
    }

    @FXML
    public void goCards() {
        System.out.println("💳 Navigation vers Mes Cartes");
        loadPage("wallet/cards.fxml");
    }

    @FXML
    public void goSend() {
        System.out.println("📤 Navigation vers Envoyer");
        loadPage("wallet/send.fxml");
    }

    @FXML
    public void goReceive() {
        System.out.println("📥 Navigation vers Recevoir");
        loadPage("wallet/receive.fxml");
    }

    @FXML
    public void goHistory() {
        System.out.println("📜 Navigation vers Historique");
        loadPage("wallet/history.fxml");
    }

    @FXML
    public void goRequestLoan() {
        System.out.println("💰 Navigation vers Demander un prêt");
        loadPage("loan/request.fxml");
    }

    @FXML
    public void goLoanRequests() {
        System.out.println("📋 Navigation vers Demandes reçues");
        loadPage("loan/requests.fxml");
    }

    @FXML
    public void goMyLoans() {
        System.out.println("📊 Navigation vers Mes prêts");
        loadPage("loan/myloans.fxml");
    }

=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
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

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("🔔 Mes notifications");
            dialog.setHeaderText("Historique des notifications");

            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.setPrefWidth(500);
            content.setPrefHeight(400);

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

                Label titleLabel = new Label(n.getTitle());
                titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

                Label msgLabel = new Label(n.getMessage());
                msgLabel.setWrapText(true);
                msgLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");

                Label statusLabel = new Label("● " + n.getStatus());
                statusLabel.setStyle(getStatusStyle(n.getStatus()));

                notifCard.getChildren().addAll(header, titleLabel, msgLabel, statusLabel);
                notifsBox.getChildren().add(notifCard);
            }

            scrollPane.setContent(notifsBox);
            content.getChildren().add(scrollPane);

            ButtonType fermer = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(fermer);
            dialog.getDialogPane().setContent(content);
<<<<<<< HEAD
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
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

<<<<<<< HEAD
=======
    public void goCards(){
        loadPage("wallet/cards.fxml");
    }

    public void goRequestLoan(){
        loadPage("loan/request.fxml");
    }

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    public void openLoanDetails(int loanId){
        try{
            FXMLLoader loader = new FXMLLoader(
                    WalletLayoutController.class.getResource("/fxml/loan/loandetails.fxml")
            );
            Parent view = loader.load();
            LoanDetailsController controller = loader.getController();
            controller.loadLoan(loanId);
            contentArea.getChildren().setAll(view);
<<<<<<< HEAD
        } catch(Exception e){
=======
        }catch(Exception e){
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            e.printStackTrace();
        }
    }

    public void goToSignup() {
        loadPage("signup.fxml");
    }
<<<<<<< HEAD
=======

    public void goMyLoans(){
        loadPage("loan/myloans.fxml");
    }

    public void goDashboard(){
        loadPage("wallet/dashboard.fxml");
    }

    public void goDashboardDepens(){
        loadPage("wallet/dashboarddepens.fxml");
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

    @FXML
    public void goDepenses(){
        loadPage("wallet/depenses.fxml");
    }

    @FXML
    public void goBudget(){
        loadPage("wallet/budget.fxml");
    }
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
}