package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.service.TypeService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Point;
import services.service.FavoriService;
import services.service.ServiceServices;
import services.sms.PrixWatcherItaf;
import tests.MainFxml;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherService {

    @FXML private Button retouritafser;
    @FXML private Button btnAjouterserrr;
    @FXML private FlowPane cardaffserv;


    // Éléments de filtrage
    @FXML private Button filterAllBtn;
    @FXML private Button filterCarsBtn;
    @FXML private Button filterHousesBtn;
    @FXML private Button filterAvailableBtn;
    @FXML private TextField searchServiceField;
    @FXML private Label totalServicesLabel;
    @FXML private Label activeServicesLabel;
    @FXML private Label servicesRevenueLabel;

    private final ServiceServices serviceServices = new ServiceServices();
    private final FavoriService favoriService = new FavoriService();

    @FXML
    public void initialize() {


        loadServices();

        if (searchServiceField != null) {
            searchServiceField.textProperty().addListener((obs, oldVal, newVal) -> {
                filterServices(newVal);
            });
        }
    }

    // ======================================================
    // 🔹 FILTRES
    // ======================================================

    @FXML
    private void filterAll() {
        resetFilterButtons();
        if (filterAllBtn != null) {
            filterAllBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-border-color: #4f46e5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        }
        loadServices();
    }

    @FXML
    private void filterCars() {
        resetFilterButtons();
        if (filterCarsBtn != null) {
            filterCarsBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-border-color: #4f46e5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        }
        filterByType(TypeService.VOITURE);
    }

    @FXML
    private void filterHouses() {
        resetFilterButtons();
        if (filterHousesBtn != null) {
            filterHousesBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-border-color: #4f46e5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        }
        filterByType(TypeService.MAISON);
    }
    @FXML
    private void backToDashboard() {
        try {
            // Charger directement le DashboardAdmin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cardaffserv.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartWallet Admin Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au Dashboard !");
        }
    }
    @FXML
    private void filterAvailable() {
        resetFilterButtons();
        if (filterAvailableBtn != null) {
            filterAvailableBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-border-color: #4f46e5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        }
        filterByStatus(Statut.DISPONIBLE);
    }

    private void resetFilterButtons() {
        String defaultStyle = "-fx-background-color: white; -fx-text-fill: #4f46e5; -fx-border-color: #4f46e5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;";
        if (filterAllBtn != null) filterAllBtn.setStyle(defaultStyle);
        if (filterCarsBtn != null) filterCarsBtn.setStyle(defaultStyle);
        if (filterHousesBtn != null) filterHousesBtn.setStyle(defaultStyle);
        if (filterAvailableBtn != null) filterAvailableBtn.setStyle(defaultStyle);
    }

    private void filterByType(TypeService type) {
        try {
            List<Services> allServices = serviceServices.recupererServices();
            List<Services> filtered = allServices.stream()
                    .filter(s -> s.getTypeService() == type)
                    .collect(Collectors.toList());
            updateStats(filtered);
            displayServices(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de filtrer les services !");
        }
    }

    private void filterByStatus(Statut statut) {
        try {
            List<Services> allServices = serviceServices.recupererServices();
            List<Services> filtered = allServices.stream()
                    .filter(s -> s.getStatut() == statut)
                    .collect(Collectors.toList());
            updateStats(filtered);
            displayServices(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de filtrer les services !");
        }
    }

    private void filterServices(String searchText) {
        try {
            List<Services> allServices = serviceServices.recupererServices();

            if (searchText == null || searchText.isEmpty()) {
                updateStats(allServices);
                displayServices(allServices);
                return;
            }

            String lowerSearch = searchText.toLowerCase();
            List<Services> filtered = allServices.stream()
                    .filter(s ->
                            s.getType().toLowerCase().contains(lowerSearch) ||
                                    s.getDescription().toLowerCase().contains(lowerSearch) ||
                                    s.getTypeServiceString().toLowerCase().contains(lowerSearch) ||
                                    s.getAdresse().toLowerCase().contains(lowerSearch)
                    )
                    .collect(Collectors.toList());

            updateStats(filtered);
            displayServices(filtered);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStats(List<Services> services) {
        if (totalServicesLabel != null) {
            totalServicesLabel.setText(String.valueOf(services.size()));
        }

        if (activeServicesLabel != null) {
            long activeCount = services.stream()
                    .filter(s -> s.getStatut() == Statut.DISPONIBLE)
                    .count();
            activeServicesLabel.setText(String.valueOf(activeCount));
        }

        if (servicesRevenueLabel != null) {
            double revenue = services.stream()
                    .mapToDouble(Services::getPrix)
                    .sum();
            servicesRevenueLabel.setText(String.format("%.1fk", revenue / 1000));
        }
    }

    // ======================================================
    // 🔹 NAVIGATION
    // ======================================================

    @FXML
    private void retourMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/acceuilservices/AcceuilService.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Ajouter Service");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    // 🔹 CHARGEMENT DES SERVICES
    // ======================================================

    @FXML
    public void loadServices() {
        try {
            List<Services> services = serviceServices.recupererServices();
            updateStats(services);
            displayServices(services);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Impossible de récupérer les services !");
        }
    }

    private void displayServices(List<Services> services) {
        cardaffserv.getChildren().clear();

        if (services.isEmpty()) {
            Text noData = new Text("Aucun service trouvé");
            noData.setStyle("-fx-font-size:16px; -fx-padding:20px; -fx-fill: #6b7280;");
            cardaffserv.getChildren().add(noData);
            return;
        }

        for (Services s : services) {
            VBox card = createServiceCard(s);
            cardaffserv.getChildren().add(card);
        }
    }

    // ======================================================
    // 🔹 IMAGE - DEPUIS LE BUREAU
    // ======================================================

    private ImageView createServiceImage(Services service) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-radius:8; -fx-background-radius:8;");

        String imageName = service.getImage();
        boolean imageLoaded = false;

        if (imageName != null && !imageName.isEmpty()) {
            try {
                // Chemin du bureau
                String desktopPath = System.getProperty("user.home") + "/Desktop/";
                File imageFile = new File(desktopPath + imageName);

                if (imageFile.exists()) {
                    Image img = new Image(imageFile.toURI().toString(), 220, 120, true, true);
                    imageView.setImage(img);
                    imageLoaded = true;
                    System.out.println("✅ Image chargée: " + imageFile.getAbsolutePath());
                } else {
                    System.out.println("⚠️ Image non trouvée: " + imageFile.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur chargement: " + e.getMessage());
            }
        }

        // Image par défaut si pas d'image
        if (!imageLoaded) {
            setDefaultImage(imageView, service);
        }

        return imageView;
    }

    private void setDefaultImage(ImageView imageView, Services service) {
        try {
            String defaultUrl;
            if (service.getTypeService() == TypeService.VOITURE) {
                defaultUrl = "https://cdn-icons-png.flaticon.com/512/3095/3095110.png";
            } else if (service.getTypeService() == TypeService.MAISON) {
                defaultUrl = "https://cdn-icons-png.flaticon.com/512/2575/2575044.png";
            } else {
                defaultUrl = "https://cdn-icons-png.flaticon.com/512/1149/1149561.png";
            }
            Image img = new Image(defaultUrl, 220, 120, true, true);
            imageView.setImage(img);
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color: linear-gradient(to bottom, #4f46e5, #ec4899); -fx-background-radius: 8;");
        }
    }

    // ======================================================
    // 🔹 CARD SERVICE AVEC YANDEX MAPS
    // ======================================================

    private VBox createServiceCard(Services service) {

        VBox card = new VBox(8);
        card.setPrefWidth(250);
        card.setStyle("""
                -fx-border-color:#e5e7eb;
                -fx-border-radius:10;
                -fx-padding:15;
                -fx-background-color:white;
                -fx-effect:dropshadow(three-pass-box, rgba(0,0,0,0.1), 10,0,0,0);
                -fx-background-radius:10;
                """);

        // Animation au survol
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-border-color:#4f46e5; -fx-border-width:2; -fx-border-radius:10; -fx-padding:15; " +
                        "-fx-background-color:white; -fx-background-radius:10; " +
                        "-fx-effect:dropshadow(three-pass-box, rgba(79,70,229,0.3), 15,0,0,0);"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-border-color:#e5e7eb; -fx-border-width:1; -fx-border-radius:10; -fx-padding:15; " +
                        "-fx-background-color:white; -fx-background-radius:10; " +
                        "-fx-effect:dropshadow(three-pass-box, rgba(0,0,0,0.1), 10,0,0,0);"
        ));

        // ================= IMAGE =================
        ImageView imageView = createServiceImage(service);

        // ================= TEXTES =================
        Text typeText = new Text("🚗 Type: " + service.getType());
        typeText.setStyle("-fx-font-size:14px; -fx-fill:#374151;");

        Text statutText = new Text("📊 Statut: " + service.getStatutString());
        String statutColor = service.getStatut() == Statut.DISPONIBLE ? "#10b981" : "#ef4444";
        statutText.setStyle("-fx-font-size:14px; -fx-fill:" + statutColor + "; -fx-font-weight:bold;");

        Text typeServiceText = new Text("📌 Catégorie: " + service.getTypeServiceString());
        typeServiceText.setStyle("-fx-font-size:14px; -fx-fill:#4f46e5;");

        Text prixText = new Text("💰 Prix: " + service.getPrix() + " DT");
        prixText.setStyle("-fx-font-size:14px; -fx-fill:#374151; -fx-font-weight:bold;");

        Text adresseText = new Text("📍 " + service.getAdresse());
        adresseText.setStyle("-fx-font-size:12px; -fx-fill:#6b7280; -fx-wrap-text:true;");

        // ================= LOCALISATION =================
        double lat = 36.8065;
        double lng = 10.1815;

        if (service.getLocalisation() != null) {
            Point p = service.getLocalisation();
            lat = p.getY();  // latitude
            lng = p.getX();  // longitude
        }

        final double latValue = lat;
        final double lngValue = lng;

        // ================= MINI MAP - YANDEX MAPS =================
        ImageView mapImage = new ImageView();
        mapImage.setFitWidth(250);
        mapImage.setFitHeight(150);
        mapImage.setPreserveRatio(true);
        mapImage.setStyle("-fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5,0,0,0);");

        try {
            // Yandex Maps : ll=longitude,latitude
            String mapUrl = "https://static-maps.yandex.ru/1.x/?lang=fr_FR&ll="
                    + lngValue + "," + latValue
                    + "&z=13&l=map&size=250,150&pt="
                    + lngValue + "," + latValue + ",pm2rdm";

            mapImage.setImage(new Image(mapUrl, true));
            System.out.println("🗺️ Carte Yandex chargée pour service " + service.getId());

        } catch (Exception e) {
            System.err.println("❌ Erreur Yandex pour service " + service.getId());
            // Fallback OpenStreetMap
            try {
                String fallbackUrl = String.format(
                        "https://staticmap.openstreetmap.de/staticmap.php?center=%f,%f&zoom=15&size=250x150&markers=%f,%f,red",
                        latValue, lngValue, latValue, lngValue
                );
                mapImage.setImage(new Image(fallbackUrl, true));
                System.out.println("🗺️ Fallback OSM pour service " + service.getId());
            } catch (Exception ex) {
                mapImage.setStyle("-fx-background-color: linear-gradient(to bottom, #4f46e5, #ec4899); -fx-background-radius: 8;");
            }
        }

        // ✅ CLIC → OPENSTREETMAP
        mapImage.setOnMouseClicked(e -> openInOSM(latValue, lngValue));

        // ================= BOUTONS =================
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15; -fx-cursor: hand;");
        btnModifier.setOnAction(e -> showModifierService(service));

        Button btnSupprimer = new Button("🗑️ Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> deleteService(service));

        Button btnModifierPrix = new Button("💰 Modifier Prix");
        btnModifierPrix.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 15; -fx-cursor: hand;");
        btnModifierPrix.setOnAction(e -> modifierPrix(service));

        buttonsBox.getChildren().addAll(btnModifier, btnSupprimer, btnModifierPrix);

        card.getChildren().addAll(
                imageView,
                typeText,
                statutText,
                typeServiceText,
                prixText,
                adresseText,
                mapImage,
                buttonsBox
        );

        return card;
    }

    // ======================================================
    // 🔹 MODIFIER PRIX AVEC SMS
    // ======================================================

    private void modifierPrix(Services service) {
        try {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(service.getPrix()));
            dialog.setTitle("Modifier le prix");
            dialog.setHeaderText("Service: " + service.getDescription());
            dialog.setContentText("Nouveau prix (DT):");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                float ancienPrix = service.getPrix();
                float nouveauPrix = Float.parseFloat(result.get());

                if (ancienPrix == nouveauPrix) {
                    showAlert(Alert.AlertType.INFORMATION, "Info", "Le prix n'a pas changé");
                    return;
                }

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText("Changement de prix");
                confirmAlert.setContentText(String.format(
                        "Ancien prix: %.2f DT\nNouveau prix: %.2f DT\n\nVoulez-vous continuer ?",
                        ancienPrix, nouveauPrix
                ));

                Optional<ButtonType> confirmation = confirmAlert.showAndWait();

                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {

                    service.setPrix(nouveauPrix);
                    serviceServices.modifierServices(service);

                    PrixWatcherItaf watcher = new PrixWatcherItaf();
                    watcher.notifierChangementService(service, ancienPrix, nouveauPrix);

                    int nombreFavoris = favoriService.nombreFavorisPourService(service.getId());

                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            String.format(
                                    "✅ Prix modifié avec succès !\n\n" +
                                            "Ancien prix: %.2f DT\n" +
                                            "Nouveau prix: %.2f DT\n\n" +
                                            "📱 SMS envoyés à %d utilisateur(s) ayant ce service en favori.",
                                    ancienPrix, nouveauPrix, nombreFavoris
                            )
                    );

                    loadServices();
                }
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre valide !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le prix !");
        }
    }

    // ======================================================
    // 🔹 HELPERS
    // ======================================================

    private void deleteService(Services service) {
        try {
            int nombreFavoris = favoriService.nombreFavorisPourService(service.getId());

            if (nombreFavoris > 0) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Attention");
                confirmAlert.setHeaderText("Ce service est dans les favoris de " + nombreFavoris + " utilisateur(s)");
                confirmAlert.setContentText("La suppression va également retirer ce service de leurs favoris. Continuer ?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    serviceServices.supprimerServices(service);
                    loadServices();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé !");
                }
            } else {
                serviceServices.supprimerServices(service);
                loadServices();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé !");
            }

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

        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e) {
            System.err.println("Erreur ouverture navigateur: " + e.getMessage());
        }
    }
}