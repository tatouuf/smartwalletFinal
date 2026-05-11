package esprit.tn.souha_pi.controllers.loan;

import entities.User;
import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.services.LoanRequestService;
import esprit.tn.souha_pi.services.ia.ICreditScoringService;
import esprit.tn.souha_pi.services.ia.impl.CreditScoringService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import services.ServiceUser;
import utils.Session;

import java.sql.SQLException;
import java.util.List;

public class RequestLoanController {

    @FXML private ComboBox<User> userCombo;
    @FXML private TextField amountField;
    @FXML private TextArea messageField;
    @FXML private Label statusLabel;

    @FXML private VBox iaAnalysisBox;
    @FXML private Label iaProbabilityLabel;
    @FXML private ProgressBar iaProbabilityBar;
    @FXML private Label iaRecommendationLabel;
    @FXML private Label iaSuggestionLabel;

    @FXML private VBox iaInsightsContainer;
    @FXML private Label iaSummaryLabel;

    private ServiceUser userService = new ServiceUser();
    private LoanRequestService requestService = new LoanRequestService();
    private ICreditScoringService creditScoringService = new CreditScoringService();

    private User currentUser;
    private int currentUserId;
    private double currentAmount = 0;
    private User currentLender = null;

    @FXML
    public void initialize() {
        currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            DialogUtil.error("Erreur", "Vous devez être connecté");
            return;
        }

        currentUserId = currentUser.getId();

        // Charger les utilisateurs éligibles
        try {
            List<User> users = userService.recupererUsersOnly();
            users.removeIf(u -> u.getId() == currentUserId);
            userCombo.getItems().setAll(users);
        } catch (SQLException e) {
            DialogUtil.error("Erreur", "Impossible de charger les utilisateurs: " + e.getMessage());
        }

        // Configuration de l'affichage des utilisateurs dans la ComboBox
        userCombo.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? "" : u.getPrenom() + " " + u.getNom());
            }
        });

        userCombo.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? "" : u.getPrenom() + " " + u.getNom());
            }
        });

        // Listeners pour l'analyse IA
        userCombo.valueProperty().addListener((obs, old, newVal) -> {
            currentLender = newVal;
            analyzeWithIA();
        });

        amountField.textProperty().addListener((obs, old, newVal) -> {
            try {
                currentAmount = Double.parseDouble(newVal);
                analyzeWithIA();
            } catch (Exception ignored) {}
        });

        if (iaAnalysisBox != null) {
            iaAnalysisBox.setVisible(false);
        }

        loadIAInsights();
    }

    private void analyzeWithIA() {
        if (iaAnalysisBox == null) return;

        if (currentLender == null || currentAmount <= 0) {
            iaAnalysisBox.setVisible(false);
            return;
        }

        try {
            ICreditScoringService.RepaymentProbability currentProbability =
                    creditScoringService.predictRepayment(currentUserId, currentAmount, 30);

            iaAnalysisBox.setVisible(true);

            double prob = currentProbability.getProbability();
            double percent = prob * 100;

            iaProbabilityLabel.setText(String.format("Probabilité: %.0f%%", percent));
            iaProbabilityBar.setProgress(prob);

            String color = prob > 0.7 ? "#27ae60" : (prob > 0.4 ? "#f39c12" : "#e74c3c");
            iaProbabilityLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

            iaRecommendationLabel.setText("Niveau: " + currentProbability.getLevel());

            if (!currentProbability.getFactors().isEmpty()) {
                iaSuggestionLabel.setText("💡 " + currentProbability.getFactors().get(0));
            } else {
                iaSuggestionLabel.setText("💡 Analyse en cours...");
            }

        } catch (Exception e) {
            iaAnalysisBox.setVisible(false);
        }
    }

    private void loadIAInsights() {
        if (iaInsightsContainer == null || iaSummaryLabel == null) return;

        iaInsightsContainer.getChildren().clear();

        List<LoanRequest> sentRequests = requestService.getRequestsByBorrower(currentUserId);

        if (sentRequests.isEmpty()) {
            iaSummaryLabel.setText("Aucune demande envoyée");
            return;
        }

        Label title = new Label("📊 MES DEMANDES");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6b21a5;");
        iaInsightsContainer.getChildren().add(title);

        int totalRequests = sentRequests.size();
        double totalAmount = 0;
        int pending = 0, accepted = 0, rejected = 0;

        for (LoanRequest req : sentRequests) {
            totalAmount += req.getAmount();
            switch (req.getStatus()) {
                case "PENDING": pending++; break;
                case "ACCEPTED": accepted++; break;
                case "REJECTED": rejected++; break;
            }
        }

        iaSummaryLabel.setText(String.format("%d demandes • %.2f TND", totalRequests, totalAmount));

        VBox stats = new VBox(5);
        stats.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 8;");
        stats.getChildren().addAll(
                new Label("✅ Acceptées: " + accepted),
                new Label("⏳ En attente: " + pending),
                new Label("❌ Refusées: " + rejected)
        );

        iaInsightsContainer.getChildren().add(stats);
    }

    @FXML
    private void sendRequest() {
        try {
            if (userCombo.getValue() == null) {
                DialogUtil.error("Erreur", "Veuillez sélectionner un prêteur");
                return;
            }

            User lender = userCombo.getValue();

            String amountStr = amountField.getText().trim();
            if (amountStr.isEmpty()) {
                DialogUtil.error("Erreur", "Veuillez saisir un montant");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    DialogUtil.error("Erreur", "Le montant doit être positif");
                    return;
                }
                if (amount > 10000) {
                    DialogUtil.error("Erreur", "Le montant maximum est de 10 000 TND");
                    return;
                }
            } catch (NumberFormatException e) {
                DialogUtil.error("Erreur", "Montant invalide");
                return;
            }

            // Confirmation
            String message = String.format(
                    "Prêteur: %s %s\nMontant: %.2f TND\n\nConfirmez-vous ?",
                    lender.getPrenom(), lender.getNom(), amount
            );

            if (!DialogUtil.confirm("Confirmation", message)) {
                return;
            }

            // Créer la demande
            requestService.createRequest(
                    currentUserId,
                    lender.getId(),
                    amount,
                    messageField.getText()
            );

            DialogUtil.success("Succès", "Demande de prêt envoyée !");

            // Réinitialiser
            amountField.clear();
            messageField.clear();
            userCombo.setValue(null);
            if (iaAnalysisBox != null) {
                iaAnalysisBox.setVisible(false);
            }
            loadIAInsights();

        } catch (Exception e) {
            DialogUtil.error("Erreur", "Impossible d'envoyer la demande: " + e.getMessage());
        }
    }
}