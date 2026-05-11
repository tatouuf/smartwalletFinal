package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.services.PlanningService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlanningsController {

    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private TextField budgetField;

    @FXML
    private DatePicker dateEcheancePicker;

    @FXML
    private Button btnAjouterPlanning;

    @FXML
    private ListView<Planning> planningsList;

    @FXML
    private VBox depensesContainer;

    @FXML
    private TextField depenseDescriptionField;

    @FXML
    private TextField depenseMontantField;

    @FXML
    private ComboBox<String> depenseCategorieCombo;

    @FXML
    private TextArea depenseCommentaireField;

    @FXML
    private Button btnAjouterDepense;

    @FXML
    private Label totalBudgetLabel;

    @FXML
    private Label totalDepensesLabel;

    @FXML
    private Button btnRetourDashboard;

    private PlanningService planningService;
    private Planning planningSelectionne;
    private ObservableList<Planning> planningsObservable;

    @FXML
    public void initialize() {
        planningService = PlanningService.getInstance();

        // Initialiser les ComboBox
        String[] categories = {"Alimentation", "Transport", "Éducation", "Loisirs", "Santé", "Logement", "Vêtements", "Autre"};
        categorieCombo.getItems().addAll(categories);
        depenseCategorieCombo.getItems().addAll(categories);

        // Charger les plannings de l'utilisateur connecté
        int userId = Session.isLoggedIn() ? Session.getCurrentUser().getId() : 1;
        loadPlannings(userId);

        // Liste des plannings
        planningsList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                planningSelectionne = selected;
                afficherDepenses(selected);
                updateTotaux();
            }
        });

        // Ajouter un planning
        btnAjouterPlanning.setOnAction(e -> ajouterPlanning());

        // Ajouter une dépense
        btnAjouterDepense.setOnAction(e -> ajouterDepense());

        updateTotaux();
    }

    private void loadPlannings(int userId) {
        planningsObservable = FXCollections.observableArrayList(planningService.getPlanningsByUser(userId));
        planningsList.setItems(planningsObservable);
    }

    private void ajouterPlanning() {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText();
        String categorie = categorieCombo.getValue();
        String budgetText = budgetField.getText().trim();
        LocalDateTime dateEcheance = dateEcheancePicker.getValue() != null ?
                dateEcheancePicker.getValue().atStartOfDay() : LocalDateTime.now().plusMonths(1);

        if (nom.isEmpty() || categorie == null || budgetText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            double budget = Double.parseDouble(budgetText);
            if (budget <= 0) {
                showAlert("Erreur", "Le budget doit être supérieur à 0");
                return;
            }

            int userId = Session.isLoggedIn() ? Session.getCurrentUser().getId() : 1;
            Planning planning = new Planning(0, userId, nom, description, categorie, budget, dateEcheance);

            if (planningService.addPlanning(userId, planning)) {
                planningsObservable.add(planning);
                clearPlanningForm();
                updateTotaux();
                showAlert("Succès", "Planning ajouté avec succès !");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Budget invalide");
        }
    }

    private void ajouterDepense() {
        if (planningSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un planning");
            return;
        }

        String description = depenseDescriptionField.getText().trim();
        String montantText = depenseMontantField.getText().trim();
        String categorie = depenseCategorieCombo.getValue();
        String commentaire = depenseCommentaireField.getText();

        if (description.isEmpty() || montantText.isEmpty() || categorie == null) {
            showAlert("Erreur", "Veuillez remplir description, montant et catégorie");
            return;
        }

        try {
            double montant = Double.parseDouble(montantText);
            if (montant <= 0) {
                showAlert("Erreur", "Le montant doit être supérieur à 0");
                return;
            }

            Depense depense = new Depense(0, planningSelectionne.getId(), description, montant, categorie, commentaire);

            if (planningService.addDepense(planningSelectionne.getId(), depense)) {
                afficherDepenses(planningSelectionne);
                clearDepenseForm();
                updateTotaux();
                showAlert("Succès", "Dépense ajoutée avec succès !");

                // Rafraîchir l'affichage du planning dans la liste
                planningsList.refresh();
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Montant invalide");
        }
    }

    private void afficherDepenses(Planning planning) {
        depensesContainer.getChildren().clear();

        Label title = new Label("📋 Dépenses pour: " + planning.getNom());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #4f46e5;");
        depensesContainer.getChildren().add(title);

        Label budgetInfo = new Label(String.format("💰 Budget: %.2f DT | Dépensé: %.2f DT | Reste: %.2f DT",
                planning.getBudgetTotal(), planning.getDepensesActuelles(), planning.getReste()));
        budgetInfo.setStyle("-fx-padding: 5 0 10 0;");
        depensesContainer.getChildren().add(budgetInfo);

        Label pourcentageInfo = new Label(String.format("📊 Progression: %.1f%%", planning.getPourcentage()));
        pourcentageInfo.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
        depensesContainer.getChildren().add(pourcentageInfo);

        Separator sep = new Separator();
        depensesContainer.getChildren().add(sep);

        if (planning.getDepenses().isEmpty()) {
            Label empty = new Label("Aucune dépense enregistrée pour ce planning.");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 10;");
            depensesContainer.getChildren().add(empty);
        } else {
            for (Depense d : planning.getDepenses()) {
                VBox depenseBox = new VBox(5);
                depenseBox.setStyle("-fx-background-color: #f9fafb; -fx-padding: 10; -fx-background-radius: 8;");

                Label descLabel = new Label("📝 " + d.getDescription());
                descLabel.setStyle("-fx-font-weight: bold;");

                Label montantLabel = new Label(String.format("💰 Montant: %.2f DT", d.getMontant()));
                montantLabel.setStyle("-fx-text-fill: #10b981;");

                Label catLabel = new Label("🏷️ Catégorie: " + d.getCategorie());

                Label dateLabel = new Label("📅 " + d.getDateDepense().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                dateLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");

                depenseBox.getChildren().addAll(descLabel, montantLabel, catLabel, dateLabel);

                if (d.getCommentaire() != null && !d.getCommentaire().isEmpty()) {
                    Label commentLabel = new Label("💬 " + d.getCommentaire());
                    commentLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
                    depenseBox.getChildren().add(commentLabel);
                }

                depensesContainer.getChildren().add(depenseBox);
            }
        }
    }

    private void updateTotaux() {
        int userId = Session.isLoggedIn() ? Session.getCurrentUser().getId() : 1;
        double totalBudget = planningService.getTotalBudgetByUser(userId);
        double totalDepenses = planningService.getTotalDepensesByUser(userId);

        totalBudgetLabel.setText(String.format("%.2f DT", totalBudget));
        totalDepensesLabel.setText(String.format("%.2f DT", totalDepenses));
    }

    private void clearPlanningForm() {
        nomField.clear();
        descriptionField.clear();
        categorieCombo.setValue(null);
        budgetField.clear();
        dateEcheancePicker.setValue(null);
    }

    private void clearDepenseForm() {
        depenseDescriptionField.clear();
        depenseMontantField.clear();
        depenseCategorieCombo.setValue(null);
        depenseCommentaireField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartWallet Admin Dashboard");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner au dashboard");
        }
    }
}