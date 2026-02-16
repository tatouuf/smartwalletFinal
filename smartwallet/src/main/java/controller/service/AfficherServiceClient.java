package controller.service;

import entities.service.Services;
import entities.service.Statut;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.service.ServiceServices;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AfficherServiceClient {

    @FXML
    private Button retouritafser;

    @FXML
    private HBox cardaffserv;

    @FXML
    private ImageView imgLogoList;

    private final ServiceServices ss = new ServiceServices();

    // ================= RETOUR =================
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retouritafser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load main screen!");
        }
    }

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        if (imgLogoList != null) {
            imgLogoList.setImage(new Image(getClass().getResourceAsStream("/icons/logoservices.png")));
            imgLogoList.setClip(new Circle(40, 40, 40));
        }
        loadAvailableServices();
    }

    // ================= AFFICHER SERVICES DISPONIBLES =================
    private void loadAvailableServices() {
        try {
            List<Services> services = ss.recupererServices();
            cardaffserv.getChildren().clear();

            for (Services s : services) {
                // Affiche uniquement les services DISPONIBLES
                if (s.getStatut() != Statut.DISPONIBLE) continue;

                VBox card = createServiceCard(s);
                cardaffserv.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load services!");
        }
    }

    // ================= CREATE SERVICE CARD =================
    private VBox createServiceCard(Services s) {
        VBox card = new VBox();
        card.setPrefWidth(250);
        card.setSpacing(8);
        card.setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12;" +
                        "-fx-background-color: white;"
        );

        // ===== IMAGE =====
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        if (s.getImage() != null && !s.getImage().isBlank()) {
            File file = new File(s.getImage());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }

        // ===== TEXT INFOS =====
        Text id = new Text("Code: " + s.getId());
        Text prix = new Text("Price: " + s.getPrix());
        Text type = new Text("Type: " + s.getType());
        Text statutText = new Text("Status: " + s.getStatutString());
        Text localisation = new Text("Location: " + s.getLocalisation());
        Text adresse = new Text("Address: " + s.getAdresse());
        Text typeService = new Text("Service Type: " + s.getTypeServiceString());

        // ===== BUTTONS =====
        Button btnAdd = new Button("Add");
        btnAdd.setStyle(
                "-fx-background-color: #ff96db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );

        // Action Add : change statut et recharge la liste
        btnAdd.setOnAction(event -> {
            try {
                s.setStatut(Statut.NON_DISPONIBLE);
                ss.modifierServiceStatut(s);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Service added successfully!");
                loadAvailableServices(); // Recharge la liste pour enlever le service ajouté
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add service!");
            }
        });

        HBox buttonBox = new HBox(10, btnAdd);
        card.getChildren().addAll(
                imageView, id, prix, type, statutText, localisation, adresse, typeService, buttonBox
        );

        return card;
    }

    // ================= ALERT =================
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
