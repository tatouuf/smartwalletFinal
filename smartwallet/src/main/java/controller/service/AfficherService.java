package controller.service;

import entities.service.Services;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.service.ServiceServices;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AfficherService {
    @FXML private Button retouritafser;
    @FXML
    private HBox cardaffserv;

    @FXML
    private ImageView imgLogoList; // Logo en haut à droite
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainalc/MainALC.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) retouritafser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu principal !");
        }
    }
    @FXML
    public void initialize() {
        // Charger le logo en haut à droite
        if (imgLogoList != null) {
            imgLogoList.setImage(
                    new Image(getClass().getResourceAsStream("/icons/logoservices.png"))
            );
            // Rendre le logo circulaire
            Circle clip = new Circle(40, 40, 40); // rayon = 40 pour fitWidth/fitHeight = 80
            imgLogoList.setClip(clip);
        }

        // Charger les services au démarrage
        loadServices();
    }


    /**
     * Charger tous les services depuis la base et créer des cartes dynamiques
     */
    public void loadServices() {
        ServiceServices ss = new ServiceServices();

        try {
            List<Services> services = ss.recupererServices();
            cardaffserv.getChildren().clear(); // vider le HBox avant d'ajouter

            for (Services s : services) {
                // Créer VBox pour chaque service
                VBox card = new VBox();
                card.setPrefWidth(200);
                card.setSpacing(5);
                card.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f4f4f4;");

                // ===== IMAGE DU SERVICE =====
                ImageView imageView = new ImageView();
                imageView.setFitWidth(200);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);

                if (s.getImage() != null && !s.getImage().isBlank()) {
                    File file = new File(s.getImage());
                    if (file.exists()) {
                        Image img = new Image(file.toURI().toString());
                        imageView.setImage(img);
                    }
                }

                // ===== TEXTES =====
                Text id = new Text("Code : " + s.getId());
                Text prix = new Text("Price : " + s.getPrix());
                Text type = new Text("Type : " + s.getType());
                Text statut = new Text("Statut : " + s.getStatutString());
                Text localisation = new Text("Location : " + s.getLocalisation());
                Text adresse = new Text("Adress : " + s.getAdresse());
                Text typeService = new Text("Type Service : " + s.getTypeServiceString());

                card.getChildren().addAll(
                        imageView,
                        id,
                        prix,
                        type,
                        statut,
                        localisation,
                        adresse,
                        typeService
                );

                // ===== BOUTONS Modifier / Supprimer =====
                HBox buttonsBox = new HBox(10);
                buttonsBox.setStyle("-fx-alignment: center; -fx-padding: 5;");

                Button btnModifier = new Button("Modify");
                btnModifier.setOnAction(event -> showModifierService(s));

                Button btnSupprimer = new Button("Detete");
                btnSupprimer.setOnAction(event -> {
                    try {
                        ss.supprimerServices(s);
                        loadServices(); // recharge après suppression
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                buttonsBox.getChildren().addAll(btnModifier, btnSupprimer);
                card.getChildren().add(buttonsBox);

                // Ajouter la card dans le HBox principal
                cardaffserv.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvrir ModifierService.fxml pour un service donné
     */
    private void showModifierService(Services s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/services/ModifierService.fxml"));
            Parent root = loader.load();

            ModifierService controller = loader.getController();
            controller.setService(s);

            Stage stage = new Stage();
            stage.setTitle("Modifier Service");
            stage.setScene(new Scene(root));
            stage.show();

            // Recharger la liste des services après fermeture
            stage.setOnHidden(e -> loadServices());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
