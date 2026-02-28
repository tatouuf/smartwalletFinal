package controller.service;

import controller.acceuilservice.AcceuilServiceClient;
import entities.service.Services;
import entities.service.Statut;
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
import javafx.stage.Stage;
import org.locationtech.jts.geom.Point;
import services.service.ServiceServices;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfficherServiceClient {

    @FXML private Button retouritafser;
    @FXML private HBox cardaffserv;
    @FXML private ImageView imgLogoList;

    private final ServiceServices ss = new ServiceServices();
    private final Map<Integer, Services> addedServices = new HashMap<>();

    // Variable pour stocker HostServices
    private javafx.application.HostServices hostServices;

    // Setter pour HostServices (au cas où on veut l'injecter)
    public void setHostServices(javafx.application.HostServices hostServices) {
        this.hostServices = hostServices;
        System.out.println("✅ HostServices injecté dans AfficherServiceClient");
    }

    // ================= RETOUR =================
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml")
            );
            Parent root = loader.load();

            // Passer HostServices au contrôleur suivant
            AcceuilServiceClient controller = loader.getController();
            if (controller != null && hostServices != null) {
                controller.setHostServices(hostServices);
            }

            Stage stage = (Stage) retouritafser.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'écran principal !");
        }
    }

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        try {
            if (imgLogoList != null) {
                Image img = new Image(
                        getClass().getResourceAsStream("/icons/logoservices.png")
                );
                imgLogoList.setImage(img);
                imgLogoList.setClip(new Circle(40, 40, 40));
            }
        } catch (Exception e) {
            System.out.println("Logo non trouvé");
        }

        // Initialiser HostServices depuis MyApp
        initHostServices();

        loadAvailableServices();
    }

    // ================= INIT HOST SERVICES =================
    private void initHostServices() {
        if (hostServices == null) {
            hostServices = MyApp.getHostServicesInstance();
            if (hostServices != null) {
                System.out.println("✅ HostServices récupéré depuis MyApp");
            } else {
                System.err.println("⚠️ HostServices toujours null après tentative de récupération");
            }
        }
    }

    // ================= LOAD SERVICES =================
    private void loadAvailableServices() {
        try {
            List<Services> services = ss.recupererServices();
            if (cardaffserv == null) return;
            cardaffserv.getChildren().clear();

            for (Services s : services) {
                if (s.getStatut() != Statut.DISPONIBLE && !addedServices.containsKey(s.getId()))
                    continue;

                VBox card = createServiceCard(s);
                cardaffserv.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les services !");
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

        Text confirmationText = new Text();
        confirmationText.setStyle("-fx-font-weight: bold; -fx-fill: #2c3e50;");

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
        mapImage.setStyle("-fx-cursor: hand;");

        double lat = 36.8065, lng = 10.1815;
        if (s.getLocalisation() != null) {
            Point p = s.getLocalisation();
            lat = p.getY();
            lng = p.getX();
        }
        final double latValue = lat;
        final double lngValue = lng;

        try {
            String mapUrl = "https://static-maps.yandex.ru/1.x/?lang=fr_FR&ll="
                    + lngValue + "," + latValue
                    + "&z=13&l=map&size=250,180&pt=" + lngValue + "," + latValue + ",pm2rdm";
            mapImage.setImage(new Image(mapUrl, true));
        } catch (Exception e) {
            mapImage.setStyle("-fx-background-color:#bdc3c7;");
            System.err.println("Erreur chargement carte: " + e.getMessage());
        }

        Tooltip.install(mapImage, new Tooltip(String.format("Lat: %.4f, Lng: %.4f\nCliquez pour ouvrir dans OpenStreetMap", latValue, lngValue)));

        // ===== ACTION AU CLIC SUR LA CARTE =====
        mapImage.setOnMouseClicked(e -> {
            // Réinitialiser HostServices au moment du clic
            initHostServices();

            if (hostServices != null) {
                String url = "https://www.openstreetmap.org/?mlat=" + latValue +
                        "&mlon=" + lngValue + "#map=18/" + latValue + "/" + lngValue;

                System.out.println("🌍 Ouverture OpenStreetMap: " + url);
                hostServices.showDocument(url);
            } else {
                System.err.println("❌ HostServices non disponible!");

                // Fallback: essayer d'ouvrir avec Desktop
                try {
                    java.awt.Desktop.getDesktop().browse(
                            new java.net.URI("https://www.openstreetmap.org/?mlat=" + latValue +
                                    "&mlon=" + lngValue + "#map=18/" + latValue + "/" + lngValue)
                    );
                    System.out.println("✅ Ouverture avec Desktop API");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.WARNING, "Attention",
                            "Impossible d'ouvrir la carte.\nCoordonnées: " + latValue + ", " + lngValue);
                }
            }
        });

        // ===== TEXT =====
        Text id = new Text("Code: " + s.getId());
        Text prix = new Text("Prix: " + s.getPrix() + " DT");
        Text type = new Text("Type: " + s.getType());
        Text statutText = new Text("Statut: " + s.getStatutString());
        Text localisationText = new Text("Adresse: " + (s.getAdresse() != null ? s.getAdresse() : "Non spécifiée"));
        Text typeService = new Text("Service Type: " + s.getTypeServiceString());

        // ===== BUTTONS =====
        Button btnAdd = new Button("Ajouter");
        Button btnCancel = new Button("Annuler");
        styleButtons(btnAdd, btnCancel);

        // ---------- ADD ----------
        btnAdd.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Durée du service");
            dialog.setHeaderText("Entrez la durée pour ce service (jours)");
            dialog.setContentText("Durée (jours) :");

            dialog.showAndWait().ifPresent(duree -> {
                try {
                    int dureeVal = Integer.parseInt(duree);
                    if (dureeVal <= 0) {
                        showAlert(Alert.AlertType.WARNING, "Invalide", "La durée doit être positive !");
                        return;
                    }

                    s.setDuree(dureeVal);
                    ss.modifierDureeService(s.getId(), dureeVal);
                    s.setStatut(Statut.NON_DISPONIBLE);
                    ss.modifierServiceStatut(s);

                    addedServices.put(s.getId(), s);
                    statutText.setText("Statut: " + s.getStatutString());
                    confirmationText.setText("✓ Vous avez ajouté " + s.getTypeServiceString() +
                            " avec une durée de " + dureeVal + " jours");
                    confirmationText.setStyle("-fx-font-weight: bold; -fx-fill: #27ae60;");

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté avec succès !");
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Durée invalide !");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout du service!");
                }
            });
        });

        // ---------- CANCEL ----------
        btnCancel.setOnAction(event -> {
            try {
                if (addedServices.containsKey(s.getId())) {
                    s.setDuree(0);
                    ss.modifierDureeService(s.getId(), 0);
                    s.setStatut(Statut.DISPONIBLE);
                    ss.modifierServiceStatut(s);

                    addedServices.remove(s.getId());
                    statutText.setText("Statut: " + s.getStatutString());
                    confirmationText.setText("✗ Service annulé");
                    confirmationText.setStyle("-fx-font-weight: bold; -fx-fill: #c0392b;");

                    showAlert(Alert.AlertType.INFORMATION, "Annulé", "Service annulé et durée réinitialisée !");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'annulation du service!");
            }
        });

        HBox buttonBox = new HBox(10, btnAdd, btnCancel);

        card.getChildren().addAll(
                confirmationText, imageView, id, prix, type, typeService, statutText,
                localisationText, mapImage, buttonBox
        );

        return card;
    }

    // ================= IMAGE SAFE LOAD =================
    private void loadServiceImage(ImageView imageView, String path) {
        try {
            if (path != null && !path.isBlank() && new File(path).exists()) {
                imageView.setImage(new Image(new File(path).toURI().toString()));
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/icons/default_service.png"));
                if (defaultImage != null) {
                    imageView.setImage(defaultImage);
                }
            }
        } catch (Exception e) {
            System.out.println("Image non chargée: " + path);
        }
    }

    // ================= STYLE BUTTONS =================
    private void styleButtons(Button add, Button cancel) {
        add.setStyle("-fx-background-color: #ff96db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        cancel.setStyle("-fx-background-color: #888888; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
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