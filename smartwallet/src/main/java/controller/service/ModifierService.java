package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import services.service.ServiceServices;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ModifierService {

    @FXML private TextField txtPrix;
    @FXML private ComboBox<TypeService> txtTypeServiceCategory;
    @FXML private TextField txtType;
    @FXML private ComboBox<Statut> txtStatut;
    @FXML private TextField txtLocalisation;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtImage;
    @FXML private Button btnSave;
    @FXML private Button btnBrowseImage;
    @FXML private WebView localisationservice;
    @FXML private Label coordLabel;

    private Services currentService;
    private WebEngine webEngine;
    private Double selectedLatitude = 36.8065;
    private Double selectedLongitude = 10.1815;
    private File selectedFile;
    private static final String IMAGE_DIRECTORY = "D:\\imageprojet";
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @FXML
    public void initialize() {
        txtTypeServiceCategory.getItems().setAll(TypeService.values());
        txtStatut.getItems().setAll(Statut.values());

        initializeMap();
        updateCoordDisplay();
    }

    public void setService(Services s) {
        if (s == null) return;
        this.currentService = s;

        txtPrix.setText(String.valueOf(s.getPrix()));
        txtType.setText(s.getType());

        txtImage.setText(s.getImage());
        txtTypeServiceCategory.setValue(s.getTypeService());
        txtStatut.setValue(s.getStatut());

        if (s.getLocalisation() != null) {
            selectedLongitude = s.getLocalisation().getX();
            selectedLatitude = s.getLocalisation().getY();
            txtLocalisation.setText(String.format("%.6f,%.6f", selectedLatitude, selectedLongitude));
        }

        updateCoordDisplay();
    }

    // -------------------- MAP --------------------
    private void initializeMap() {
        if (localisationservice == null) {
            System.err.println("ERREUR: localisationservice est null");
            return;
        }

        webEngine = localisationservice.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserDataDirectory(new File("cache"));

        String mapHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style>
                    html, body { height: 100%; margin: 0; }
                    #map { height: 100%; width: 100%; }
                    .loading {
                        position: absolute; top: 50%; left: 50%;
                        transform: translate(-50%, -50%);
                        background: white; padding: 10px;
                        border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.2);
                        z-index: 1000;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <div class="loading" id="loading">Chargement de la carte...</div>
                <script>
                    let map;
                    let marker;
                    let initialLat = 36.8065;
                    let initialLng = 10.1815;

                    function initMap() {
                        map = L.map('map').setView([initialLat, initialLng], 13);
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '© OpenStreetMap', maxZoom: 19
                        }).addTo(map);

                        marker = L.marker([initialLat, initialLng], { draggable: true }).addTo(map);
                        marker.bindPopup("Position actuelle").openPopup();

                        function sendToJava(lat, lng) {
                            if (window.javaConnector) window.javaConnector.updateCoordinates(lat, lng);
                        }

                        marker.on('dragend', function(e) {
                            let pos = marker.getLatLng();
                            sendToJava(pos.lat, pos.lng);
                        });

                        map.on('click', function(e) {
                            marker.setLatLng(e.latlng);
                            sendToJava(e.latlng.lat, e.latlng.lng);
                        });

                        document.getElementById('loading').style.display = 'none';
                        sendToJava(marker.getLatLng().lat, marker.getLatLng().lng);
                        setTimeout(() => map.invalidateSize(), 500);
                    }

                    if (document.readyState === 'loading') {
                        document.addEventListener('DOMContentLoaded', initMap);
                    } else {
                        initMap();
                    }

                    window.addEventListener('resize', () => {
                        if (map) setTimeout(() => map.invalidateSize(), 100);
                    });
                </script>
            </body>
            </html>
        """;

        webEngine.loadContent(mapHtml);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Platform.runLater(() -> {
                    try {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        JavaConnector connector = new JavaConnector();
                        window.setMember("javaConnector", connector);

                        // Transmettre les coordonnées du service au HTML avant initMap
                        if (currentService != null && currentService.getLocalisation() != null) {
                            selectedLatitude = currentService.getLocalisation().getY();
                            selectedLongitude = currentService.getLocalisation().getX();
                        }

                        System.out.println("Bridge Java créé avec succès");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else if (newState == Worker.State.FAILED) {
                System.err.println("ÉCHEC chargement HTML");
                Throwable ex = webEngine.getLoadWorker().getException();
                if (ex != null) ex.printStackTrace();
            }
        });
    }

    // -------------------- BRIDGE JAVA --------------------
    public class JavaConnector {
        public void updateCoordinates(double lat, double lng) {
            Platform.runLater(() -> {
                selectedLatitude = lat;
                selectedLongitude = lng;
                updateCoordDisplay();
            });
        }
    }

    private void updateCoordDisplay() {
        if (coordLabel != null)
            coordLabel.setText(String.format("Latitude: %.6f | Longitude: %.6f", selectedLatitude, selectedLongitude));
        if (txtLocalisation != null)
            txtLocalisation.setText(String.format("%.6f,%.6f", selectedLatitude, selectedLongitude));
    }

    // -------------------- IMAGE --------------------
    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) btnBrowseImage.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                File destDir = new File(IMAGE_DIRECTORY);
                if (!destDir.exists()) destDir.mkdirs();
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File dest = new File(destDir, fileName);
                Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                txtImage.setText(dest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // -------------------- ENREGISTRER MODIFICATIONS --------------------
    @FXML
    private void enregistrerModifications() {
        try {
            if (currentService == null) return;

            float prix = Float.parseFloat(txtPrix.getText());
            Point loc = geometryFactory.createPoint(new Coordinate(selectedLongitude, selectedLatitude));

            currentService.setPrix(prix);
            currentService.setType(txtType.getText());
            currentService.setLocalisation(loc);
            currentService.setAdresse(txtAdresse.getText());
            currentService.setImage(txtImage.getText());
            currentService.setTypeService(txtTypeServiceCategory.getValue());
            currentService.setStatut(txtStatut.getValue());

            ServiceServices serviceServices = new ServiceServices();
            serviceServices.modifierServices(currentService);

            new Alert(Alert.AlertType.INFORMATION, "Service modifié avec succès").showAndWait();
            ((Stage) btnSave.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).showAndWait();
        }
    }

    // -------------------- RETOUR --------------------
    @FXML
    private void retourMain() {
        try {
            URL fxmlURL = getClass().getResource("/acceuilservice/AcceuilService.fxml");
            if (fxmlURL == null) {
                System.err.println("AcceuilService.fxml introuvable !");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Accueil Services");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}