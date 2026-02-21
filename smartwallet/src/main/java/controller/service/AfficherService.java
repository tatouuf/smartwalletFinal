package controller.service;

import entities.service.Services;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Point;
import services.service.ServiceServices;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AfficherService {

    @FXML private Button retouritafser;
    @FXML private HBox cardaffserv;
    @FXML private ImageView imgLogoList;
    @FXML private Button btnAjouterserrr;

    // ================= NAVIGATION =================

    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainALC/MainALC.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retouritafser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de retourner au menu principal !");
        }
    }

    @FXML
    private void retourAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/services/AjouterService.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retouritafser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Service");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir Ajouter Service !");
        }
    }

    // ================= INITIALISATION =================

    @FXML
    public void initialize() {
        if (imgLogoList != null) {
            try {
                Image image = new Image(getClass().getResourceAsStream("/icons/logoservices.png"));
                imgLogoList.setImage(image);
                Circle clip = new Circle(40, 40, 40);
                imgLogoList.setClip(clip);
            } catch (Exception e) {
                System.err.println("Logo non chargé: " + e.getMessage());
            }
        }

        loadServices();
    }

    // ================= MINI MAP LOCAL =================

    private WebView createMiniMap(double lat, double lng,
                                  int serviceId, String type, String statut) {

        WebView webView = new WebView();
        webView.setPrefSize(250, 180);
        webView.setMinSize(250, 180);
        webView.setMaxSize(250, 180);

        WebEngine engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);

        // Chemins locaux Leaflet
        String leafletJs = getClass().getResource("/leaflet/leaflet.js").toExternalForm();
        String leafletCss = getClass().getResource("/leaflet/leaflet.css").toExternalForm();

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <link rel="stylesheet" href="%s"/>
                <style>
                    html, body { margin:0; padding:0; height:100%%; width:100%%; overflow:hidden; }
                    #map { height:100%%; width:100%%; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script src="%s"></script>
                <script>
                    var map = L.map('map', {scrollWheelZoom:false}).setView([%f, %f], 13);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap',
                        maxZoom: 19
                    }).addTo(map);
                    var marker = L.marker([%f, %f]).addTo(map);
                    marker.bindPopup('<b>Service #%d</b><br>Type: %s<br>Statut: %s').openPopup();
                    setTimeout(function(){ map.invalidateSize(); }, 100);
                </script>
            </body>
            </html>
            """,
                leafletCss,
                leafletJs,
                lat, lng,
                lat, lng,
                serviceId, type, statut
        );

        engine.loadContent(html);
        return webView;
    }

    // ================= LOAD SERVICES =================

    @FXML
    public void loadServices() {
        ServiceServices ss = new ServiceServices();

        try {
            List<Services> services = ss.recupererServices();
            cardaffserv.getChildren().clear();

            if (services.isEmpty()) {
                Text noData = new Text("Aucun service trouvé");
                noData.setStyle("-fx-font-size:16px; -fx-padding:20px;");
                cardaffserv.getChildren().add(noData);
                return;
            }

            for (Services s : services) {
                VBox card = createServiceCard(s);
                cardaffserv.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de récupérer les services !");
        }
    }

    // ================= CARD =================

    private VBox createServiceCard(Services service) {

        VBox card = new VBox(8);
        card.setPrefWidth(250);
        card.setStyle(
                "-fx-border-color:#ccc; -fx-border-radius:5; -fx-padding:12;" +
                        "-fx-background-color:white;" +
                        "-fx-effect:dropshadow(three-pass-box, rgba(0,0,0,0.1), 5,0,0,0);"
        );

        // IMAGE
        ImageView imageView = new ImageView();
        imageView.setFitWidth(226);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        String imagePath = service.getImage();
        if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
            imageView.setImage(new Image(new File(imagePath).toURI().toString()));
        } else {
            setDefaultImage(imageView);
        }

        Text typeText = new Text("Type: " + service.getType());
        Text statutText = new Text("Statut: " + service.getStatutString());
        Text typeServiceText = new Text("Catégorie: " + service.getTypeServiceString());

        // COORDONNÉES DEPUIS BD
        double lat = 36.8065;
        double lng = 10.1815;

        if (service.getLocalisation() != null) {
            Point p = service.getLocalisation();
            lat = p.getY();
            lng = p.getX();
        }

        WebView miniMap = createMiniMap(
                lat,
                lng,
                service.getId(),
                service.getType(),
                service.getStatutString()
        );

        Label coordLabel = new Label(String.format("%.4f, %.4f", lat, lng));

        // BOUTONS
        HBox buttonsBox = new HBox(10);

        Button btnModifier = new Button("Modifier");
        btnModifier.setOnAction(e -> showModifierService(service));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setOnAction(e -> deleteService(service));

        buttonsBox.getChildren().addAll(btnModifier, btnSupprimer);

        card.getChildren().addAll(
                imageView,
                typeText,
                statutText,
                typeServiceText,
                miniMap,
                coordLabel,
                buttonsBox
        );

        return card;
    }

    // ================= UTIL =================

    private void setDefaultImage(ImageView imageView) {
        try {
            Image defaultImage =
                    new Image(getClass().getResourceAsStream("/icons/default_service.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color:#bdc3c7;");
        }
    }

    private void deleteService(Services service) {
        try {
            new ServiceServices().supprimerServices(service);
            loadServices();
            showAlert(Alert.AlertType.INFORMATION,
                    "Succès", "Service supprimé !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur", "Suppression impossible !");
        }
    }

    private void showModifierService(Services service) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/services/ModifierService.fxml"));
            Parent root = loader.load();

            ModifierService controller = loader.getController();
            controller.setService(service);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Service");
            stage.show();

            stage.setOnHidden(e -> loadServices());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}