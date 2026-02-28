package controller.service;

import entities.service.Services;
import entities.service.Statut;
import entities.user.User;
import javafx.application.HostServices;
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
import services.service.FavoriService;
import utils.Session;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfficherServiceClient {

    @FXML private Button retouritafser;
    @FXML private Button btnMesFavoris; // Nouveau bouton
    @FXML private HBox cardaffserv;
    @FXML private ImageView imgLogoList;

    private final ServiceServices ss = new ServiceServices();
    private final FavoriService fs = new FavoriService();
    private final Map<Integer, Services> addedServices = new HashMap<>();
    private HostServices hostServices;
    private final Map<Integer, Boolean> etatFavoris = new HashMap<>(); // Pour suivre l'état des favoris

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'écran principal !");
        }
    }

    // ================= MES FAVORIS =================
    @FXML
    private void goToMesFavoris() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/services/MesFavoris.fxml")
            );
            Parent root = loader.load();

            // Passer HostServices au contrôleur des favoris
            MesFavorisController controller = loader.getController();
            if (controller != null && hostServices != null) {
                controller.setHostServices(hostServices);
            }

            Stage stage = (Stage) btnMesFavoris.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Mes Favoris");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page des favoris");
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
        loadAvailableServices();
    }

    // ================= LOAD SERVICES =================
    private void loadAvailableServices() {
        try {
            List<Services> services = ss.recupererServices();
            if (cardaffserv == null) return;
            cardaffserv.getChildren().clear();

            // Vérifier l'état des favoris pour chaque service
            for (Services s : services) {
                try {
                    boolean estFavori = fs.estEnFavori(s.getId());
                    etatFavoris.put(s.getId(), estFavori);
                } catch (SQLException e) {
                    etatFavoris.put(s.getId(), false);
                }
            }

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
        }

        Tooltip.install(mapImage, new Tooltip(String.format("%.4f, %.4f", latValue, lngValue)));

        mapImage.setOnMouseClicked(e -> {
            if (hostServices != null) {
                String url = "https://www.openstreetmap.org/?mlat=" + latValue +
                        "&mlon=" + lngValue + "#map=18/" + latValue + "/" + lngValue;
                hostServices.showDocument(url);
            }
        });

        // ===== TEXT =====
        Text id = new Text("Code: " + s.getId());
        Text prix = new Text("Prix: " + s.getPrix() + " DT");
        Text type = new Text("Type: " + s.getType());
        Text statutText = new Text("Statut: " + s.getStatutString());
        Text localisationText = new Text("Adresse: " + (s.getAdresse() != null ? s.getAdresse() : "Non spécifiée"));
        Text typeService = new Text("Service Type: " + s.getTypeServiceString());

        // ===== BOUTON FAVORI (COEUR) =====
        Button btnFavori = new Button();
        btnFavori.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 20px;");

        // Initialiser l'état du cœur
        boolean estFavori = etatFavoris.getOrDefault(s.getId(), false);
        if (estFavori) {
            btnFavori.setText("❤️"); // Cœur rouge
            btnFavori.setStyle(btnFavori.getStyle() + "-fx-text-fill: red;");
        } else {
            btnFavori.setText("🤍"); // Cœur vide
        }

        Tooltip.install(btnFavori, new Tooltip(estFavori ? "Retirer des favoris" : "Ajouter aux favoris"));

        btnFavori.setOnAction(event -> {
            try {
                User currentUser = Session.getCurrentUser();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.WARNING, "Non connecté", "Veuillez vous connecter pour ajouter aux favoris.");
                    return;
                }

                if (etatFavoris.getOrDefault(s.getId(), false)) {
                    // Supprimer des favoris
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirmation");
                    confirmAlert.setHeaderText("Retirer des favoris");
                    confirmAlert.setContentText("Voulez-vous retirer ce service de vos favoris ?");

                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                boolean supprime = fs.supprimerFavori(s.getId());
                                if (supprime) {
                                    etatFavoris.put(s.getId(), false);
                                    btnFavori.setText("🤍");
                                    btnFavori.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 20px;");
                                    Tooltip.install(btnFavori, new Tooltip("Ajouter aux favoris"));
                                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Service retiré des favoris !");
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retirer des favoris.");
                            }
                        }
                    });
                } else {
                    // Ajouter aux favoris
                    boolean ajoute = fs.ajouterFavori(s.getId());
                    if (ajoute) {
                        etatFavoris.put(s.getId(), true);
                        btnFavori.setText("❤️");
                        btnFavori.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 20px; -fx-text-fill: red;");
                        Tooltip.install(btnFavori, new Tooltip("Retirer des favoris"));

                        // Option pour ajouter une note personnelle
                        TextInputDialog noteDialog = new TextInputDialog();
                        noteDialog.setTitle("Note personnelle");
                        noteDialog.setHeaderText("Ajouter une note pour ce favori (optionnel)");
                        noteDialog.setContentText("Note:");

                        noteDialog.showAndWait().ifPresent(note -> {
                            if (!note.trim().isEmpty()) {
                                // Ici vous pouvez sauvegarder la note si vous avez un champ note_personnelle
                                System.out.println("Note ajoutée: " + note);
                            }
                        });

                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté aux favoris !");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de gérer les favoris.");
            }
        });

        // ===== BUTTONS (PAYPAL ET AUTRES) =====
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
                        showAlert(Alert.AlertType.WARNING, "Invalide", "Durée doit être positive !");
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

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté !");
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

                    showAlert(Alert.AlertType.INFORMATION, "Annulé", "Service annulé !");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'annulation!");
            }
        });

        // HBox pour les boutons (Favori + Actions)
        HBox topButtons = new HBox(10, btnFavori);
        topButtons.setStyle("-fx-alignment: center-right;");

        HBox actionButtons = new HBox(10, btnAdd, btnCancel);
        actionButtons.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(
                topButtons,
                confirmationText,
                imageView,
                id,
                prix,
                type,
                typeService,
                statutText,
                localisationText,
                mapImage,
                actionButtons
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