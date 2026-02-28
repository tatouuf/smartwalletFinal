package controller.assurance;

import entities.assurances.Assurances;
import entities.assurances.Statut;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;
import services.paymentservice.PayPalService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AfficherAssuranceClient {

    @FXML
    private Button retourhaamdi;

    @FXML
    private FlowPane cardAffAssurance;

    @FXML
    private ImageView imgLogoAssurance;

    @FXML
    private Button payer;

    @FXML
    private WebView paypalWebView;

    @FXML
    private Button closePayPalButton;

    private final ServiceAssurances serviceAssurances = new ServiceAssurances();
    private final PayPalService payPalService = new PayPalService();

    // Variable pour stocker l'assurance sélectionnée
    private Assurances selectedAssurance;
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        loadLogo();
        loadAssurances();

        // Initialiser le WebEngine pour PayPal
        if (paypalWebView != null) {
            webEngine = paypalWebView.getEngine();
            setupPayPalWebView();
        }
    }

    private void setupPayPalWebView() {
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED.equals(newValue)) {
                String location = webEngine.getLocation();
                System.out.println("Page chargée: " + location);

                // Détecter le retour de PayPal après paiement
                if (location.contains("success") || location.contains("return")) {
                    // Extraire l'orderId de l'URL
                    String orderId = extractOrderIdFromUrl(location);

                    if (orderId != null) {
                        // Capturer le paiement
                        boolean captured = payPalService.captureOrder(orderId);

                        if (captured) {
                            showAlert(Alert.AlertType.INFORMATION, "Succès",
                                    "Paiement effectué avec succès !");

                            // Activer l'assurance
                            if (selectedAssurance != null) {
                                activateAssuranceAfterPayment(selectedAssurance);
                            }
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur",
                                    "Le paiement a été approuvé mais n'a pas pu être capturé.");
                        }
                    }

                    hidePayPalWebView();
                    loadAssurances(); // Recharger la liste

                } else if (location.contains("cancel")) {
                    showAlert(Alert.AlertType.WARNING, "Annulation",
                            "Paiement annulé.");
                    hidePayPalWebView();
                }
            }
        });
    }

    private String extractOrderIdFromUrl(String url) {
        // Exemple d'URL: https://example.com/success?token=9HJ14005MD837982B
        if (url.contains("token=")) {
            String token = url.substring(url.indexOf("token=") + 6);
            if (token.contains("&")) {
                token = token.substring(0, token.indexOf("&"));
            }
            return token;
        }
        return null;
    }

    private void loadLogo() {
        try {
            Image logo = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/icons/logoservices.png")
            ));
            imgLogoAssurance.setImage(logo);
        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    private void loadAssurances() {
        try {
            List<Assurances> list = serviceAssurances.recupererAssurance();
            cardAffAssurance.getChildren().clear();

            for (Assurances a : list) {
                // Afficher uniquement les assurances INACTIVE
                if (a.getStatut() != Statut.INACTIVE) continue;

                VBox card = createAssuranceCard(a);
                cardAffAssurance.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les assurances !");
        }
    }

    private VBox createAssuranceCard(Assurances assurance) {
        VBox card = new VBox(10);
        card.setPrefWidth(220);
        card.setStyle("-fx-border-color: #c388b5; -fx-border-width: 2; -fx-padding:15; -fx-background-color:#f9f9f9; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Text id = new Text("Code: " + assurance.getId());
        id.setFont(Font.font("System", FontWeight.BOLD, 12));

        Text nom = new Text("Nom: " + assurance.getNomAssurance());
        nom.setFont(Font.font("System", FontWeight.NORMAL, 14));

        Text type = new Text("Type: " + assurance.getTypeAssurance());
        Text prix = new Text("Montant: " + String.format("%.2f", assurance.getPrix()) + " TND");
        prix.setStyle("-fx-fill: #0070ba; -fx-font-weight: bold;");

        Text duree = new Text("Durée: " + assurance.getDureeMois() + " mois");
        Text statut = new Text("Statut: " + assurance.getStatut());
        statut.setStyle("-fx-fill: #f44336;");

        // Boutons
        Button btnSelect = new Button("Sélectionner");
        Button btnPay = new Button("Payer PayPal");

        btnSelect.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        btnPay.setStyle("-fx-background-color: #0070ba; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        btnPay.setDisable(true); // Désactivé par défaut

        // Action Sélectionner
        btnSelect.setOnAction(e -> {
            selectedAssurance = assurance;
            btnPay.setDisable(false); // Activer le bouton PayPal
            showAlert(Alert.AlertType.INFORMATION, "Sélection",
                    "Assurance sélectionnée: " + assurance.getNomAssurance());
        });

        // Action Payer avec PayPal
        btnPay.setOnAction(e -> handlePayPalPayment(assurance));

        HBox buttonBox = new HBox(10, btnSelect, btnPay);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(id, nom, type, prix, duree, statut, buttonBox);

        return card;
    }

    @FXML
    public void payer() {
        if (selectedAssurance == null) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "Veuillez d'abord sélectionner une assurance !");
            return;
        }
        handlePayPalPayment(selectedAssurance);
    }

    private void handlePayPalPayment(Assurances assurance) {
        try {
            // Vérifier que le prix est bien un nombre
            double prix = assurance.getPrix();
            System.out.println("Prix de l'assurance: " + prix);

            // Demander confirmation
            Optional<ButtonType> result = showConfirmation(
                    "Confirmation de paiement",
                    "Vous allez payer " + String.format("%.2f", prix) + " TND pour l'assurance " +
                            assurance.getNomAssurance() + "\n\nVoulez-vous continuer ?"
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Créer l'ordre PayPal - utiliser USD car PayPal Sandbox ne supporte pas TND
                String approvalLink = payPalService.createOrder(
                        prix,
                        "USD", // Important: utiliser USD pour le sandbox
                        assurance.getNomAssurance() + " - " + assurance.getTypeAssurance()
                );

                // Afficher le WebView et charger PayPal
                showPayPalWebView(approvalLink);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur PayPal",
                    "Erreur de connexion à PayPal : " + e.getMessage());
        }
    }

    private void showPayPalWebView(String url) {
        if (paypalWebView != null && webEngine != null) {
            paypalWebView.setVisible(true);
            paypalWebView.toFront();

            if (closePayPalButton != null) {
                closePayPalButton.setVisible(true);
                closePayPalButton.toFront();
            }

            webEngine.load(url);
        } else {
            // Fallback: ouvrir dans le navigateur par défaut
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void hidePayPalWebView() {
        if (paypalWebView != null) {
            paypalWebView.setVisible(false);
        }
        if (closePayPalButton != null) {
            closePayPalButton.setVisible(false);
        }

        // Recharger les assurances pour montrer les changements
        loadAssurances();
    }

    private void activateAssuranceAfterPayment(Assurances assurance) {
        try {
            assurance.setStatut(Statut.ACTIVE);
            serviceAssurances.modifierStatutAssurance(assurance);

            // Recharger la liste pour mettre à jour l'affichage
            loadAssurances();

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Assurance activée avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "L'assurance a été payée mais n'a pas pu être activée automatiquement.");
        }
    }

    @FXML
    public void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourhaamdi.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil Services");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de retourner au menu principal !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}