package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Planning;
import esprit.tn.chayma.services.DepenseService;
import esprit.tn.chayma.services.PlanningService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.DataChangeNotifier;
import utils.Session;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardDepensController {

    // ========== FXML (d'après votre fichier .fxml) ==========
    @FXML private Label totalDepensesLabel;       // Total général des dépenses
    @FXML private Label depensesMoisLabel;        // Dépenses du mois en cours
    @FXML private Label totalPlanningsLabel;      // Nombre de plannings (optionnel)
    @FXML private PieChart depensesCategorieChart; // PieChart des catégories
    @FXML private LineChart<String, Number> evolutionDepensesChart; // LineChart évolution
    @FXML private Button btnRetourDashboard;

    // Services
    private DepenseService depenseService = new DepenseService();
    private PlanningService planningService = PlanningService.getInstance();
    private int currentUserId;

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur connecté
        if (Session.getCurrentUser() != null) {
            currentUserId = Session.getCurrentUser().getId();
        } else {
            currentUserId = 1; // fallback
        }

        // S'abonner aux notifications de changement (dépenses modifiées)
        DataChangeNotifier.addListener(this::refreshAll);

        // Premier chargement
        refreshAll();
    }

    /**
     * Rafraîchit tout le tableau de bord : cartes, pie chart et line chart.
     */
    private void refreshAll() {
        updateSummaryCards();
        updatePieChart();
        updateEvolutionChart();
    }

    /**
     * Met à jour les trois cartes : total dépenses, dépenses du mois, nombre de plannings.
     */
    private void updateSummaryCards() {
        List<Depense> allDepenses = depenseService.getAllByUser(currentUserId);

        // Total général
        double total = allDepenses.stream()
                .mapToDouble(Depense::getMontant)
                .sum();
        totalDepensesLabel.setText(String.format("%.2f DT", total));

        // Dépenses du mois en cours
        LocalDate now = LocalDate.now();
        double monthTotal = allDepenses.stream()
                .filter(d -> d.getDateDepense() != null)
                .filter(d -> d.getDateDepense().getYear() == now.getYear()
                        && d.getDateDepense().getMonth() == now.getMonth())
                .mapToDouble(Depense::getMontant)
                .sum();
        depensesMoisLabel.setText(String.format("%.2f DT", monthTotal));

        // Nombre de plannings (optionnel, gardé pour compatibilité)
        List<Planning> plannings = planningService.getPlanningsByUser(currentUserId);
        totalPlanningsLabel.setText(String.valueOf(plannings.size()));
    }

    /**
     * Met à jour le PieChart des dépenses par catégorie.
     */
    private void updatePieChart() {
        List<Depense> allDepenses = depenseService.getAllByUser(currentUserId);
        Map<String, Double> depensesParCategorie = allDepenses.stream()
                .filter(d -> d.getCategorie() != null && !d.getCategorie().isEmpty())
                .collect(Collectors.groupingBy(
                        Depense::getCategorie,
                        Collectors.summingDouble(Depense::getMontant)
                ));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : depensesParCategorie.entrySet()) {
            if (entry.getValue() > 0) {
                pieData.add(new PieChart.Data(
                        entry.getKey() + " (" + String.format("%.2f", entry.getValue()) + " DT)",
                        entry.getValue()
                ));
            }
        }

        if (pieData.isEmpty()) {
            pieData.add(new PieChart.Data("Aucune dépense", 1));
        }

        depensesCategorieChart.setData(pieData);
    }

    /**
     * Met à jour le LineChart de l'évolution mensuelle des dépenses (12 derniers mois).
     */
    private void updateEvolutionChart() {
        if (evolutionDepensesChart == null) return;

        List<Depense> allDepenses = depenseService.getAllByUser(currentUserId);

        // Grouper par année-mois (format "yyyy-MM")
        Map<String, Double> monthlySum = allDepenses.stream()
                .filter(d -> d.getDateDepense() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getDateDepense().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.summingDouble(Depense::getMontant)
                ));

        // Trier les mois
        List<String> sortedMonths = monthlySum.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        // Ne garder que les 12 derniers mois si la liste est longue
        if (sortedMonths.size() > 12) {
            sortedMonths = sortedMonths.subList(sortedMonths.size() - 12, sortedMonths.size());
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Dépenses mensuelles");

        for (String month : sortedMonths) {
            series.getData().add(new XYChart.Data<>(month, monthlySum.get(month)));
        }

        evolutionDepensesChart.getData().clear();
        if (!series.getData().isEmpty()) {
            evolutionDepensesChart.getData().add(series);
        } else {
            // Aucune donnée : afficher un message
            evolutionDepensesChart.setTitle("Aucune dépense enregistrée");
        }
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au Dashboard !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}