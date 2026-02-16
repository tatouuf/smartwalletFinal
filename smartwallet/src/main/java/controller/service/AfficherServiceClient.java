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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfficherServiceClient {

    @FXML
    private Button retouritafser;

    @FXML
    private HBox cardaffserv;

    @FXML
    private ImageView imgLogoList;

    private final ServiceServices ss = new ServiceServices();

    // Garde une trace des services ajoutés pour pouvoir annuler
    private final Map<Integer, Services> addedServices = new HashMap<>();

    // ================= RETOUR =================
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retouritafser.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
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
                // Affiche uniquement les services DISPONIBLES ou ceux ajoutés (pour pouvoir Cancel)
                if (s.getStatut() != Statut.DISPONIBLE && !addedServices.containsKey(s.getId())) continue;

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
        Button btnCancel = new Button("Cancel");

        btnAdd.setStyle(
                "-fx-background-color: #ff96db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );

        btnCancel.setStyle(
                "-fx-background-color: #888888;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );

        // Action Add : change statut, garde dans addedServices, mais ne supprime pas la carte
        btnAdd.setOnAction(event -> {
            try {
                s.setStatut(Statut.NON_DISPONIBLE);
                ss.modifierServiceStatut(s);
                addedServices.put(s.getId(), s); // marque comme ajouté
                statutText.setText("Status: " + s.getStatutString());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Service added! You can Cancel if needed.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add service!");
            }
        });

        // Action Cancel : revient à DISPONIBLE si le service avait été ajouté
        btnCancel.setOnAction(event -> {
            try {
                if (addedServices.containsKey(s.getId())) {
                    s.setStatut(Statut.DISPONIBLE);
                    ss.modifierServiceStatut(s);
                    addedServices.remove(s.getId());
                    statutText.setText("Status: " + s.getStatutString());
                    showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Service addition cancelled!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel service!");
            }
        });

        HBox buttonBox = new HBox(10, btnAdd, btnCancel);
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
