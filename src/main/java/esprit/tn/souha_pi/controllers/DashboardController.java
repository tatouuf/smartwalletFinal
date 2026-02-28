package esprit.tn.souha_pi.controllers;

<<<<<<< HEAD
=======
import esprit.tn.souha_pi.entities.User;
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.services.BankCardService;
<<<<<<< HEAD
import esprit.tn.souha_pi.utils.EventBus;
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import esprit.tn.souha_pi.utils.DialogUtil;
<<<<<<< HEAD
import tests.MainFxml;
import utils.Session;
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

public class DashboardController {

    @FXML private Label balanceLabel;
    @FXML private Label welcomeLabel;
    @FXML private VBox cardsContainer;
    @FXML private Button addCardButton;
    @FXML private Label noWalletLabel;
    @FXML private Button createWalletButton;

    private static DashboardController instance;
    private WalletService walletService = new WalletService();
    private BankCardService cardService = new BankCardService();
<<<<<<< HEAD
    entities.User currentUser = Session.getCurrentUser();
=======
    private User currentUser;
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

    public DashboardController(){
        instance = this;
    }

    @FXML
    public void initialize(){
<<<<<<< HEAD
        entities.User currentUser = Session.getCurrentUser();
=======
        currentUser = WalletLayoutController.instance.getCurrentUser();
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

        if (currentUser != null) {
            welcomeLabel.setText("Bienvenue " + currentUser.getFullname());
            refreshDashboard();
        }
    }

    public void refreshDashboard(){
        try {
            // Récupérer le wallet de l'utilisateur connecté
            Wallet wallet = walletService.getByUserId(currentUser.getId());
            balanceLabel.setText(String.format("%.2f TND", wallet.getBalance()));

            if (noWalletLabel != null) noWalletLabel.setVisible(false);
            if (createWalletButton != null) createWalletButton.setVisible(false);

            // Afficher les cartes de l'utilisateur connecté
            afficherCartes();

        } catch (Exception e) {
            // Pas de wallet
            balanceLabel.setText("0.00 TND");
            if (noWalletLabel != null) {
                noWalletLabel.setVisible(true);
                noWalletLabel.setText("Vous n'avez pas encore de wallet");
            }
            if (createWalletButton != null) {
                createWalletButton.setVisible(true);
            }
            if (cardsContainer != null) {
                cardsContainer.getChildren().clear();
            }
        }
    }

