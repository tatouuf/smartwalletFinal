package controller.credit;

import entities.credit.Credit;
import entities.credit.StatutCredit;
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
import javafx.stage.Stage;
import javafx.stage.Modality;
import services.credit.ServiceCredit;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AfficherCreditClient {

    @FXML private TableView<Credit> tableCredits;
    @FXML private TableColumn<Credit, Integer> colId;
    @FXML private TableColumn<Credit, String> colNomClient;
    @FXML private TableColumn<Credit, Float> colMontant;
    @FXML private TableColumn<Credit, String> colDateCredit;
    @FXML private TableColumn<Credit, String> colDescription;
    @FXML private TableColumn<Credit, String> colStatut;
    @FXML private TableColumn<Credit, Void> colActions;

    @FXML private ImageView imgLogoCredit;
    @FXML private Button btnRetourcredit;
    @FXML private Button itafa;

    private final ServiceCredit serviceCredit = new ServiceCredit();
    private final ObservableList<Credit> creditList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadLogo();
        setupColumns();
        styleTable();
        loadCredits();
    }

    // ================== RETOUR ==================
    @FXML
    private void retourMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuilservices/AcceuilServiceClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourcredit.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main ALC");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir au menu principal !");
        }
    }

    // ================== LOGO ==================
    private void loadLogo() {
        try {
            Image logo = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/icons/logoservices.png"))
            );
            imgLogoCredit.setImage(logo);
        } catch (Exception e) {
            System.out.println("❌ Logo introuvable !");
        }
    }

    // ================== TABLE ==================
    private void setupColumns() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdCredit()));
        colNomClient.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomClient()));
        colMontant.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMontant()));
        colDateCredit.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDateCredit() != null ? data.getValue().getDateCredit().toString() : ""
        ));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        // Statut coloré
        colStatut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatut().name()));
        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                } else {
                    setText(statut);
                    if (statut.equalsIgnoreCase("REMBOURSE")) {
                        setTextFill(Color.web("#2e7d32"));
                    } else if (statut.equalsIgnoreCase("REFUSE")) {
                        setTextFill(Color.web("#c62828"));
                    } else {
                        setTextFill(Color.web("#1565c0"));
                    }
                }
            }
        });

        // Actions Add / Cancel
        addActionButtons();
    }

    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {

            private final Button btnAdd = new Button("Add");
            private final Button btnCancel = new Button("Cancel");
            private final HBox container = new HBox(10, btnAdd, btnCancel);

            {
                btnAdd.setStyle("-fx-background-color: #8d36ff; -fx-text-fill: white; -fx-font-weight: bold;");
                btnCancel.setStyle("-fx-background-color: #c388b5; -fx-text-fill: white; -fx-font-weight: bold;");

                btnAdd.setOnAction(event -> {
                    Credit credit = getTableView().getItems().get(getIndex());
                    try {
                        credit.setStatut(StatutCredit.REMBOURSE);
                        serviceCredit.modifierStatutCredit(credit);
                        tableCredits.refresh();
                        btnAdd.setDisable(true);
                        showAlert("Success", "Credit marked as REMBOURSE!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert("Error", "Failed to update credit status!");
                    }
                });

                btnCancel.setOnAction(event -> {
                    Credit credit = getTableView().getItems().get(getIndex());
                    try {
                        credit.setStatut(StatutCredit.NON_REMBOURSE);
                        serviceCredit.modifierStatutCredit(credit);
                        tableCredits.refresh();
                        btnAdd.setDisable(false);
                        showAlert("Cancelled", "Credit reset to NON_REMBOURSE.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert("Error", "Failed to reset credit status!");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    @FXML
    private void itafaaction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/credit/AjouterCredit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) itafa.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Credit");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Ajouter Credit !");
        }
    }

    private void styleTable() {
        tableCredits.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 15;
                -fx-border-radius: 15;
                -fx-padding: 10;
                """);
    }

    // ================== LOAD DATA ==================
    private void loadCredits() {
        try {
            List<Credit> credits = serviceCredit.recupererCredits();

            // Filtrer uniquement les NON_REMBOURSE pour cet affichage client
            creditList.setAll(credits.stream()
                    .filter(c -> c.getStatut() == StatutCredit.NON_REMBOURSE)
                    .toList());

            tableCredits.setItems(creditList);
        } catch (SQLException e) {
            showAlert("Erreur Base de Données", "Impossible de charger les crédits.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
