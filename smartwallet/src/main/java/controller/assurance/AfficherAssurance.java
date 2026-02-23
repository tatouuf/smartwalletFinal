package controller.assurance;

import entities.assurances.Assurances;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AfficherAssurance {
    @FXML private Button retourhaamdi;
    @FXML
    private FlowPane cardAffAssurance;
    @FXML
    private Button haamdi;

    @FXML
    private ImageView imgLogoAssurance; // 🔥 LOGO

    private ServiceAssurances serviceAssurances = new ServiceAssurances();
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainalc/MainALC.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) retourhaamdi.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu principal !");
        }
    }
    @FXML
    private void haamdiah() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/assurance/AjouterAssurance.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) haamdi.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Insurance");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir Ajouter Assurance !");
        }
    }

    @FXML
    public void initialize() {
        loadLogo();       // ✅ charger logo
        loadAssurances();
    }

    // ================== LOGO ==================
    private void loadLogo() {
        try {
            Image logo = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream("/icons/logoservices.png")
                    )
            );
            imgLogoAssurance.setImage(logo);
        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    // ================== ASSURANCES ==================
    public void loadAssurances() {
        try {
            List<Assurances> list = serviceAssurances.recupererAssurance();
            cardAffAssurance.getChildren().clear();

            for (Assurances a : list) {
                VBox card = new VBox(5);
                card.setPrefWidth(220);
                card.setStyle("-fx-border-color:black; -fx-padding:10; -fx-background-color:#f4f4f4;");

                Text id = new Text("Code : " + a.getId());
                Text nom = new Text("Name : " + a.getNomAssurance());
                Text type = new Text("Type : " + a.getTypeAssurance());
                Text prix = new Text("Amount : " + a.getPrix());
                Text duree = new Text("Duration : " + a.getDureeMois() + " mois");
                Text statut = new Text("Status : " + a.getStatut());

                Button btnModifier = new Button("Modify");
                btnModifier.setOnAction(e -> showModifierAssurance(a));

                Button btnSupprimer = new Button("Delete");
                btnSupprimer.setOnAction(e -> {
                    try {
                        serviceAssurances.supprimerAssurance(a);
                        loadAssurances();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                card.getChildren().addAll(
                        id, nom, type, prix, duree, statut,
                        btnModifier, btnSupprimer
                );

                cardAffAssurance.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showModifierAssurance(Assurances a) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/assurance/ModifierAssurance.fxml")
            );
            Parent root = loader.load();

            ModifierAssurance controller = loader.getController();
            controller.setAssurance(a);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Assurance");
            stage.show();

            stage.setOnHidden(e -> loadAssurances());

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
