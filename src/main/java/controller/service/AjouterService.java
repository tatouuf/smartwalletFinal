package controller.service;

import entities.User;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import services.service.ServiceServices;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AjouterService {

    @FXML private ImageView imgLogoService;
    @FXML private WebView localisationservice;
    @FXML private TextField prixservice;
    @FXML private ComboBox<TypeService> typeserviceservice;
    @FXML private ComboBox<Statut> statutservice;
    @FXML private TextField descriptionservice;
    @FXML private TextField imagajt;
    @FXML private Button btnAjouterserrr;
    @FXML private Label coordLabel;
    @FXML private Label prixPreview;
    @FXML private Label coordPreview;
    @FXML private Label statusPreview;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;

    private Double selectedLatitude = 36.8065;
    private Double selectedLongitude = 10.1815;
    private File selectedFile;
    private static final String IMAGE_DIRECTORY = System.getProperty("user.home") + "/Desktop/";
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation AjouterService ===");

        // Initialiser les ComboBox
        if (typeserviceservice != null) {
            typeserviceservice.getItems().setAll(TypeService.values());
            for (TypeService type : TypeService.values()) {
                if (type.name().toLowerCase().contains("voiture")) {
                    typeserviceservice.setValue(type);
                    break;
                }
            }
            if (typeserviceservice.getValue() == null && !typeserviceservice.getItems().isEmpty()) {
                typeserviceservice.setValue(typeserviceservice.getItems().get(0));
            }
        }

        if (statutservice != null) {
            statutservice.getItems().setAll(Statut.values());
            for (Statut statut : Statut.values()) {
                if (statut.name().equals("DISPONIBLE")) {
                    statutservice.setValue(statut);
                    break;
                }
            }
            if (statutservice.getValue() == null && !statutservice.getItems().isEmpty()) {
                statutservice.setValue(statutservice.getItems().get(0));
            }
        }

        // Logo
        if (imgLogoService != null) {
            try {
                Image image = new Image(getClass().getResourceAsStream("/icons/logoservices.png"));
                imgLogoService.setImage(image);
                Circle clip = new Circle(25, 25, 25);
                imgLogoService.setClip(clip);
            } catch (Exception e) {
                System.err.println("Logo non chargé: " + e.getMessage());
            }
        }

        // Initialiser la carte
        initializeMap();

        // Mettre à jour l'affichage des coordonnées
        updateCoordDisplay();

        // Mettre à jour les aperçus
        updatePrixPreview();
        updateStatusPreview();

        // Ajouter un listener pour le champ prix
        if (prixservice != null) {
            prixservice.textProperty().addListener((obs, oldVal, newVal) -> updatePrixPreview());
        }
    }

    // ======================================================
    // 🔹 MÉTHODES D'APERÇU
    // ======================================================

    @FXML
    private void updatePrixPreview() {
        if (prixPreview != null && prixservice != null) {
            String prixText = prixservice.getText();
            if (prixText.isEmpty()) {
                prixPreview.setText("0 DT");
            } else {
                try {
                    float prix = Float.parseFloat(prixText);
                    prixPreview.setText(String.format("%.2f DT", prix));
                } catch (NumberFormatException e) {
                    prixPreview.setText("0 DT");
                }
            }
        }
    }

    @FXML
    private void updateStatusPreview() {
        if (statusPreview != null && statutservice != null && statutservice.getValue() != null) {
            String statusText = statutservice.getValue().name();
            if (statusText.equals("DISPONIBLE")) {
                statusPreview.setText("DISPONIBLE");
            } else {
                statusPreview.setText("NON DISPONIBLE");
            }
        }
    }

    // ======================================================
    // 🔹 CARTE INTERACTIVE
    // ======================================================

    private void initializeMap() {
        if (localisationservice == null) {
            System.err.println("ERREUR: localisationservice est null");
            return;
        }

        webEngine = localisationservice.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserDataDirectory(new File("cache"));

        System.out.println("Chargement de la carte...");

        String mapHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            <style>
                * { margin: 0; padding: 0; }
                html, body { height: 100%; width: 100%; overflow: hidden; }
                #map { height: 100%; width: 100%; background-color: #f0f0f0; }
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
                var map;
                var marker;
                
                function initMap() {
                    try {
                        if (typeof L === 'undefined') {
                            document.getElementById('loading').innerHTML = "Erreur: Leaflet non chargé";
                            return;
                        }
                        
                        map = L.map('map').setView([36.8065, 10.1815], 13);
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '© OpenStreetMap',
                            maxZoom: 19
                        }).addTo(map);
                        
                        marker = L.marker([36.8065, 10.1815], {
                            draggable: true,
                            autoPan: true
                        }).addTo(map);
                        marker.bindPopup("Position actuelle").openPopup();
                        
                        function sendToJava(lat, lng) {
                            if (window.javaConnector) {
                                window.javaConnector.updateCoordinates(lat, lng);
                            }
                        }
                        
                        marker.on('dragend', function(e) {
                            var pos = marker.getLatLng();
                            sendToJava(pos.lat, pos.lng);
                        });
                        
                        map.on('click', function(e) {
                            marker.setLatLng(e.latlng);
                            sendToJava(e.latlng.lat, e.latlng.lng);
                        });
                        
                        document.getElementById('loading').style.display = 'none';
                        sendToJava(36.8065, 10.1815);
                        setTimeout(function() { map.invalidateSize(); }, 500);
                        
                    } catch (e) {
                        document.getElementById('loading').innerHTML = "Erreur: " + e.message;
                    }
                }
                
                if (document.readyState === 'loading') {
                    document.addEventListener('DOMContentLoaded', initMap);
                } else {
                    initMap();
                }
                
                window.addEventListener('resize', function() {
                    if (map) setTimeout(function() { map.invalidateSize(); }, 100);
                });
            </script>
        </body>
        </html>
        """;

        webEngine.loadContent(mapHtml);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("État WebView: " + newState);

            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("HTML chargé avec succès");
                Platform.runLater(() -> {
                    try {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaConnector", new JavaConnector());
                        System.out.println("Bridge Java créé avec succès");
                    } catch (Exception e) {
                        System.err.println("Erreur création bridge: " + e.getMessage());
                    }
                });
            } else if (newState == Worker.State.FAILED) {
                System.err.println("ÉCHEC chargement HTML");
            }
        });
    }

    public class JavaConnector {
        public void updateCoordinates(double lat, double lng) {
            Platform.runLater(() -> {
                try {
                    System.out.println("📍 Coordonnées reçues: " + lat + ", " + lng);
                    selectedLatitude = lat;
                    selectedLongitude = lng;
                    updateCoordDisplay();
                } catch (Exception e) {
                    System.err.println("Erreur updateCoordinates: " + e.getMessage());
                }
            });
        }
    }

    private void updateCoordDisplay() {
        if (coordLabel != null) {
            coordLabel.setText(String.format("Latitude: %.6f | Longitude: %.6f",
                    selectedLatitude, selectedLongitude));
        }
        if (coordPreview != null) {
            coordPreview.setText(String.format("%.6f, %.6f", selectedLatitude, selectedLongitude));
        }
        if (latitudeField != null) {
            latitudeField.setText(String.format("%.6f", selectedLatitude));
        }
        if (longitudeField != null) {
            longitudeField.setText(String.format("%.6f", selectedLongitude));
        }
        System.out.println("✅ Coordonnées mises à jour: " + selectedLatitude + ", " + selectedLongitude);
    }

    // ======================================================
    // 🔹 IMAGE
    // ======================================================

    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) imagajt.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) return;

        try {
            File destDir = new File(IMAGE_DIRECTORY);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            String fileName = selectedFile.getName();
            File dest = new File(destDir, fileName);
            Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            imagajt.setText(dest.getAbsolutePath());

            System.out.println("✅ Image copiée: " + dest.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Image téléchargée avec succès");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image: " + e.getMessage());
        }
    }

    // ======================================================
    // 🔹 AJOUTER SERVICE
    // ======================================================

    @FXML
    private void onButtonClicked() {
        try {
            // Validation
            if (prixservice.getText().isEmpty() || descriptionservice.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs");
                return;
            }

            if (typeserviceservice.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Type service", "Veuillez sélectionner un type de service");
                return;
            }

            if (statutservice.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Statut", "Veuillez sélectionner un statut");
                return;
            }

            if (selectedLatitude == null || selectedLongitude == null) {
                showAlert(Alert.AlertType.WARNING, "Localisation", "Veuillez sélectionner un point sur la carte");
                return;
            }

            float prix;
            try {
                prix = Float.parseFloat(prixservice.getText());
                if (prix <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix doit être supérieur à 0");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix doit être un nombre valide");
                return;
            }

            System.out.println("📍 Sauvegarde des coordonnées: " + selectedLatitude + ", " + selectedLongitude);

            GeometryFactory geometryFactory = new GeometryFactory();
            Point localisationPoint = geometryFactory.createPoint(
                    new Coordinate(selectedLongitude, selectedLatitude)
            );

            User currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de session", "Utilisateur non connecté");
                return;
            }

            Services service = new Services(
                    prix,
                    localisationPoint,
                    descriptionservice.getText().trim(),
                    descriptionservice.getText().trim(),
                    typeserviceservice.getValue().toString(),
                    statutservice.getValue(),
                    typeserviceservice.getValue(),
                    currentUser,
                    imagajt.getText().trim()
            );

            ServiceServices serviceServices = new ServiceServices();
            serviceServices.ajouterServices(service);

            System.out.println("✅ Service ajouté avec succès - ID: " + service.getId());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté avec succès !");
            retourMain();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    // ======================================================
    // 🔹 NAVIGATION
    // ======================================================

    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/services/AfficherService.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnAjouterserrr.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Services");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir AfficherService.fxml");
        }
    }

    @FXML
    private void resetForm() {
        prixservice.clear();
        descriptionservice.clear();
        imagajt.clear();

        selectedLatitude = 36.8065;
        selectedLongitude = 10.1815;

        if (typeserviceservice.getItems().size() > 0) {
            for (TypeService type : TypeService.values()) {
                if (type.name().toLowerCase().contains("voiture")) {
                    typeserviceservice.setValue(type);
                    break;
                }
            }
            if (typeserviceservice.getValue() == null) {
                typeserviceservice.setValue(typeserviceservice.getItems().get(0));
            }
        }

        if (statutservice.getItems().size() > 0) {
            for (Statut statut : Statut.values()) {
                if (statut.name().equals("DISPONIBLE")) {
                    statutservice.setValue(statut);
                    break;
                }
            }
            if (statutservice.getValue() == null) {
                statutservice.setValue(statutservice.getItems().get(0));
            }
        }

        updateCoordDisplay();
        updatePrixPreview();
        updateStatusPreview();
        initializeMap();

        System.out.println("🔄 Formulaire réinitialisé");
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