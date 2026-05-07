package controller.assurance;

import entities.assurances.Assurances;
import entities.assurances.Statut;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AfficherAssuranceClient {

    @FXML
    private Button retourhaamdi;

    @FXML
    private FlowPane cardAffAssurance;

    @FXML
    private ImageView imgLogoAssurance;

    private final ServiceAssurances serviceAssurances = new ServiceAssurances();

    // ================= RETOUR AU MENU PRINCIPAL =================
    @FXML
    private void retourMain() {
        try {
            // Charger le FXML du menu principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml"));
            Parent root = loader.load();

            // Récupérer la stage actuelle
            Stage stage = (Stage) retourhaamdi.getScene().getWindow();

            // Mettre à jour la scène
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Impossible to return to main menu!");
        }
    }



    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        loadLogo();
        loadAssurances();
    }

    // ================= CHARGER LOGO =================
    private void loadLogo() {
        try {
            Image logo = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/icons/logoservices.png")
            ));
            imgLogoAssurance.setImage(logo);
        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    // ================= CHARGER LES ASSURANCES INACTIVE =================
    private void loadAssurances() {
        try {
            List<Assurances> list = serviceAssurances.recupererAssurance();
            cardAffAssurance.getChildren().clear();

            for (Assurances a : list) {

                // 🔹 Afficher uniquement les assurances INACTIVE
                if (a.getStatut() != Statut.INACTIVE) continue;

                VBox card = new VBox(8);
                card.setPrefWidth(220);
                card.setStyle("-fx-border-color:black; -fx-padding:10; -fx-background-color:#f4f4f4;");

                Text id = new Text("Code: " + a.getId());
                Text nom = new Text("Name: " + a.getNomAssurance());
                Text type = new Text("Type: " + a.getTypeAssurance());
                Text prix = new Text("Amount: " + a.getPrix());
                Text duree = new Text("Duration: " + a.getDureeMois() + " months");
                Text statut = new Text("Status: " + a.getStatut());

                // ===== BUTTONS ADD / CANCEL =====
                Button btnAdd = new Button("Add");
                Button btnCancel = new Button("Cancel");

                btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                btnCancel.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

                // Action ADD → active l'assurance mais ne retire pas la carte
                btnAdd.setOnAction(e -> {
                    try {
                        a.setStatut(Statut.ACTIVE);
                        serviceAssurances.modifierStatutAssurance(a);

                        statut.setText("Status: " + a.getStatut());
                        btnAdd.setDisable(true);

                        showAlert(Alert.AlertType.INFORMATION, "Success", "Insurance activated successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to activate insurance!");
                    }
                });

                // Action CANCEL → remet l'assurance en INACTIVE si on change d’avis
                btnCancel.setOnAction(e -> {
                    try {
                        a.setStatut(Statut.INACTIVE);
                        serviceAssurances.modifierStatutAssurance(a);

                        statut.setText("Status: " + a.getStatut());
                        btnAdd.setDisable(false);

                        showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Insurance deactivated successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to deactivate insurance!");
                    }
                });

                HBox buttonBox = new HBox(10, btnAdd, btnCancel);
                card.getChildren().addAll(id, nom, type, prix, duree, statut, buttonBox);
                cardAffAssurance.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load insurances!");
        }
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
