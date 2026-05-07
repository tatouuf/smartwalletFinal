package esprit.tn.souha_pi.controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.TransactionService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HistoryController implements Initializable {

    @FXML private TableView<Transaction> table;

    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Number> colAmount;
    @FXML private TableColumn<Transaction, String> colWith;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, String> colStatus;

    @FXML private Label userInfoLabel;
    @FXML private Label totalLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private Label totalDebitsLabel;
    @FXML private Label countLabel;

    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> periodFilter;

    @FXML private VBox noHistoryBox;
    @FXML private Label noHistoryLabel;

    private final TransactionService transactionService = new TransactionService();

    private entities.User currentUser;
    private List<Transaction> allTransactions = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentUser = Session.getCurrentUser();

        configurerColonnes();
        configurerFiltres();
        chargerHistorique();
    }

    private void configurerColonnes() {
        if (colDate != null) {
            colDate.setCellValueFactory(cellData -> {
                Transaction transaction = cellData.getValue();

                if (transaction != null && transaction.getCreatedAt() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    return new SimpleStringProperty(sdf.format(transaction.getCreatedAt()));
                }

                return new SimpleStringProperty("-");
            });
        }

        if (colType != null) {
            colType.setCellValueFactory(cellData -> {
                Transaction transaction = cellData.getValue();
                String type = transaction != null ? safe(transaction.getType()) : "-";

                String upperType = type.toUpperCase();

                String icon;
                if (upperType.contains("CREDIT") || upperType.contains("CRÉDIT") || upperType.contains("RECEIVE") || upperType.contains("RECEPTION") || upperType.contains("TOP_UP")) {
                    icon = "⬇️ ";
                } else {
                    icon = "⬆️ ";
                }

                return new SimpleStringProperty(icon + type);
            });
        }

        if (colAmount != null) {
            colAmount.setCellValueFactory(cellData -> {
                Transaction transaction = cellData.getValue();

                if (transaction == null) {
                    return new SimpleDoubleProperty(0);
                }

                return new SimpleDoubleProperty(transaction.getAmount());
            });

            colAmount.setCellFactory(col -> new TableCell<Transaction, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        return;
                    }

                    double value = item.doubleValue();
                    setText(String.format("%.2f TND", Math.abs(value)));

                    if (value >= 0) {
                        setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            });
        }

        if (colWith != null) {
            colWith.setCellValueFactory(cellData -> {
                Transaction transaction = cellData.getValue();

                if (transaction == null) {
                    return new SimpleStringProperty("-");
                }

                String target = safe(transaction.getTarget());

                String cleaned = target
                        .replace("Transfert vers ", "")
                        .replace("Réception de ", "")
                        .replace("Reception de ", "");

                return new SimpleStringProperty(cleaned.isBlank() ? "-" : cleaned);
            });
        }

        if (colDescription != null) {
            colDescription.setCellValueFactory(cellData -> {
                Transaction transaction = cellData.getValue();

                if (transaction == null) {
                    return new SimpleStringProperty("-");
                }

                String type = safe(transaction.getType()).toUpperCase();
                String target = safe(transaction.getTarget());

                if (type.contains("SEND") || type.contains("DEBIT") || type.contains("ENVOI")) {
                    return new SimpleStringProperty("💰 Envoi d'argent");
                }

                if (type.contains("RECEIVE") || type.contains("RECEPTION") || type.contains("CREDIT")) {
                    return new SimpleStringProperty("💵 Réception d'argent");
                }

                if (type.contains("TOP_UP") || type.contains("DEPOT")) {
                    return new SimpleStringProperty("📥 Rechargement wallet");
                }

                if (type.contains("LOAN")) {
                    return new SimpleStringProperty("🤝 Transaction prêt");
                }

                return new SimpleStringProperty(target.isBlank() ? "-" : target);
            });
        }

        if (colStatus != null) {
            colStatus.setCellValueFactory(cellData -> {
                Transaction transaction = cellData.getValue();

                if (transaction == null) {
                    return new SimpleStringProperty("-");
                }

                if (transaction.getAmount() >= 0) {
                    return new SimpleStringProperty("✅ Reçu");
                }

                return new SimpleStringProperty("⏫ Envoyé");
            });

            colStatus.setCellFactory(col -> new TableCell<Transaction, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                        return;
                    }

                    setText(item);

                    if (item.contains("Reçu")) {
                        setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    }
                }
            });
        }
    }

    private void configurerFiltres() {
        if (typeFilter != null) {
            typeFilter.getItems().setAll(
                    "Tous",
                    "Crédits",
                    "Débits",
                    "Envois",
                    "Réceptions",
                    "Top Up",
                    "Prêts"
            );
            typeFilter.setValue("Tous");
        }

        if (periodFilter != null) {
            periodFilter.getItems().setAll(
                    "Tout",
                    "Aujourd'hui",
                    "Cette semaine",
                    "Ce mois",
                    "3 derniers mois"
            );
            periodFilter.setValue("Tout");
        }
    }

    private void chargerHistorique() {
        if (currentUser == null) {
            afficherNonConnecte();
            return;
        }

        try {
            allTransactions = transactionService.getUserTransactions(currentUser.getId());

            if (allTransactions == null) {
                allTransactions = new ArrayList<>();
            }

            if (userInfoLabel != null) {
                userInfoLabel.setText(
                        safe(currentUser.getFullname()) + " - " + safe(currentUser.getEmail())
                );
            }

            if (allTransactions.isEmpty()) {
                mettreAJourAffichage(allTransactions);
                afficherVide();
                return;
            }

            mettreAJourAffichage(allTransactions);

            if (noHistoryBox != null) {
                noHistoryBox.setVisible(false);
                noHistoryBox.setManaged(false);
            }

            if (table != null) {
                table.setVisible(true);
                table.setManaged(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'historique : " + e.getMessage());
        }
    }

    private void mettreAJourAffichage(List<Transaction> transactions) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        if (table != null) {
            table.setItems(FXCollections.observableArrayList(transactions));
        }

        double total = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double credits = transactions.stream()
                .filter(t -> t.getAmount() > 0)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double debits = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .mapToDouble(t -> Math.abs(t.getAmount()))
                .sum();

        if (totalLabel != null) {
            totalLabel.setText(String.format("Total: %.2f TND", total));
        }

        if (totalCreditsLabel != null) {
            totalCreditsLabel.setText(String.format("%.2f TND", credits));
        }

        if (totalDebitsLabel != null) {
            totalDebitsLabel.setText(String.format("%.2f TND", debits));
        }

        if (countLabel != null) {
            countLabel.setText(String.valueOf(transactions.size()));
        }

        if (transactions.isEmpty()) {
            afficherVide();
        } else {
            if (noHistoryBox != null) {
                noHistoryBox.setVisible(false);
                noHistoryBox.setManaged(false);
            }

            if (table != null) {
                table.setVisible(true);
                table.setManaged(true);
            }
        }
    }

    @FXML
    private void appliquerFiltres() {
        if (allTransactions == null) {
            return;
        }

        List<Transaction> filtered = allTransactions.stream()
                .filter(this::filterByType)
                .filter(this::filterByPeriod)
                .collect(Collectors.toList());

        mettreAJourAffichage(filtered);
    }

    private boolean filterByType(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        String selected = typeFilter != null ? typeFilter.getValue() : "Tous";
        String type = safe(transaction.getType()).toUpperCase();

        if ("Tous".equals(selected)) {
            return true;
        }

        if ("Crédits".equals(selected)) {
            return transaction.getAmount() > 0;
        }

        if ("Débits".equals(selected)) {
            return transaction.getAmount() < 0;
        }

        if ("Envois".equals(selected)) {
            return type.contains("SEND") || type.contains("DEBIT") || type.contains("ENVOI");
        }

        if ("Réceptions".equals(selected)) {
            return type.contains("RECEIVE") || type.contains("RECEPTION") || type.contains("CREDIT") || transaction.getAmount() > 0;
        }

        if ("Top Up".equals(selected)) {
            return type.contains("TOP_UP") || type.contains("DEPOT");
        }

        if ("Prêts".equals(selected)) {
            return type.contains("LOAN");
        }

        return true;
    }

    private boolean filterByPeriod(Transaction transaction) {
        if (transaction == null || transaction.getCreatedAt() == null) {
            return true;
        }

        String selected = periodFilter != null ? periodFilter.getValue() : "Tout";

        LocalDate transactionDate = transaction.getCreatedAt().toLocalDateTime().toLocalDate();
        LocalDate now = LocalDate.now();

        switch (selected) {
            case "Aujourd'hui":
                return transactionDate.equals(now);

            case "Cette semaine":
                return !transactionDate.isBefore(now.minusDays(7));

            case "Ce mois":
                return transactionDate.getMonth() == now.getMonth()
                        && transactionDate.getYear() == now.getYear();

            case "3 derniers mois":
                return !transactionDate.isBefore(now.minusMonths(3));

            default:
                return true;
        }
    }

    @FXML
    private void reinitialiserFiltres() {
        if (typeFilter != null) {
            typeFilter.setValue("Tous");
        }

        if (periodFilter != null) {
            periodFilter.setValue("Tout");
        }

        mettreAJourAffichage(allTransactions);
    }

    private void afficherNonConnecte() {
        if (table != null) {
            table.setVisible(false);
            table.setManaged(false);
        }

        if (noHistoryBox != null) {
            noHistoryBox.setVisible(true);
            noHistoryBox.setManaged(true);
        }

        if (noHistoryLabel != null) {
            noHistoryLabel.setText("❌ Veuillez vous connecter pour voir votre historique");
        }

        if (userInfoLabel != null) {
            userInfoLabel.setText("Non connecté");
        }
    }

    private void afficherVide() {
        if (table != null) {
            table.setVisible(false);
            table.setManaged(false);
        }

        if (noHistoryBox != null) {
            noHistoryBox.setVisible(true);
            noHistoryBox.setManaged(true);
        }

        if (noHistoryLabel != null) {
            noHistoryLabel.setText("📭 Aucune transaction pour le moment");
        }
    }

    @FXML
    private void exportPDF() {
        try {
            if (currentUser == null) {
                showAlert("Erreur", "Utilisateur non connecté.");
                return;
            }

            if (table == null || table.getItems() == null || table.getItems().isEmpty()) {
                showAlert("Erreur", "Aucune transaction à exporter.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF files", "*.pdf")
            );
            fileChooser.setInitialFileName("historique_transactions.pdf");

            File file = fileChooser.showSaveDialog(new Stage());

            if (file == null) {
                return;
            }

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Historique des Transactions")
                    .setBold()
                    .setFontSize(18));

            document.add(new Paragraph("Utilisateur : " + safe(currentUser.getFullname())));
            document.add(new Paragraph("Email : " + safe(currentUser.getEmail())));
            document.add(new Paragraph(" "));

            Table pdfTable = new Table(6);

            pdfTable.addHeaderCell("Date");
            pdfTable.addHeaderCell("Type");
            pdfTable.addHeaderCell("Montant");
            pdfTable.addHeaderCell("Avec");
            pdfTable.addHeaderCell("Description");
            pdfTable.addHeaderCell("Statut");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Transaction transaction : table.getItems()) {
                String date = transaction.getCreatedAt() != null
                        ? sdf.format(transaction.getCreatedAt())
                        : "-";

                pdfTable.addCell(date);
                pdfTable.addCell(safe(transaction.getType()));
                pdfTable.addCell(String.format("%.2f TND", transaction.getAmount()));
                pdfTable.addCell(safe(transaction.getTarget()));
                pdfTable.addCell(getDescriptionForExport(transaction));
                pdfTable.addCell(transaction.getAmount() >= 0 ? "Reçu" : "Envoyé");
            }

            document.add(pdfTable);
            document.close();

            showAlert("Succès", "PDF exporté avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'exporter le PDF : " + e.getMessage());
        }
    }

    @FXML
    private void exportExcel() {
        try {
            if (table == null || table.getItems() == null || table.getItems().isEmpty()) {
                showAlert("Erreur", "Aucune transaction à exporter.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier Excel CSV");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV files", "*.csv")
            );
            fileChooser.setInitialFileName("historique_transactions.csv");

            File file = fileChooser.showSaveDialog(new Stage());

            if (file == null) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Date;Type;Montant;Avec;Description;Statut");

                for (Transaction transaction : table.getItems()) {
                    String date = transaction.getCreatedAt() != null
                            ? sdf.format(transaction.getCreatedAt())
                            : "-";

                    String status = transaction.getAmount() >= 0 ? "Reçu" : "Envoyé";

                    writer.println(
                            escapeCsv(date) + ";" +
                                    escapeCsv(safe(transaction.getType())) + ";" +
                                    escapeCsv(String.format("%.2f TND", transaction.getAmount())) + ";" +
                                    escapeCsv(safe(transaction.getTarget())) + ";" +
                                    escapeCsv(getDescriptionForExport(transaction)) + ";" +
                                    escapeCsv(status)
                    );
                }
            }

            showAlert("Succès", "Fichier CSV exporté avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'exporter le CSV : " + e.getMessage());
        }
    }

    private String getDescriptionForExport(Transaction transaction) {
        if (transaction == null) {
            return "-";
        }

        String type = safe(transaction.getType()).toUpperCase();
        String target = safe(transaction.getTarget());

        if (type.contains("SEND") || type.contains("DEBIT") || type.contains("ENVOI")) {
            return "Envoi d'argent";
        }

        if (type.contains("RECEIVE") || type.contains("RECEPTION") || type.contains("CREDIT")) {
            return "Réception d'argent";
        }

        if (type.contains("TOP_UP") || type.contains("DEPOT")) {
            return "Rechargement wallet";
        }

        if (type.contains("LOAN")) {
            return "Transaction prêt";
        }

        return target.isBlank() ? "-" : target;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String cleaned = value.replace("\"", "\"\"");

        if (cleaned.contains(";") || cleaned.contains("\"") || cleaned.contains("\n")) {
            return "\"" + cleaned + "\"";
        }

        return cleaned;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}