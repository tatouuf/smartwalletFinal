package Controllers;

import entities.Reclamation;
import entities.ReclamationStatuts;
import entities.Transaction;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceDashboardAdmin;
import services.ServiceNotification;
import entities.DashboardAdmin;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardAdminController {

    private static final Logger logger =
            Logger.getLogger(DashboardAdminController.class.getName());

    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalTransactions;
    @FXML private Label lblBenefice;
    @FXML private Label lblNotificationCount;

    @FXML private PieChart pieReclamations;
    @FXML private LineChart<String, Number> lineUsers;

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String>  colNom;
    @FXML private TableColumn<User, String>  colPrenom;
    @FXML private TableColumn<User, String>  colEmail;

    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, Integer> colTransactionId;
    @FXML private TableColumn<Transaction, Double>  colAmount;
    @FXML private TableColumn<Transaction, String>  colDate;

    private final ServiceDashboardAdmin dashboardService;
    private final ServiceNotification notificationService;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter dtf =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DashboardAdminController() {
        dashboardService = new ServiceDashboardAdmin();
        notificationService = new ServiceNotification();
    }

    @FXML
    private void initialize() {
        setupTables();
        loadDashboardData();
        updateNotificationCount();
    }

    private void setupTables() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableUsers.setPlaceholder(new Label("No users found."));

        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        colDate.setCellValueFactory(cell -> {
            var ts = cell.getValue().getCreatedAt();
            String display = ts != null
                    ? ts.toLocalDateTime().format(dtf)
                    : "—";
            return new javafx.beans.property.SimpleStringProperty(display);
        });

        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(df.format(amount) + " DT");
                    setStyle(amount >= 0
                            ? "-fx-text-fill: #10b981; -fx-font-weight: bold;"
                            : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                }
            }
        });

        tableTransactions.setPlaceholder(new Label("No transactions found."));
    }

    private void loadDashboardData() {
        try {
            DashboardAdmin dashboard = dashboardService.getDashboard();

            loadStatistics(dashboard);
            loadReclamationsPieChart(dashboard);
            loadUsersLineChart(dashboard);
            loadUsersTable(dashboard);
            loadTransactionsTable(dashboard);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading dashboard", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while loading dashboard data.");
        }
    }

    private void updateNotificationCount() {
        try {
            if (!Session.isLoggedIn()) return;
            int count = notificationService.countUnreadNotifications(
                    Session.getCurrentUser().getId());
            lblNotificationCount.setText(String.valueOf(count));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading notifications count", e);
        }
    }

    private void loadStatistics(DashboardAdmin dashboard) {
        lblTotalUsers.setText(String.valueOf(dashboard.getTotalUsers()));
        lblTotalTransactions.setText(String.valueOf(dashboard.getTotalTransactions()));
        lblBenefice.setText(df.format(dashboard.getBenefice()) + " DT");
    }

    private void loadReclamationsPieChart(DashboardAdmin dashboard) {
        int pending    = 0;
        int inProgress = 0;
        int resolved   = 0;

        for (Reclamation r : dashboard.getListeReclamations()) {
            if (r.getStatut() == null) continue;
            switch (r.getStatut()) {
                case PENDING    -> pending++;
                case IN_PROGRESS -> inProgress++;
                case RESOLVED   -> resolved++;
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Pending ("     + pending    + ")", pending),
                new PieChart.Data("In Progress (" + inProgress + ")", inProgress),
                new PieChart.Data("Resolved ("    + resolved   + ")", resolved)
        );

        pieReclamations.setData(pieData);
        pieReclamations.setLegendVisible(true);
        pieReclamations.setLabelsVisible(true);

        pieReclamations.getData().forEach(data -> {
            String color = switch (data.getName().split(" ")[0]) {
                case "Pending"    -> "#f59e0b";
                case "In"         -> "#3b82f6";
                case "Resolved"   -> "#10b981";
                default           -> "#cccccc";
            };
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        });
    }

    private void loadUsersLineChart(DashboardAdmin dashboard) {
        java.util.Map<String, Long> byMonth = new java.util.LinkedHashMap<>();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"};

        for (String m : months) byMonth.put(m, 0L);

        dashboard.getListeUsers().forEach(u -> {
            if (u.getDate_creation() != null) {
                String month = months[u.getDate_creation().getMonthValue() - 1];
                byMonth.merge(month, 1L, Long::sum);
            }
        });

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Users");
        byMonth.forEach((month, count) ->
                series.getData().add(new XYChart.Data<>(month, count)));

        lineUsers.getData().clear();
        lineUsers.getData().add(series);
    }

    private void loadUsersTable(DashboardAdmin dashboard) {
        tableUsers.setItems(
                FXCollections.observableArrayList(dashboard.getListeUsers()));
    }

    private void loadTransactionsTable(DashboardAdmin dashboard) {
        tableTransactions.setItems(
                FXCollections.observableArrayList(dashboard.getListeTransactions()));
    }

    @FXML
    private void handleManageUsers() {
        MainFxml.getInstance().showManageUsers();
    }

    @FXML
    private void handleManageReclamations() {
        MainFxml.getInstance().showReclamationAdmin();
    }

    @FXML
    private void handleNotifications() {
        MainFxml.getInstance().showNotifications();
    }

    @FXML
    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to logout?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Session.clearSession();
                MainFxml.getInstance().showSignIn();
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}