    private void afficherCartes() {
        if (cardsContainer != null) {
            cardsContainer.getChildren().clear();

            try {
                // Récupérer UNIQUEMENT les cartes de l'utilisateur connecté
                var cartes = cardService.getAllByUser(currentUser.getId());

                if (cartes.isEmpty()) {
                    Label noCards = new Label("Aucune carte associée");
                    noCards.setStyle("-fx-text-fill: #64748b;");
                    cardsContainer.getChildren().add(noCards);
                } else {
                    for (BankCard carte : cartes) {
                        VBox cardBox = creerVignetteCarte(carte);
                        cardsContainer.getChildren().add(cardBox);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private VBox creerVignetteCarte(BankCard carte) {
        VBox cardBox = new VBox(8);
        cardBox.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                "-fx-padding: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 5);");
        cardBox.setPrefWidth(300);

        // Type de carte
        Label typeLabel = new Label(carte.getCardType());
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Numéro de carte (masqué partiellement)
        String numero = carte.getCardNumber();
        String numeroMasque = "**** **** **** " + numero.substring(numero.length() - 4);
        Label numberLabel = new Label(numeroMasque);
        numberLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-letter-spacing: 2px;");

        // RIB (NOUVEAU)
        Label ribLabel = new Label("RIB: " + carte.getRib());
        ribLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 12px;");

        // Titulaire et expiration
        HBox infoBox = new HBox(20);
        Label holderLabel = new Label(carte.getCardHolder());
        holderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        Label expiryLabel = new Label("Exp: " + carte.getExpiryDate());
        expiryLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        infoBox.getChildren().addAll(holderLabel, expiryLabel);

        // Boutons d'action
        HBox actionsBox = new HBox(10);
        Button envoyerBtn = new Button("Envoyer");
        envoyerBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 5 10;");
        envoyerBtn.setOnAction(e -> ouvrirEnvoi(carte));

        Button detailsBtn = new Button("Détails");
        detailsBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 5 10;");
        detailsBtn.setOnAction(e -> afficherDetails(carte));

        actionsBox.getChildren().addAll(envoyerBtn, detailsBtn);

        cardBox.getChildren().addAll(typeLabel, numberLabel, ribLabel, infoBox, actionsBox);
        return cardBox;
    }

<<<<<<< HEAD

    // Dans DashboardController.java - Remplacer la méthode ouvrirEnvoi
    private void ouvrirEnvoi(BankCard carte) {
        System.out.println("📨 Demande d'envoi avec carte: " + carte.getCardNumber());

        // Utiliser MainFxml pour ouvrir directement la page d'envoi
        MainFxml.getInstance().openSendWithCard(carte);
    }

    // Modifier aussi les autres méthodes de navigation
    @FXML
    private void addCard() {
        MainFxml.getInstance().openWalletLayoutWithSection("CARDS");
    }

    @FXML
    private void goSend() {
        MainFxml.getInstance().openWalletLayoutWithSection("SEND");
    }

    @FXML
    private void goReceive() {
        MainFxml.getInstance().openWalletLayoutWithSection("RECEIVE");
    }

    @FXML
    private void goHistory() {
        MainFxml.getInstance().openWalletLayoutWithSection("HISTORY");
    }

    @FXML
    private void createWallet() {
        MainFxml.getInstance().openWalletLayoutWithSection("DASHBOARD");
    }

    // Méthode utilitaire pour trouver le WalletLayoutController
    private WalletLayoutController findWalletController(javafx.scene.Parent root) {
        // Vérifier si le root lui-même est un WalletLayoutController
        Object controller = root.getProperties().get("controller");
        if (controller instanceof WalletLayoutController) {
            return (WalletLayoutController) controller;
        }

        // Parcourir les enfants si c'est un VBox, HBox, etc.
        if (root instanceof javafx.scene.layout.Pane) {
            for (javafx.scene.Node child : ((javafx.scene.layout.Pane) root).getChildren()) {
                if (child instanceof javafx.scene.Parent) {
                    WalletLayoutController found = findWalletController((javafx.scene.Parent) child);
                    if (found != null) return found;
                }
            }
        }

        return null;
    }
=======
    private void ouvrirEnvoi(BankCard carte) {
        // Ouvrir la page d'envoi avec la carte sélectionnée
        SendController.setCarteSource(carte);
        WalletLayoutController.instance.goSend();
    }

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    private void afficherDetails(BankCard carte) {
        String message = String.format(
                "Détails de la carte:\n\n" +
                        "Type: %s\n" +
                        "Titulaire: %s\n" +
                        "Numéro: %s\n" +
                        "RIB: %s\n" +
                        "Expiration: %s\n" +
                        "CVV: %s",
                carte.getCardType(),
                carte.getCardHolder(),
                carte.getCardNumber(),
                carte.getRib(),
                carte.getExpiryDate(),
                carte.getCvv()
        );
        DialogUtil.success("Détails de la carte", message);
    }

<<<<<<< HEAD



=======
    @FXML
    private void createWallet() {
        WalletLayoutController.instance.openInscription();
    }

    @FXML
    private void addCard() {
        WalletLayoutController.instance.goCards();
    }

    @FXML
    private void goSend() {
        WalletLayoutController.instance.goSend();
    }

    @FXML
    private void goReceive() {
        WalletLayoutController.instance.goReceive();
    }

    @FXML
    private void goHistory() {
        WalletLayoutController.instance.goHistory();
    }
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

    public static void refreshStatic(){
        if(instance != null){
            instance.refreshDashboard();
        }
    }
}