package controller.service;

import entities.service.Services;
import entities.service.Statut;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Point;
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

    // 🔥 HostServices pour ouvrir OpenStreetMap
    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    // garde trace des services ajoutés
    private final Map<Integer, Services> addedServices = new HashMap<>();

    // ================= RETOUR =================
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml")
            );
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

        // sécuriser logo
        try {
            if (imgLogoList != null) {
                Image img = new Image(
                        getClass().getResourceAsStream("/icons/logoservices.png")
                );
                imgLogoList.setImage(img);
                imgLogoList.setClip(new Circle(40, 40, 40));
            }
        } catch (Exception e) {
            System.out.println("Logo not found");
        }

        loadAvailableServices();
    }

    // ================= LOAD SERVICES =================
    private void loadAvailableServices() {
        try {
            List<Services> services = ss.recupererServices();

            if (cardaffserv == null) return;

            cardaffserv.getChildren().clear();

            for (Services s : services) {

                // afficher seulement DISPONIBLE ou déjà ajouté
                if (s.getStatut() != Statut.DISPONIBLE
                        && !addedServices.containsKey(s.getId())) {
                    continue;
                }

                VBox card = createServiceCard(s);
                cardaffserv.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load services!");
        }
    }

    // ================= CREATE CARD =================
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

        // ===== IMAGE SERVICE =====
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);
        loadServiceImage(imageView, s.getImage());

        // ===== MINI MAP =====
        ImageView mapImage = new ImageView();
        mapImage.setFitWidth(250);
        mapImage.setFitHeight(180);
        mapImage.setPreserveRatio(true);

        // récupérer coordonnées par défaut
        double lat = 36.8065; // Tunis centre par défaut
        double lng = 10.1815;

        if (s.getLocalisation() != null) {
            Point p = s.getLocalisation();
            lat = p.getY();
            lng = p.getX();
        }

        final double latValue = lat;
        final double lngValue = lng;

        // charger image statique de carte (optionnel)
        try {
            String mapUrl = "https://static-maps.yandex.ru/1.x/?lang=fr_FR&ll="
                    + lngValue + "," + latValue
                    + "&z=13&l=map&size=250,180&pt="
                    + lngValue + "," + latValue + ",pm2rdm";

            mapImage.setImage(new Image(mapUrl, true));

        } catch (Exception e) {
            mapImage.setStyle("-fx-background-color:#bdc3c7;");
        }

        // tooltip coordonnées
        Tooltip.install(mapImage, new Tooltip(String.format("%.4f, %.4f", latValue, lngValue)));

        // clic → ouvrir OpenStreetMap
        mapImage.setOnMouseClicked(e -> {
            if (hostServices != null) {
                String url = "https://www.openstreetmap.org/?mlat=" + latValue +
                        "&mlon=" + lngValue +
                        "#map=18/" + latValue + "/" + lngValue;
                hostServices.showDocument(url);
            } else {
                System.err.println("HostServices non initialisé !");
            }
        });

        // ===== TEXT =====
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
        styleButtons(btnAdd, btnCancel);

        // ---------- ADD ----------
        btnAdd.setOnAction(event -> {
            try {
                s.setStatut(Statut.NON_DISPONIBLE);
                ss.modifierServiceStatut(s);

                addedServices.put(s.getId(), s);
                statutText.setText("Status: " + s.getStatutString());

                showAlert(Alert.AlertType.INFORMATION,
                        "Success",
                        "Service added! You can Cancel if needed.");

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add service!");
            }
        });

        // ---------- CANCEL ----------
        btnCancel.setOnAction(event -> {
            try {
                if (addedServices.containsKey(s.getId())) {

                    s.setStatut(Statut.DISPONIBLE);
                    ss.modifierServiceStatut(s);

                    addedServices.remove(s.getId());
                    statutText.setText("Status: " + s.getStatutString());

                    showAlert(Alert.AlertType.INFORMATION,
                            "Cancelled",
                            "Service addition cancelled!");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel service!");
            }
        });

        HBox buttonBox = new HBox(10, btnAdd, btnCancel);

        card.getChildren().addAll(
                imageView,
                id,
                prix,
                type,
                typeService,
                statutText,
                localisation,
                mapImage,
                buttonBox
        );

        return card;
    }

    // ================= IMAGE SAFE LOAD =================
    private void loadServiceImage(ImageView imageView, String path) {
        try {
            if (path != null && !path.isBlank()) {
                File file = new File(path);
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                }
            }
        } catch (Exception e) {
            System.out.println("Image not loaded: " + path);
        }
    }

    // ================= STYLE BUTTONS =================
    private void styleButtons(Button add, Button cancel) {
        add.setStyle(
                "-fx-background-color: #ff96db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );

        cancel.setStyle(
                "-fx-background-color: #888888;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );
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