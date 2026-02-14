package controller.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.credit.ServiceCredit;

import java.sql.SQLException;
import java.util.List;

public class AfficherCredit {

    @FXML
    private TableView<Credit> tableCredits;

    @FXML
    private TableColumn<Credit, Integer> colId;

    @FXML
    private TableColumn<Credit, String> colNomClient;

    @FXML
    private TableColumn<Credit, Float> colMontant;

    @FXML
    private TableColumn<Credit, String> colDateCredit;

    @FXML
    private TableColumn<Credit, String> colDescription;

    @FXML
    private TableColumn<Credit, String> colStatut;

    @FXML
    private TableColumn<Credit, Void> colActions;

    private ServiceCredit serviceCredit = new ServiceCredit();

    @FXML
    public void initialize() {
        setupColumns();
        loadCredits();
    }

    private void setupColumns() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getIdCredit()));
        colNomClient.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNomClient()));
        colMontant.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getMontant()));
        colDateCredit.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDateCredit().toString()));
        colDescription.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescription()));
        colStatut.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut().name()));

        // Colonne Actions (Modifier / Supprimer)
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox pane = new HBox(10, btnModifier, btnSupprimer);

            {
                btnModifier.setOnAction(event -> {
                    Credit credit = getTableView().getItems().get(getIndex());
                    showModifierCredit(credit);
                });

                btnSupprimer.setOnAction(event -> {
                    Credit credit = getTableView().getItems().get(getIndex());
                    try {
                        serviceCredit.supprimerCredit(credit);
                        loadCredits(); // recharger après suppression
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Erreur lors de la suppression !");
                        alert.show();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadCredits() {
        try {
            List<Credit> credits = serviceCredit.recupererCredits();
            ObservableList<Credit> obsList = FXCollections.observableArrayList(credits);
            tableCredits.setItems(obsList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ouvrir ModifierCredit.fxml pour un crédit donné
    private void showModifierCredit(Credit c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/credit/ModifierCredit.fxml"));
            Parent root = loader.load();

            ModifierCredit controller = loader.getController();
            controller.setCredit(c);

            Stage stage = new Stage();
            stage.setTitle("Modifier Crédit");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(e -> loadCredits()); // recharger la table après modification
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
