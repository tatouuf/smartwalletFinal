package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import services.service.ServiceServices;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ModifierService {

    @FXML private TextField txtPrix;
    @FXML private ComboBox<TypeService> txtTypeServiceCategory;
    @FXML private TextField txtType;
    @FXML private ComboBox<Statut> txtStatut;
    @FXML private TextField txtLocalisation;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtDescription;
    @FXML private TextField txtImage;
    @FXML private Button btnSave;
    @FXML private ImageView imgLogoModifier;
    @FXML private WebView mapView;

    private WebEngine webEngine;
    private Services currentService;

    private double selectedLatitude = 36.8065;
    private double selectedLongitude = 10.1815;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // ================= INIT =================
    @FXML
    public void initialize() {

        // ✅ logo safe
        try {
            imgLogoModifier.setImage(
                    new Image(getClass().getResourceAsStream("/icons/logoservices.png"))
            );
            imgLogoModifier.setClip(new Circle(100, 100, 100));
        } catch (Exception ignored) {}

        // ✅ combos
        txtTypeServiceCategory.getItems().setAll(TypeService.values());
        txtStatut.getItems().setAll(Statut.values());

        // ✅ important pour WebView
        mapView.setContextMenuEnabled(false);

        webEngine = mapView.getEngine();

        // ✅ attendre chargement complet
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaApp", new JavaApp());
                } catch (Exception e) {
                    System.out.println("JS bridge error");
                }
            }
        });

        // ✅ charger map après init
        Platform.runLater(() ->
                loadMap(selectedLatitude, selectedLongitude)
        );
    }

    // ================= SET SERVICE =================
    public void setService(Services s) {
        if (s == null) return;

        this.currentService = s;

        txtPrix.setText(String.valueOf(s.getPrix()));
        txtType.setText(s.getType());
        txtAdresse.setText(s.getAdresse());
        txtDescription.setText(s.getDescription());
        txtImage.setText(s.getImage());
        txtTypeServiceCategory.setValue(s.getTypeService());
        txtStatut.setValue(s.getStatut());

        if (s.getLocalisation() != null) {
            selectedLongitude = s.getLocalisation().getX();
            selectedLatitude = s.getLocalisation().getY();
            txtLocalisation.setText(selectedLatitude + "," + selectedLongitude);

            Platform.runLater(() ->
                    loadMap(selectedLatitude, selectedLongitude)
            );
        }
    }

    // ================= MAP =================
    private void loadMap(double lat, double lng) {

        String html = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8"/>
                    <link rel="stylesheet"
                     href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                    <style>
                        html, body { margin:0; height:100%%; }
                        #map { height:100%%; width:100%%; }
                    </style>
                </head>
                <body>
                <div id="map"></div>

                <script>
                    var map = L.map('map').setView([%f, %f], 13);

                    L.tileLayer(
                        'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                        { maxZoom:19 }
                    ).addTo(map);

                    var marker = L.marker([%f, %f], {draggable:true}).addTo(map);

                    function sendToJava(lat,lng){
                        try{
                            if(window.javaApp){
                                window.javaApp.sendCoordinates(lat,lng);
                            }
                        }catch(e){}
                    }

                    marker.on('dragend', function(e){
                        var p = e.target.getLatLng();
                        sendToJava(p.lat, p.lng);
                    });

                    map.on('click', function(e){
                        marker.setLatLng(e.latlng);
                        sendToJava(e.latlng.lat, e.latlng.lng);
                    });
                </script>
                </body>
                </html>
                """, lat, lng, lat, lng);

        webEngine.loadContent(html);
    }

    // ================= JAVA ↔ JS =================
    public class JavaApp {
        public void sendCoordinates(double lat, double lng) {
            Platform.runLater(() -> {
                selectedLatitude = lat;
                selectedLongitude = lng;
                txtLocalisation.setText(lat + "," + lng);

                // ✅ IMPORTANT : ne pas bloquer UI
                new Thread(() -> reverseGeocode(lat, lng)).start();
            });
        }
    }

    // ================= REVERSE GEOCODE =================
    private void reverseGeocode(double lat, double lng) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat="
                    + lat + "&lon=" + lng;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "JavaFXApp")
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            String address = json.optString("display_name", "");

            Platform.runLater(() -> txtAdresse.setText(address));

        } catch (Exception e) {
            System.out.println("Geocode failed");
        }
    }

    // ================= SAVE =================
    @FXML
    private void enregistrerModifications() {
        try {

            if (currentService == null) {
                throw new RuntimeException("Service null");
            }

            float prix = Float.parseFloat(txtPrix.getText());
            Point loc = parseLatLngToPoint(txtLocalisation.getText());

            currentService.setPrix(prix);
            currentService.setType(txtType.getText());
            currentService.setLocalisation(loc);
            currentService.setAdresse(txtAdresse.getText());
            currentService.setDescription(txtDescription.getText());
            currentService.setImage(txtImage.getText());
            currentService.setTypeService(txtTypeServiceCategory.getValue());
            currentService.setStatut(txtStatut.getValue());

            new ServiceServices().modifierServices(currentService);

            new Alert(Alert.AlertType.INFORMATION,
                    "Service updated successfully").showAndWait();

            ((Stage) btnSave.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Error updating service").showAndWait();
        }
    }

    // ================= PARSE =================
    private Point parseLatLngToPoint(String text) {
        try {
            String[] parts = text.trim().split("\\s*,\\s*");
            double lat = Double.parseDouble(parts[0]);
            double lng = Double.parseDouble(parts[1]);
            return geometryFactory.createPoint(new Coordinate(lng, lat));
        } catch (Exception e) {
            return null;
        }
    }
}