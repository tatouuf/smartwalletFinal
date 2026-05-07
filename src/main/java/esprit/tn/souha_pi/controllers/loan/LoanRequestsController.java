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
import javafx.scene.layout.HBox;
import services.ServiceUser;
import utils.Session;

import java.util.List;

public class LoanRequestsController {

    @FXML private VBox requestsContainer;
    @FXML private VBox iaInsightsContainer;
    @FXML private Label iaSummaryLabel;
    @FXML private Label statusLabel;

    private LoanRequestService requestService = new LoanRequestService();
    private ServiceUser userService = new ServiceUser();
    private ICreditScoringService creditScoringService = new CreditScoringService();

    private User currentUser;
    private int currentUserId;

    @FXML
    public void initialize() {
        currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            if (statusLabel != null) {
                statusLabel.setText("Session expirée. Veuillez vous reconnecter.");
            }
            return;
        }

        currentUserId = currentUser.getId();
        loadRequests();
        loadIAInsights();
    }

    private void loadRequests() {
        if (requestsContainer == null) return;

        requestsContainer.getChildren().clear();

        List<LoanRequest> list = requestService.getRequestsForLender(currentUserId);

        if (list.isEmpty()) {
            Label empty = new Label("Aucune demande de prêt reçue");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 15px;");
            requestsContainer.getChildren().add(empty);
            return;
        }

        for (LoanRequest request : list) {
            VBox card = createRequestCard(request);
            requestsContainer.getChildren().add(card);
        }
    }

    private VBox createRequestCard(LoanRequest request) {
        try {
            User borrower = userService.getById(request.getBorrowerId());
            String borrowerName = (borrower != null) ?
                    borrower.getPrenom() + " " + borrower.getNom() : "Utilisateur inconnu";

            // Nom
            Label nameLabel = new Label(borrowerName);
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Montant
            Label amountLabel = new Label(String.format("%.2f TND", request.getAmount()));
            amountLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");

            // Message
            Label messageLabel = new Label("Message: " + (request.getMessage() != null ? request.getMessage() : ""));
            messageLabel.setStyle("-fx-text-fill: #555;");
            messageLabel.setWrapText(true);

            // Date
            Label dateLabel = new Label("Demandé le: " + request.getCreatedAt());

            // Boutons
            Button acceptBtn = new Button("✅ Accepter");
            acceptBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            acceptBtn.setOnAction(e -> acceptRequest(request));

            Button rejectBtn = new Button("❌ Refuser");
            rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            rejectBtn.setOnAction(e -> rejectRequest(request));

            HBox actions = new HBox(10, acceptBtn, rejectBtn);

            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 12; " +
                    "-fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10,0,0,3);");

            card.getChildren().addAll(nameLabel, amountLabel, messageLabel, dateLabel, actions);
            return card;

        } catch (Exception e) {
            e.printStackTrace();
            VBox errorCard = new VBox(10);
            errorCard.setStyle("-fx-background-color: #ffeeee; -fx-padding: 18; -fx-background-radius: 12;");
            errorCard.getChildren().add(new Label("Erreur lors du chargement"));
            return errorCard;
        }
    }

    private void acceptRequest(LoanRequest request) {
        boolean confirmed = DialogUtil.confirm(
                "Accepter la demande",
                String.format("Vous allez ACCEPTER cette demande de prêt.\n\nMontant: %.2f TND\n\nVoulez-vous continuer ?",
                        request.getAmount())
        );

        if (!confirmed) return;

        try {
            requestService.acceptRequest(request.getId());
            DialogUtil.success("Succès", "Prêt créé avec succès !");
            loadRequests();
            loadIAInsights();
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Impossible d'accepter: " + e.getMessage());
        }
    }

    private void rejectRequest(LoanRequest request) {
        boolean confirmed = DialogUtil.confirm(
                "Refuser la demande",
                "Êtes-vous sûr de vouloir refuser cette demande ?"
        );

        if (!confirmed) return;

        try {
            requestService.rejectRequest(request.getId());
            DialogUtil.success("Succès", "Demande refusée");
            loadRequests();
            loadIAInsights();
        } catch (Exception e) {
            DialogUtil.error("Erreur", "Impossible de refuser: " + e.getMessage());
        }
    }

    private void loadIAInsights() {
        if (iaInsightsContainer == null || iaSummaryLabel == null) return;

        iaInsightsContainer.getChildren().clear();

        List<LoanRequest> list = requestService.getRequestsForLender(currentUserId);

        if (list.isEmpty()) {
            iaSummaryLabel.setText("Aucune demande à analyser");
            return;
        }

        Label title = new Label("🤖 ANALYSE IA");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6b21a5;");
        iaInsightsContainer.getChildren().add(title);

        int totalRequests = list.size();
        double totalAmount = 0;
        for (LoanRequest r : list) {
            totalAmount += r.getAmount();
        }

        iaSummaryLabel.setText(String.format("%d demandes • %.2f TND", totalRequests, totalAmount));

        for (LoanRequest request : list) {
            try {
                User borrower = userService.getById(request.getBorrowerId());
                String borrowerName = (borrower != null) ?
                        borrower.getPrenom() + " " + borrower.getNom() : "Inconnu";

                ICreditScoringService.TrustScore score =
                        creditScoringService.calculateTrustScore(request.getBorrowerId(), currentUserId);

                HBox insightRow = new HBox(10);
                insightRow.setStyle("-fx-padding: 8; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");

                Label nameLabel = new Label(borrowerName + ":");
                nameLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 100;");

                Label scoreLabel = new Label(String.format("%.0f/100", score.getScore()));
                String color = score.getScore() >= 70 ? "#27ae60" : (score.getScore() >= 50 ? "#f39c12" : "#e74c3c");
                scoreLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-min-width: 60;");

                insightRow.getChildren().addAll(nameLabel, scoreLabel);
                iaInsightsContainer.getChildren().add(insightRow);

            } catch (Exception e) {
                System.err.println("Erreur analyse IA: " + e.getMessage());
            }
        }
    }
}