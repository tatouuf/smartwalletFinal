package controller.assurance;

import entities.assurances.Assurances;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.assurances.ServiceAssurances;

import java.sql.SQLException;
import java.util.List;

public class AfficherAssurance {

    @FXML
    private HBox cardAffAssurance;

    private ServiceAssurances serviceAssurances = new ServiceAssurances();

    @FXML
    public void initialize() {
        loadAssurances();
    }

    public void loadAssurances() {

        try {
            List<Assurances> list = serviceAssurances.recupererAssurance();
            cardAffAssurance.getChildren().clear();

            for (Assurances a : list) {

                VBox card = new VBox(5);
                card.setPrefWidth(220);
                card.setStyle("-fx-border-color:black; -fx-padding:10; -fx-background-color:#f4f4f4;");

                Text id = new Text("ID : " + a.getId());
                Text nom = new Text("Nom : " + a.getNomAssurance());
                Text type = new Text("Type : " + a.getTypeAssurance());
                Text prix = new Text("Prix : " + a.getPrix());
                Text duree = new Text("Durée : " + a.getDureeMois() + " mois");
                Text statut = new Text("Statut : " + a.getStatut());

                Button btnModifier = new Button("Modifier");
                btnModifier.setOnAction(e -> showModifierAssurance(a));

                Button btnSupprimer = new Button("Supprimer");
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
}
