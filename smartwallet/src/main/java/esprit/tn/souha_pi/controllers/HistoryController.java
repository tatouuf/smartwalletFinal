package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.TransactionService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import services.ServiceUser;
import utils.Session;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    private TransactionService transactionService = new TransactionService();
    private ServiceUser userService = new ServiceUser();
    entities.User currentUser = Session.getCurrentUser();
    private List<Transaction> allTransactions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        entities.User currentUser = Session.getCurrentUser();

        configurerColonnes();
        configurerFiltres();
        chargerHistorique();
    }

    private void configurerColonnes() {
        // Date formatée
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                return new SimpleStringProperty(sdf.format(cellData.getValue().getCreatedAt()));
            }
            return new SimpleStringProperty("");
        });

        // Type avec icône
        colType.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getType();
            String icon = type.contains("CRÉDIT") || type.contains("RECEIVE") ? "⬇️ " : "⬆️ ";
            return new SimpleStringProperty(icon + type);
        });

        // Montant coloré
        colAmount.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getAmount()));

        colAmount.setCellFactory(col -> new TableCell<Transaction, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    double value = item.doubleValue();
                    setText(String.format("%.2f TND", Math.abs(value)));
                    if (value > 0) {
                        setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Avec qui (expéditeur/destinataire)
        colWith.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            String description = t.getTarget();

            // Extraire le nom de la personne
            if (description.contains("vers") || description.contains("de")) {
                return new SimpleStringProperty(description.replace("Transfert vers ", "")
                        .replace("Réception de ", ""));
            }
            return new SimpleStringProperty(description);
        });

        // Description détaillée
        colDescription.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            String desc = t.getTarget();

            // Enrichir la description
            if (t.getType().contains("SEND")) {
                return new SimpleStringProperty("💰 Envoi d'argent");
            } else if (t.getType().contains("RECEIVE")) {
                return new SimpleStringProperty("💵 Réception d'argent");
            } else if (t.getType().contains("TOP_UP")) {
                return new SimpleStringProperty("📥 Rechargement wallet");
            } else if (t.getType().contains("LOAN")) {
                return new SimpleStringProperty("🤝 Remboursement prêt");
            }
            return new SimpleStringProperty(desc);
        });

        // Statut de la transaction
        colStatus.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            if (t.getAmount() > 0) {
                return new SimpleStringProperty("✅ Reçu");
            } else {
                return new SimpleStringProperty("⏫ Envoyé");
            }
        });

        colStatus.setCellFactory(col -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if (item.contains("Reçu")) {
                        setStyle("-fx-text-fill: #22c55e;");
                    } else {
                        setStyle("-fx-text-fill: #3b82f6;");
                    }
                }
            }
        });
    }

    private void configurerFiltres() {
        typeFilter.getItems().addAll("Tous", "Crédits", "Débits", "Envois", "Réceptions");
        typeFilter.setValue("Tous");

        periodFilter.getItems().addAll("Tout", "Aujourd'hui", "Cette semaine", "Ce mois", "3 derniers mois");
        periodFilter.setValue("Tout");
    }

    private void chargerHistorique() {
        if (currentUser == null) {
            afficherNonConnecte();
            return;
        }

        try {
            allTransactions = transactionService.getUserTransactions(currentUser.getId());
            userInfoLabel.setText(currentUser.getFullname() + " - " + currentUser.getEmail());

            if (allTransactions.isEmpty()) {
                afficherVide();
                return;
            }

            mettreAJourAffichage(allTransactions);
            noHistoryBox.setVisible(false);
            noHistoryBox.setManaged(false);
            table.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mettreAJourAffichage(List<Transaction> transactions) {
        table.setItems(FXCollections.observableArrayList(transactions));

        // Calculer les totaux
        double total = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        double credits = transactions.stream().filter(t -> t.getAmount() > 0)
                .mapToDouble(Transaction::getAmount).sum();
        double debits = transactions.stream().filter(t -> t.getAmount() < 0)
                .mapToDouble(t -> Math.abs(t.getAmount())).sum();

        totalLabel.setText(String.format("Total: %.2f TND", total));
        totalCreditsLabel.setText(String.format("%.2f TND", credits));
        totalDebitsLabel.setText(String.format("%.2f TND", debits));
        countLabel.setText(String.valueOf(transactions.size()));
    }

    @FXML
    private void appliquerFiltres() {
        if (allTransactions == null) return;

        List<Transaction> filtered = allTransactions.stream()
                .filter(t -> filterByType(t))
                .filter(t -> filterByPeriod(t))
                .collect(Collectors.toList());

        mettreAJourAffichage(filtered);
    }

    private boolean filterByType(Transaction t) {
        String selected = typeFilter.getValue();
        if ("Tous".equals(selected)) return true;
        if ("Crédits".equals(selected)) return t.getAmount() > 0;
        if ("Débits".equals(selected)) return t.getAmount() < 0;
        if ("Envois".equals(selected)) return t.getType().contains("SEND");
        if ("Réceptions".equals(selected)) return t.getType().contains("RECEIVE") || t.getAmount() > 0;
        return true;
    }

    private boolean filterByPeriod(Transaction t) {
        if (t.getCreatedAt() == null) return true;

        String selected = periodFilter.getValue();
        LocalDate date = t.getCreatedAt().toLocalDateTime().toLocalDate();
        LocalDate now = LocalDate.now();

        switch (selected) {
            case "Aujourd'hui":
                return date.equals(now);
            case "Cette semaine":
                return date.isAfter(now.minusDays(7));
            case "Ce mois":
                return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
            case "3 derniers mois":
                return date.isAfter(now.minusMonths(3));
            default:
                return true;
        }
    }

    @FXML
    private void reinitialiserFiltres() {
        typeFilter.setValue("Tous");
        periodFilter.setValue("Tout");
        if (allTransactions != null) {
            mettreAJourAffichage(allTransactions);
        }
    }

    private void afficherNonConnecte() {
        table.setVisible(false);
        noHistoryBox.setVisible(true);
        noHistoryBox.setManaged(true);
        noHistoryLabel.setText("❌ Veuillez vous connecter pour voir votre historique");
        userInfoLabel.setText("Non connecté");
    }

    private void afficherVide() {
        table.setVisible(false);
        noHistoryBox.setVisible(true);
        noHistoryBox.setManaged(true);
        noHistoryLabel.setText("📭 Aucune transaction pour le moment");
    }
}