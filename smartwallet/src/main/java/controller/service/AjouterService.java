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
    @FXML private WebView localisationservice;  // WebView pour la carte
    @FXML private TextField prixservice;
    @FXML private ComboBox<TypeService> typeserviceservice;
    @FXML private ComboBox<Statut> statutservice;
    @FXML private TextField descriptionservice;
    @FXML private TextField imagajt;
    @FXML private Button btnAjouterserrr;
    @FXML private Label coordLabel;  // Pour afficher "Latitude: 36,8065 | Longitude: 10,1815"
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;

    private Double selectedLatitude = 36.8065;
    private Double selectedLongitude = 10.1815;
    private File selectedFile;
    private static final String IMAGE_DIRECTORY = "D:\\imageprojet";
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        System.out.println("=== Initialisation AjouterService ===");

        // Initialiser les ComboBox
        if (typeserviceservice != null) {
            typeserviceservice.getItems().setAll(TypeService.values());
            // Sélectionner "voiture" par défaut si disponible
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
            // Sélectionner "DISPONIBLE" par défaut
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
                Circle clip = new Circle(100, 100, 100);
                imgLogoService.setClip(clip);
            } catch (Exception e) {
                System.err.println("Logo non chargé: " + e.getMessage());
            }
        }

        // Initialiser la carte
        initializeMap();

        // Mettre à jour l'affichage des coordonnées
        updateCoordDisplay();
    }

    private void initializeMap() {
        if (localisationservice == null) {
            System.err.println("ERREUR: localisationservice est null");
            return;
        }

        webEngine = localisationservice.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // Activer les fonctionnalités supplémentaires
        webEngine.setUserDataDirectory(new File("cache"));

        System.out.println("Chargement de la carte...");

        // HTML avec Leaflet - Version simplifiée et robuste
        String mapHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                }
                html, body {
                    height: 100%;
                    width: 100%;
                    overflow: hidden;
                }
                #map {
                    height: 100%;
                    width: 100%;
                    background-color: #f0f0f0;
                }
                .loading {
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    background: white;
                    padding: 10px;
                    border-radius: 5px;
                    box-shadow: 0 2px 5px rgba(0,0,0,0.2);
                    z-index: 1000;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <div class="loading" id="loading">Chargement de la carte...</div>
            <script>
                console.log("Début du script");
                
                // Fonction d'initialisation
                function initMap() {
                    try {
                        console.log("Initialisation de la carte...");
                        
                        // Vérifier que Leaflet est chargé
                        if (typeof L === 'undefined') {
                            console.error("Leaflet non chargé!");
                            document.getElementById('loading').innerHTML = "Erreur: Leaflet non chargé";
                            return;
                        }
                        
                        console.log("Leaflet chargé, version: " + L.version);
                        
                        // Créer la carte
                        var map = L.map('map').setView([36.8065, 10.1815], 13);
                        
                        // Ajouter les tuiles OpenStreetMap
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '© OpenStreetMap',
                            maxZoom: 19
                        }).addTo(map);
                        
                        console.log("Tuiles ajoutées");
                        
                        // Créer le marqueur
                        var marker = L.marker([36.8065, 10.1815], {
                            draggable: true,
                            autoPan: true
                        }).addTo(map);
                        
                        console.log("Marqueur créé");
                        
                        // Popup
                        marker.bindPopup("Position actuelle").openPopup();
                        
                        // Fonction pour envoyer les coordonnées à Java
                        function sendToJava(lat, lng) {
                            console.log("Envoi à Java: " + lat + ", " + lng);
                            if (window.javaConnector) {
                                window.javaConnector.updateCoordinates(lat, lng);
                            } else {
                                console.log("Bridge Java pas encore disponible");
                            }
                        }
                        
                        // Événements
                        marker.on('dragend', function(e) {
                            var pos = marker.getLatLng();
                            sendToJava(pos.lat, pos.lng);
                        });
                        
                        map.on('click', function(e) {
                            marker.setLatLng(e.latlng);
                            sendToJava(e.latlng.lat, e.latlng.lng);
                        });
                        
                        // Cacher le message de chargement
                        document.getElementById('loading').style.display = 'none';
                        
                        // Envoyer les coordonnées initiales
                        var pos = marker.getLatLng();
                        sendToJava(pos.lat, pos.lng);
                        
                        // Forcer le redimensionnement
                        setTimeout(function() {
                            map.invalidateSize();
                            console.log("Carte redimensionnée");
                        }, 500);
                        
                    } catch (e) {
                        console.error("Erreur: " + e.message);
                        document.getElementById('loading').innerHTML = "Erreur: " + e.message;
                    }
                }
                
                // Attendre que la page soit complètement chargée
                if (document.readyState === 'loading') {
                    document.addEventListener('DOMContentLoaded', initMap);
                } else {
                    initMap();
                }
                
                // Gérer le redimensionnement
                window.addEventListener('resize', function() {
                    if (map) {
                        setTimeout(function() { map.invalidateSize(); }, 100);
                    }
                });
                
            </script>
        </body>
        </html>
        """;

        // Charger le contenu
        webEngine.loadContent(mapHtml);

        // Gérer les événements de chargement
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("État WebView: " + newState);

            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("HTML chargé avec succès");

                // Attendre un peu que le DOM soit prêt
                Platform.runLater(() -> {
                    try {
                        // Créer le bridge Java
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaConnector", new JavaConnector());
                        System.out.println("Bridge Java créé avec succès");

                        // Vérifier que le bridge fonctionne
                        webEngine.executeScript(
                                "console.log('Bridge Java disponible: ' + (typeof javaConnector !== 'undefined'));"
                        );

                    } catch (Exception e) {
                        System.err.println("Erreur création bridge: " + e.getMessage());
                        e.printStackTrace();
                    }
                });

            } else if (newState == Worker.State.FAILED) {
                System.err.println("ÉCHEC chargement HTML");
                Throwable exception = webEngine.getLoadWorker().getException();
                if (exception != null) {
                    System.err.println("Exception: " + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    // Bridge Java-JavaScript
    public class JavaConnector {
        public void updateCoordinates(double lat, double lng) {
            Platform.runLater(() -> {
                try {
                    System.out.println("Coordonnées reçues: " + lat + ", " + lng);

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
        // Mettre à jour le label des coordonnées
        if (coordLabel != null) {
            coordLabel.setText(String.format("Latitude: %.4f | Longitude: %.4f",
                    selectedLatitude, selectedLongitude));
        }

        // Mettre à jour les champs de texte si présents
        if (latitudeField != null) {
            latitudeField.setText(String.format("%.4f", selectedLatitude));
        }
        if (longitudeField != null) {
            longitudeField.setText(String.format("%.4f", selectedLongitude));
        }
    }

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
            if (!destDir.exists()) destDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            File dest = new File(destDir, fileName);

            Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            imagajt.setText(dest.getAbsolutePath());

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Image téléchargée avec succès");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image");
        }
    }

    @FXML
    private void onButtonClicked() {
        try {
            // Validation
            if (prixservice.getText().isEmpty() || descriptionservice.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs obligatoires",
                        "Veuillez remplir tous les champs");
                return;
            }

            if (typeserviceservice.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Type service",
                        "Veuillez sélectionner un type de service");
                return;
            }

            if (statutservice.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Statut",
                        "Veuillez sélectionner un statut");
                return;
            }

            float prix;
            try {
                prix = Float.parseFloat(prixservice.getText());
                if (prix <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Prix invalide",
                            "Le prix doit être supérieur à 0");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Prix invalide",
                        "Le prix doit être un nombre valide");
                return;
            }

            // Créer le point de localisation
            GeometryFactory geometryFactory = new GeometryFactory();
            Point localisationPoint = geometryFactory.createPoint(
                    new Coordinate(selectedLongitude, selectedLatitude)
            );

            // Utilisateur (à adapter)
            User currentUser = new User();
            currentUser = Session.getCurrentUser() ;

            // Créer le service
            Services service = new Services(
                    prix,
                    localisationPoint,
                    "",
                    descriptionservice.getText().trim(),
                    typeserviceservice.getValue().toString(), // Utiliser la valeur sélectionnée
                    statutservice.getValue(),
                    typeserviceservice.getValue(),
                    currentUser,
                    imagajt.getText().trim()
            );

            // Ajouter
            ServiceServices serviceServices = new ServiceServices();
            serviceServices.ajouterServices(service);

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Service ajouté avec succès !");

            retourMain();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

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
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir AfficherService.fxml");
        }
    }

    @FXML
    private void resetForm() {
        prixservice.clear();
        descriptionservice.clear();
        imagajt.clear();

        selectedLatitude = 36.8065;
        selectedLongitude = 10.1815;

        // Remettre les valeurs par défaut des ComboBox
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

        // Recharger la carte
        initializeMap();
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