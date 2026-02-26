package controller.service;

import entities.service.Services;
import javafx.application.Platform;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Point;
import services.service.ServiceServices;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AfficherService {

    @FXML private Button retouritafser;
    @FXML private Button btnAjouterserrr;
    @FXML private HBox cardaffserv;
    @FXML private ImageView imgLogoList;

    @FXML
    public void initialize() {

        // ✅ Logo
        try {
            if (imgLogoList != null) {
                Image image = new Image(
                        getClass().getResourceAsStream("/icons/logoservices.png")
                );
                imgLogoList.setImage(image);
                imgLogoList.setClip(new Circle(40, 40, 40));
            }
        } catch (Exception e) {
            System.err.println("Logo non chargé: " + e.getMessage());
        }

        loadServices();
    }

    // ======================================================
    // 🔹 Navigation
    // ======================================================

    @FXML
    private void retourMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/acceuilservices/AcceuilService.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Accueil Services");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void retourAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/services/AjouterService.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Ajouter Service");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    // 🔹 Chargement services
    // ======================================================

    @FXML
    public void loadServices() {
        ServiceServices ss = new ServiceServices();
        cardaffserv.getChildren().clear();

        try {
            List<Services> services = ss.recupererServices();

            if (services.isEmpty()) {
                Text noData = new Text("Aucun service trouvé");
                noData.setStyle("-fx-font-size:16px; -fx-padding:20px;");
                cardaffserv.getChildren().add(noData);
                return;
            }

            for (Services s : services) {
                cardaffserv.getChildren().add(createServiceCard(s));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Impossible de récupérer les services !");
        }
    }

    // ======================================================
    // 🔹 Card Service
    // ======================================================

    private VBox createServiceCard(Services service) {

        VBox card = new VBox(8);
        card.setPrefWidth(250);
        card.setStyle("""
                -fx-border-color:#ccc;
                -fx-border-radius:5;
                -fx-padding:12;
                -fx-background-color:white;
                -fx-effect:dropshadow(three-pass-box, rgba(0,0,0,0.1), 5,0,0,0);
                """);

        // ================= IMAGE + OVERLAY =================
        ImageView imageView = new ImageView();
        imageView.setFitWidth(226);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        try {
            if (service.getImage() != null &&
                    !service.getImage().isEmpty() &&
                    new File(service.getImage()).exists()) {

                imageView.setImage(
                        new Image(new File(service.getImage()).toURI().toString())
                );
            } else {
                setDefaultImage(imageView);
            }
        } catch (Exception e) {
            setDefaultImage(imageView);
        }

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(imageView);

        // ✅ Overlay si NON_DISPONIBLE
        if ("non_disponible".equalsIgnoreCase(service.getStatutString())) {
            Label loueeLabel = new Label(
                    "Cette "+ service.getTypeServiceString()+ " a été louée\nDurée: " + service.getDuree() + " jour(s)"
            );
            loueeLabel.setStyle("""
                    -fx-background-color: rgba(231,76,60,0.85);
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-padding: 6 10 6 10;
                    -fx-background-radius: 5;
                    -fx-alignment: center;
                    """);
            StackPane.setAlignment(loueeLabel, Pos.TOP_CENTER);
            imageContainer.getChildren().add(loueeLabel);
        }

        // ================= TEXT =================
        Text typeText = new Text("Type: " + service.getType());
        Text statutText = new Text("Statut: " + service.getStatutString());
        Text typeServiceText = new Text("Catégorie: " + service.getTypeServiceString());

        // ================= LOCALISATION =================
        double lat = 36.8065;
        double lng = 10.1815;

        String localisationStr = "Localisation: ";
        if (service.getLocalisation() != null) {
            Point p = service.getLocalisation();
            lat = p.getY();
            lng = p.getX();
            localisationStr += String.format("%.4f, %.4f", lat, lng);
        } else {
            localisationStr += "Non définie";
        }
        Text localisationText = new Text(localisationStr);

        // ================= PRIX =================
        Text prixText = new Text("Prix: " + service.getPrix() + " DT");

        final double latValue = lat;
        final double lngValue = lng;

        // ================= MINI MAP =================
        ImageView mapImage = new ImageView();
        mapImage.setFitWidth(250);
        mapImage.setFitHeight(180);
        mapImage.setPreserveRatio(true);

        try {
            String mapUrl =
                    "https://static-maps.yandex.ru/1.x/?lang=fr_FR&ll="
                            + lngValue + "," + latValue
                            + "&z=13&l=map&size=250,180&pt="
                            + lngValue + "," + latValue + ",pm2rdm";

            mapImage.setImage(new Image(mapUrl, true));

        } catch (Exception e) {
            mapImage.setStyle("-fx-background-color:#bdc3c7;");
        }

        // ✅ CLIC → OPENSTREETMAP
        mapImage.setOnMouseClicked(e -> openInOSM(latValue, lngValue));

        Label coordLabel = new Label(
                String.format("%.4f, %.4f", latValue, lngValue)
        );

        // ================= BUTTONS =================
        HBox buttonsBox = new HBox(10);

        Button btnModifier = new Button("Modifier");
        btnModifier.setOnAction(e -> showModifierService(service));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setOnAction(e -> deleteService(service));

        buttonsBox.getChildren().addAll(btnModifier, btnSupprimer);

        card.getChildren().addAll(
                imageContainer,
                typeText,
                statutText,
                typeServiceText,
                localisationText,   // nouveau
                prixText,           // nouveau
                mapImage,
                coordLabel,
                buttonsBox
        );

        return card;
    }

    // ======================================================
    // 🔹 Helpers
    // ======================================================

    private void setDefaultImage(ImageView imageView) {
        try {
            imageView.setImage(
                    new Image(getClass().getResourceAsStream("/icons/default_service.png"))
            );
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color:#bdc3c7;");
        }
    }

    private void deleteService(Services service) {
        try {
            new ServiceServices().supprimerServices(service);
            loadServices();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression impossible !");
        }
    }

    private void showModifierService(Services service) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/services/ModifierService.fxml")
            );

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

    // ======================================================
    // 🔥 OPEN STREET MAP
    // ======================================================

    private void openInOSM(double lat, double lon) {

        String url = "https://www.openstreetmap.org/?mlat="
                + lat + "&mlon=" + lon
                + "#map=18/" + lat + "/" + lon;

        HostServices hs = MyApp.getHostServicesInstance();

        if (hs != null) {
            hs.showDocument(url);
        } else {
            System.err.println("HostServices non initialisé !");
        }
    }
}