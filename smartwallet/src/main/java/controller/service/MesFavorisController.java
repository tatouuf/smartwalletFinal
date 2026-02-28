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
import services.service.FavoriService;
import services.service.ServiceServices;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class MesFavorisController {

    @FXML private Button btnRetour;
    @FXML private HBox cardFavoris;
    @FXML private ImageView imgLogoList;
    @FXML private Text txtNombreFavoris;

    private final FavoriService fs = new FavoriService();
    private final ServiceServices ss = new ServiceServices();
    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

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
        loadFavoris();
    }

    private void loadFavoris() {
        try {
            List<Services> favoris = fs.recupererFavoris();
            if (cardFavoris == null) return;

            cardFavoris.getChildren().clear();

            if (favoris.isEmpty()) {
                Text emptyText = new Text("Vous n'avez aucun favori pour le moment.");
                emptyText.setStyle("-fx-font-size: 16px; -fx-fill: #666;");
                cardFavoris.getChildren().add(emptyText);
                if (txtNombreFavoris != null) {
                    txtNombreFavoris.setText("0 favori");
                }
                return;
            }

            if (txtNombreFavoris != null) {
                txtNombreFavoris.setText(favoris.size() + " favori" + (favoris.size() > 1 ? "s" : ""));
            }

            for (Services s : favoris) {
                VBox card = createFavoriCard(s);
                cardFavoris.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les favoris.");
        }
    }

    private VBox createFavoriCard(Services s) {
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

        // ===== IMAGE =====
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);
        loadServiceImage(imageView, s.getImage());

        // ===== INFOS =====
        Text id = new Text("Code: " + s.getId());
        Text prix = new Text("Prix: " + s.getPrix() + " DT");
        Text type = new Text("Type: " + s.getType());
        Text statut = new Text("Statut: " + s.getStatutString());
        Text adresse = new Text("Adresse: " + (s.getAdresse() != null ? s.getAdresse() : "Non spécifiée"));
        Text typeService = new Text("Service: " + s.getTypeServiceString());

        // ===== BOUTON SUPPRIMER =====
        Button btnSupprimer = new Button("🗑️ Retirer");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

        btnSupprimer.setOnAction(e -> {
            try {
                boolean supprime = fs.supprimerFavori(s.getId());
                if (supprime) {
                    loadFavoris(); // Recharger la liste
                    showInfo("Succès", "Service retiré des favoris.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Impossible de retirer des favoris.");
            }
        });

        // ===== BOUTON VOIR DETAIL =====
        Button btnVoir = new Button("👁️ Voir détails");
        btnVoir.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        btnVoir.setOnAction(e -> {
            // Retourner à la page des services avec ce service en évidence
            goToServices();
        });

        HBox buttonBox = new HBox(10, btnSupprimer, btnVoir);
        buttonBox.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(
                imageView, id, prix, type, typeService, statut, adresse, buttonBox
        );

        return card;
    }

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

    @FXML
    private void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/services/AfficherServiceClient.fxml")
            );
            Parent root = loader.load();

            AfficherServiceClient controller = loader.getController();
            if (controller != null && hostServices != null) {
                controller.setHostServices(hostServices);
            }

            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 500));
            stage.setTitle("Services");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner aux services.");
        }
    }

    private void goToServices() {
        retour(); // Même action pour l'instant
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}