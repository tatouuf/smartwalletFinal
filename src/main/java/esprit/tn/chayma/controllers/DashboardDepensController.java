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
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.Session;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardDepensController {

    @FXML private Label totalDepensesLabel;
    @FXML private Label totalPlanningsLabel;
    @FXML private Label totalBudgetsLabel;
    @FXML private Label bienvenueLabel;
    @FXML private ComboBox<String> filterUserCombo;
    @FXML private TableView<PlanningAdminView> planningsTable;
    @FXML private PieChart depensesChart;
    @FXML private Button btnRetourDashboard;

    private PlanningService planningService = PlanningService.getInstance();
    private int currentUserId;

    @FXML
    public void initialize() {
        // Vérifier que tous les champs FXML sont bien injectés
        if (totalBudgetsLabel == null || totalDepensesLabel == null || totalPlanningsLabel == null) {
            System.err.println("[DashboardDepens] ERREUR: Certains labels FXML sont null!");
            return;
        }

        if (Session.getCurrentUser() != null) {
            currentUserId = Session.getCurrentUser().getId();
            String userName = Session.getCurrentUser().getNom();
            if (bienvenueLabel != null) {
                bienvenueLabel.setText("👋 Bonjour, " + userName + " !");
            }
        } else {
            currentUserId = 1;
            if (bienvenueLabel != null) {
                bienvenueLabel.setText("👋 Bonjour !");
            }
        }

        loadData();
        setupTable();
    }

    private void loadData() {
        try {
            List<Planning> plannings = planningService.getPlanningsByUser(currentUserId);

            if (plannings == null || plannings.isEmpty()) {
                // Aucune donnée, afficher 0
                if (totalBudgetsLabel != null) totalBudgetsLabel.setText("0.00 DT");
                if (totalDepensesLabel != null) totalDepensesLabel.setText("0.00 DT");
                if (totalPlanningsLabel != null) totalPlanningsLabel.setText("0");
                return;
            }

            // CORRECTION : Utiliser Double au lieu de double pour éviter les problèmes avec null
            double totalBudgets = plannings.stream().mapToDouble(p -> {
                Double budget = p.getBudgetTotal();
                return budget != null ? budget : 0.0;
            }).sum();

            double totalDepenses = plannings.stream().mapToDouble(p -> {
                Double depense = p.getDepensesActuelles();
                return depense != null ? depense : 0.0;
            }).sum();

            int totalPlannings = plannings.size();

            if (totalBudgetsLabel != null) totalBudgetsLabel.setText(String.format("%.2f DT", totalBudgets));
            if (totalDepensesLabel != null) totalDepensesLabel.setText(String.format("%.2f DT", totalDepenses));
            if (totalPlanningsLabel != null) totalPlanningsLabel.setText(String.valueOf(totalPlannings));

            updateChart(plannings);
            updateTable(plannings);

        } catch (Exception e) {
            System.err.println("[DashboardDepens] Erreur chargement: " + e.getMessage());
            e.printStackTrace();
            if (totalBudgetsLabel != null) totalBudgetsLabel.setText("Erreur");
            if (totalDepensesLabel != null) totalDepensesLabel.setText("Erreur");
        }
    }

    private void updateTable(List<Planning> plannings) {
        if (planningsTable == null) return;

        try {
            ObservableList<PlanningAdminView> items = FXCollections.observableArrayList();

            for (Planning p : plannings) {
                // CORRECTION : Utiliser des variables temporaires avec vérification null
                double budget = 0.0;
                Double budgetObj = p.getBudgetTotal();
                if (budgetObj != null) budget = budgetObj;

                double depense = 0.0;
                Double depenseObj = p.getDepensesActuelles();
                if (depenseObj != null) depense = depenseObj;

                double reste = budget - depense;
                double progression = budget > 0 ? (depense / budget) * 100 : 0;

                String nom = p.getNom() != null ? p.getNom() : "Sans nom";
                String categorie = p.getCategorie() != null ? p.getCategorie() : "Non catégorisé";
                String statut = p.getStatut() != null ? p.getStatut() : "EN_COURS";

                items.add(new PlanningAdminView(nom, categorie, budget, depense, reste, progression, statut));
            }

            planningsTable.setItems(items);
        } catch (Exception e) {
            System.err.println("[DashboardDepens] Erreur mise à jour tableau: " + e.getMessage());
        }
    }

    private void updateChart(List<Planning> plannings) {
        if (depensesChart == null) return;

        try {
            Map<String, Double> depensesParCategorie = plannings.stream()
                    .flatMap(p -> {
                        List<Depense> depenses = p.getDepenses();
                        return depenses != null ? depenses.stream() : java.util.stream.Stream.empty();
                    })
                    .filter(d -> d.getCategorie() != null && !d.getCategorie().isEmpty())
                    .collect(Collectors.groupingBy(
                            Depense::getCategorie,
                            Collectors.summingDouble(d -> {
                                Double montant = d.getMontant();
                                return montant != null ? montant : 0.0;
                            })
                    ));

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : depensesParCategorie.entrySet()) {
                if (entry.getValue() > 0) {
                    pieData.add(new PieChart.Data(entry.getKey() + " (" + String.format("%.2f", entry.getValue()) + " DT)", entry.getValue()));
                }
            }

            if (pieData.isEmpty()) {
                pieData.add(new PieChart.Data("Aucune dépense", 1));
            }

            depensesChart.setData(pieData);
        } catch (Exception e) {
            System.err.println("[DashboardDepens] Erreur mise à jour graphique: " + e.getMessage());
        }
    }



    private void setupTable() {
        if (planningsTable == null) return;

        planningsTable.getColumns().clear();

        TableColumn<PlanningAdminView, String> nameCol = new TableColumn<>("Planning");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlanningName()));
        nameCol.setPrefWidth(150);

        TableColumn<PlanningAdminView, String> catCol = new TableColumn<>("Catégorie");
        catCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategorie()));
        catCol.setPrefWidth(120);

        TableColumn<PlanningAdminView, Double> budgetCol = new TableColumn<>("Budget");
        budgetCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getBudget()));
        budgetCol.setPrefWidth(100);
        budgetCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.2f DT", item));
            }
        });

        TableColumn<PlanningAdminView, Double> depenseCol = new TableColumn<>("Dépensé");
        depenseCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDepense()));
        depenseCol.setPrefWidth(100);
        depenseCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.2f DT", item));
            }
        });

        TableColumn<PlanningAdminView, Double> resteCol = new TableColumn<>("Reste");
        resteCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getReste()));
        resteCol.setPrefWidth(100);
        resteCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("%.2f DT", item));
            }
        });

        TableColumn<PlanningAdminView, Double> progCol = new TableColumn<>("Progression");
        progCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProgression()));
        progCol.setPrefWidth(150);
        progCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ProgressBar pb = new ProgressBar(Math.min(item / 100, 1.0));
                    pb.setPrefWidth(80);
                    Label label = new Label(String.format("%.1f%%", item));
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5, pb, label);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        TableColumn<PlanningAdminView, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));
        statutCol.setPrefWidth(100);
        statutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    if ("EN_COURS".equals(item)) {
                        badge.setStyle("-fx-background-color: #f59e0b20; -fx-text-fill: #f59e0b; -fx-padding: 3 8; -fx-background-radius: 10;");
                    } else if ("TERMINE".equals(item)) {
                        badge.setStyle("-fx-background-color: #22c55e20; -fx-text-fill: #22c55e; -fx-padding: 3 8; -fx-background-radius: 10;");
                    } else {
                        badge.setStyle("-fx-background-color: #ef444420; -fx-text-fill: #ef4444; -fx-padding: 3 8; -fx-background-radius: 10;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        planningsTable.getColumns().addAll(nameCol, catCol, budgetCol, depenseCol, resteCol, progCol, statutCol);
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
            System.err.println("[DashboardDepens] Erreur retour dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class PlanningAdminView {
        private final String planningName;
        private final String categorie;
        private final double budget;
        private final double depense;
        private final double reste;
        private final double progression;
        private final String statut;

        public PlanningAdminView(String planningName, String categorie, double budget, double depense, double reste, double progression, String statut) {
            this.planningName = planningName;
            this.categorie = categorie;
            this.budget = budget;
            this.depense = depense;
            this.reste = reste;
            this.progression = progression;
            this.statut = statut;
        }

        public String getPlanningName() { return planningName; }
        public String getCategorie() { return categorie; }
        public double getBudget() { return budget; }
        public double getDepense() { return depense; }
        public double getReste() { return reste; }
        public double getProgression() { return progression; }
        public String getStatut() { return statut; }
    }
}