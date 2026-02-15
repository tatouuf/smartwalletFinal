package controller.credit;

import entities.credit.Credit;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.credit.ServiceCredit;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AfficherCredit {

    @FXML private TableView<Credit> tableCredits;
    @FXML private TableColumn<Credit, Integer> colId;
    @FXML private TableColumn<Credit, String> colNomClient;
    @FXML private TableColumn<Credit, Float> colMontant;
    @FXML private TableColumn<Credit, String> colDateCredit;
    @FXML private TableColumn<Credit, String> colDescription;
    @FXML private TableColumn<Credit, String> colStatut;
    @FXML private TableColumn<Credit, Void> colActions;

    // 🔥 LOGO
    @FXML private ImageView imgLogoCredit;

    private final ServiceCredit serviceCredit = new ServiceCredit();
    private final ObservableList<Credit> creditList = FXCollections.observableArrayList();
    @FXML private Button btnRetourcredit;
    @FXML
    public void initialize() {
        loadLogo();          // ✅ charger logo
        setupColumns();
        styleTable();
        loadCredits();
    }
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainalc/MainALC.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetourcredit.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            showAlert("Erreur FXML", "Le fichier MainALC.fxml est introuvable ou le bouton est mal initialisé !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au menu principal !");
        }
    }

    // ================== LOGO ==================
    private void loadLogo() {
        try {
            Image logo = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream("/icons/logoservices.png")
                    )
            );
            imgLogoCredit.setImage(logo);

        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    // ================== TABLE ==================
    private void setupColumns() {

        colId.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getIdCredit()));

        colNomClient.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNomClient()));

        colMontant.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getMontant()));

        colDateCredit.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDateCredit() != null
                                ? data.getValue().getDateCredit().toString()
                                : ""
                ));

        colDescription.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));

        // 🎨 Statut coloré
        colStatut.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut().name()));

        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);

                if (empty || statut == null) {
                    setText(null);
                } else {
                    setText(statut);

                    if (statut.equalsIgnoreCase("ACCEPTE")) {
                        setTextFill(Color.web("#2e7d32"));
                    } else if (statut.equalsIgnoreCase("REFUSE")) {
                        setTextFill(Color.web("#c62828"));
                    } else {
                        setTextFill(Color.web("#1565c0"));
                    }
                }
            }
        });

        addActionButtons();
    }

    private void addActionButtons() {

        colActions.setCellFactory(param -> new TableCell<>() {

            private final Button btnModifier = new Button("Modify");
            private final Button btnSupprimer = new Button("Delete");
            private final HBox container = new HBox(10, btnModifier, btnSupprimer);

            {
                btnModifier.setStyle("""
                        -fx-background-color: #5e35b1;
                        -fx-text-fill: white;
                        -fx-background-radius: 20;
                        -fx-cursor: hand;
                        """);

                btnSupprimer.setStyle("""
                        -fx-background-color: #8e24aa;
                        -fx-text-fill: white;
                        -fx-background-radius: 20;
                        -fx-cursor: hand;
                        """);

                btnModifier.setOnAction(event -> {
                    Credit credit = getTableView().getItems().get(getIndex());
                    openModifierWindow(credit);
                });

                btnSupprimer.setOnAction(event -> {
                    Credit credit = getTableView().getItems().get(getIndex());
                    deleteCredit(credit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void styleTable() {
        tableCredits.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 15;
                -fx-border-radius: 15;
                -fx-padding: 10;
                """);
    }

    // ================== DATA ==================
    private void loadCredits() {
        try {
            List<Credit> credits = serviceCredit.recupererCredits();
            creditList.setAll(credits);
            tableCredits.setItems(creditList);

        } catch (SQLException e) {
            showAlert("Erreur Base de Données",
                    "Impossible de charger les crédits.");
        }
    }

    private void deleteCredit(Credit credit) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer ce crédit ?");
        confirm.setContentText("Client : " + credit.getNomClient());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceCredit.supprimerCredit(credit);
                    loadCredits();
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression.");
                }
            }
        });
    }

    private void openModifierWindow(Credit credit) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/credit/ModifierCredit.fxml"));

            Parent root = loader.load();
            ModifierCredit controller = loader.getController();
            controller.setCredit(credit);

            Stage stage = new Stage();
            stage.setTitle("Modifier Crédit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCredits();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
