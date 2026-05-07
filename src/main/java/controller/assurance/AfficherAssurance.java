package controller.assurance;

import entities.User;
import entities.assurances.Assurances;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;
import utils.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AfficherAssurance {

    @FXML private Button retourhaamdi;
    @FXML private FlowPane cardAffAssurance;
    @FXML private Button haamdi;
    @FXML private ImageView imgLogoAssurance;

    private ServiceAssurances serviceAssurances = new ServiceAssurances();

    @FXML
    private void retourMain() {
        try {
            User currentUser = Session.getCurrentUser();

            if (currentUser != null && "ADMIN".equals(currentUser.getRole().name())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) retourhaamdi.getScene().getWindow();
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle("Admin Dashboard");
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilService.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) retourhaamdi.getScene().getWindow();
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle("Services");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void haamdiah() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assurance/AjouterAssurance.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) haamdi.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Insurance");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir Ajouter Assurance !");
        }
    }

    @FXML
    public void initialize() {
        loadLogo();
        loadAssurances();
    }

    private void loadLogo() {
        try {
            Image logo = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/icons/logoservices.png")));
            imgLogoAssurance.setImage(logo);
            Circle clip = new Circle(27.5, 27.5, 27.5);
            imgLogoAssurance.setClip(clip);
        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    public void loadAssurances() {
        try {
            List<Assurances> list = serviceAssurances.recupererAssurance();
            cardAffAssurance.getChildren().clear();

            for (Assurances a : list) {
                VBox card = createAssuranceCard(a);
                cardAffAssurance.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createAssuranceCard(Assurances assurance) {
        VBox card = new VBox(8);
        card.setPrefWidth(250);
        card.setStyle("""
                -fx-border-color:#e5e7eb;
                -fx-border-radius:10;
                -fx-padding:15;
                -fx-background-color:white;
                -fx-effect:dropshadow(three-pass-box, rgba(0,0,0,0.1), 10,0,0,0);
                -fx-background-radius:10;
                """);

        // Animation au survol
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-border-color:#4f46e5; -fx-border-width:2; -fx-border-radius:10; -fx-padding:15; " +
                        "-fx-background-color:white; -fx-background-radius:10; " +
                        "-fx-effect:dropshadow(three-pass-box, rgba(79,70,229,0.3), 15,0,0,0);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-border-color:#e5e7eb; -fx-border-width:1; -fx-border-radius:10; -fx-padding:15; " +
                        "-fx-background-color:white; -fx-background-radius:10; " +
                        "-fx-effect:dropshadow(three-pass-box, rgba(0,0,0,0.1), 10,0,0,0);"
        ));

        // Image par défaut selon le type
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-radius:8; -fx-background-radius:8;");

        if (assurance.getTypeAssurance().name().equals("VOITURE")) {
            imageView.setImage(new Image("https://cdn-icons-png.flaticon.com/512/3095/3095110.png", 220, 120, true, true));
        } else if (assurance.getTypeAssurance().name().equals("MAISON")) {
            imageView.setImage(new Image("https://cdn-icons-png.flaticon.com/512/2575/2575044.png", 220, 120, true, true));
        } else {
            imageView.setImage(new Image("https://cdn-icons-png.flaticon.com/512/1149/1149561.png", 220, 120, true, true));
        }

        // Informations
        Text idText = new Text("🔖 Code: " + assurance.getId());
        idText.setStyle("-fx-font-size:12px; -fx-fill:#6b7280;");

        Text nomText = new Text("📋 " + assurance.getNomAssurance());
        nomText.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:#374151;");

        Text typeText = new Text("📌 Type: " + assurance.getTypeAssurance());
        typeText.setStyle("-fx-font-size:12px; -fx-fill:#4f46e5;");

        Text prixText = new Text("💰 Prix: " + assurance.getPrix() + " DT");
        prixText.setStyle("-fx-font-size:12px; -fx-fill:#374151;");

        Text dureeText = new Text("⏱️ Durée: " + assurance.getDureeMois() + " mois");
        dureeText.setStyle("-fx-font-size:12px; -fx-fill:#374151;");

        Text statutText = new Text("📊 Statut: " + assurance.getStatut());
        String statutColor = assurance.getStatut().name().equals("ACTIVE") ? "#10b981" : "#ef4444";
        statutText.setStyle("-fx-font-size:12px; -fx-fill:" + statutColor + "; -fx-font-weight:bold;");

        // Description (tooltip)
        Tooltip.install(card, new Tooltip(assurance.getDescription()));

        // Boutons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 12; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> showModifierAssurance(assurance));

        Button btnSupprimer = new Button("🗑️ Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 12; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> {
            try {
                serviceAssurances.supprimerAssurance(assurance);
                loadAssurances();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Assurance supprimée !");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression impossible !");
            }
        });

        buttonsBox.getChildren().addAll(btnModifier, btnSupprimer);

        card.getChildren().addAll(
                imageView,
                nomText,
                idText,
                typeText,
                prixText,
                dureeText,
                statutText,
                buttonsBox
        );

        return card;
    }

    private void showModifierAssurance(Assurances a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assurance/ModifierAssurance.fxml"));
            Parent root = loader.load();
            ModifierAssurance controller = loader.getController();
            controller.setAssurance(a);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Assurance");
            stage.show();
            stage.setOnHidden(e -> loadAssurances());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}