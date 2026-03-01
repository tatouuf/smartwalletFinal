package esprit.tn.souha_pi.controllers.loan;

<<<<<<< HEAD
import entities.User;
import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.services.LoanRequestService;
=======
import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.LoanRequestService;
import esprit.tn.souha_pi.services.UserService;
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import esprit.tn.souha_pi.services.ia.ICreditScoringService;
import esprit.tn.souha_pi.services.ia.impl.CreditScoringService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import esprit.tn.souha_pi.utils.DialogUtil;
<<<<<<< HEAD
import services.ServiceUser;
import utils.Session;

import java.sql.SQLException;
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import java.util.List;

public class LoanRequestsController {

    @FXML
    private VBox requestsContainer;

    @FXML
    private Label statusLabel;

    // IA Components
    @FXML
    private VBox iaInsightsContainer;

    @FXML
    private Label iaSummaryLabel;

    private LoanRequestService requestService = new LoanRequestService();
<<<<<<< HEAD
    private ServiceUser userService = new ServiceUser();
=======
    private UserService userService = new UserService();
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

    // IA Service
    private ICreditScoringService creditScoringService = new CreditScoringService();

<<<<<<< HEAD
    private User currentUser;
    private int currentUserId;

    @FXML
    public void initialize(){
        currentUser = Session.getCurrentUser();

        if(currentUser == null){
            System.out.println("ERROR: No logged user in session");
            statusLabel.setText("Session expired. Please login again.");
            return;
        }

        currentUserId = currentUser.getId();

=======
    // ⚠️ remplacer par user connecté plus tard
    private int currentUserId = 1;

    @FXML
    public void initialize(){
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        loadRequests();
        loadIAInsights();
    }

    /* ================= LOAD ================= */
<<<<<<< HEAD
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    private void loadRequests(){
        requestsContainer.getChildren().clear();

        List<LoanRequest> list = requestService.getRequestsForLender(currentUserId);

        if(list.isEmpty()){
            Label empty = new Label("No loan requests");
            empty.setStyle("-fx-text-fill:gray; -fx-font-size:15px;");
            requestsContainer.getChildren().add(empty);
            return;
        }

        for(LoanRequest request : list){
            VBox card = createRequestCard(request);
            requestsContainer.getChildren().add(card);
        }
    }

    /* ================= IA INSIGHTS ================= */


    private void loadIAInsights() {
        if (iaInsightsContainer == null) return;

        iaInsightsContainer.getChildren().clear();

        List<LoanRequest> list = requestService.getRequestsForLender(currentUserId);

        if (list.isEmpty()) {
            return;
        }

        Label title = new Label("🤖 ANALYSE IA DES DEMANDES");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#9b59b6;");
        iaInsightsContainer.getChildren().add(title);

        int totalRequests = list.size();
        double totalAmount = 0;
        for (LoanRequest r : list) {
            totalAmount += r.getAmount();
        }

        Label stats = new Label(String.format(
                "📊 %d demandes • %.2f TND",
                totalRequests, totalAmount
        ));
        stats.setStyle("-fx-text-fill:#555; -fx-padding: 0 0 10 0;");
        iaInsightsContainer.getChildren().add(stats);

        // Analyser chaque demande avec IA
        for (LoanRequest request : list) {
            try {
<<<<<<< HEAD
                // CORRECTION: Gérer l'exception SQLException
                User borrower = null;
                try {
                    borrower = userService.getById(request.getBorrowerId());
                } catch (SQLException e) {
                    System.err.println("Erreur lors du chargement de l'emprunteur: " + e.getMessage());
                }

                String borrowerName = "Unknown User";
                if(borrower != null){
                    borrowerName = borrower.getPrenom() + " " + borrower.getNom();
                }
=======
                User borrower = userService.getById(request.getBorrowerId());
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

                // Calculer score de confiance
                ICreditScoringService.TrustScore score =
                        creditScoringService.calculateTrustScore(
                                request.getBorrowerId(),
                                currentUserId
                        );

                HBox insightRow = new HBox(10);
                insightRow.setStyle("-fx-padding: 8; -fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-radius: 5;");

                Label nameLabel = new Label(borrowerName + ":");
                Label nameLabel = new Label(borrower.getFullname() + ":");
                nameLabel.setStyle("-fx-font-weight:bold; -fx-min-width: 100;");

                Label scoreLabel = new Label(String.format("%.0f/100", score.getScore()));
                scoreLabel.setStyle("-fx-font-weight:bold; -fx-min-width: 60;");

                // Color by score
                if (score.getScore() >= 70) {
                    scoreLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight:bold; -fx-min-width: 60;");
                } else if (score.getScore() >= 50) {
                    scoreLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight:bold; -fx-min-width: 60;");
                } else {
                    scoreLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight:bold; -fx-min-width: 60;");
                }

                Label levelLabel = new Label("(" + score.getLevel() + ")");
                levelLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-min-width: 80;");

                Button analyzeBtn = new Button("🔍 Détails");
                analyzeBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5 10;");

                LoanRequest finalRequest = request;
                analyzeBtn.setOnAction(e -> showIADetails(finalRequest, score));

                insightRow.getChildren().addAll(nameLabel, scoreLabel, levelLabel, analyzeBtn);
                iaInsightsContainer.getChildren().add(insightRow);

            } catch (Exception e) {
<<<<<<< HEAD
=======
                // Ignorer les erreurs pour une demande
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
                System.err.println("Erreur analyse IA: " + e.getMessage());
            }
        }
    }

    private void showIADetails(LoanRequest request, ICreditScoringService.TrustScore score) {
<<<<<<< HEAD
        try {
            // CORRECTION: Gérer l'exception SQLException
            User borrower = null;
            try {
                borrower = userService.getById(request.getBorrowerId());
            } catch (SQLException e) {
                System.err.println("Erreur lors du chargement de l'emprunteur: " + e.getMessage());
            }

            StringBuilder details = new StringBuilder();
            String borrowerName = (borrower != null) ?
                    borrower.getPrenom() + " " + borrower.getNom() : "Unknown User";

            details.append("=== ANALYSE IA POUR ").append(borrowerName).append(" ===\n\n");
            details.append("Score de confiance: ").append(String.format("%.1f/100", score.getScore())).append("\n");
            details.append("Niveau: ").append(score.getLevel()).append("\n\n");

            details.append("Facteurs analysés:\n");
            for (var entry : score.getFactors().entrySet()) {
                details.append("• ").append(entry.getKey()).append(": ")
                        .append(String.format("%.0f%%", entry.getValue() * 100)).append("\n");
            }

            details.append("\n✅ Forces:\n");
            if (score.getStrengths().isEmpty()) {
                details.append("• Aucune force particulière\n");
            } else {
                for (String s : score.getStrengths()) {
                    details.append("• ").append(s).append("\n");
                }
            }

            details.append("\n⚠️ Points d'attention:\n");
            if (score.getWeaknesses().isEmpty()) {
                details.append("• Aucun point faible\n");
            } else {
                for (String w : score.getWeaknesses()) {
                    details.append("• ").append(w).append("\n");
                }
            }

            DialogUtil.confirm("Analyse IA Détail", details.toString());

        } catch (Exception e) {
            DialogUtil.error("Erreur", "Impossible d'afficher les détails: " + e.getMessage());
        }
    }

    /* ================= CARD ================= */
    private VBox createRequestCard(LoanRequest request){
        try {
            // CORRECTION: Gérer l'exception SQLException
            User borrower = null;
            try {
                borrower = userService.getById(request.getBorrowerId());
            } catch (SQLException e) {
                System.err.println("Erreur lors du chargement de l'emprunteur: " + e.getMessage());
            }

            String borrowerName = (borrower != null) ?
                    borrower.getPrenom() + " " + borrower.getNom() : "Unknown User";

            /* ---- borrower ---- */
            Label name = new Label(borrowerName);
            name.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

            /* ---- amount ---- */
            Label amount = new Label(request.getAmount() + " TND");
            amount.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#2ecc71;");

            /* ---- message ---- */
            Label message = new Label("Message: " + request.getMessage());
            message.setStyle("-fx-text-fill:#555;");

            /* ---- date ---- */
            Label date = new Label("Requested at: " + request.getCreatedAt());

            /* ---- buttons ---- */
            Button accept = new Button("✅ Accept");
            accept.setStyle("-fx-background-color:#27ae60; -fx-text-fill:white; -fx-font-weight:bold;");

            Button reject = new Button("❌ Reject");
            reject.setStyle("-fx-background-color:#e74c3c; -fx-text-fill:white; -fx-font-weight:bold;");

            // IA Button
            Button analyzeBtn = new Button("🤖 IA");
            analyzeBtn.setStyle("-fx-background-color:#9b59b6; -fx-text-fill:white; -fx-font-weight:bold;");

            accept.setOnAction(e -> acceptRequest(request));
            reject.setOnAction(e -> rejectRequest(request));

            analyzeBtn.setOnAction(e -> {
                try {
                    ICreditScoringService.TrustScore score =
                            creditScoringService.calculateTrustScore(
                                    request.getBorrowerId(),
                                    currentUserId
                            );
                    showIADetails(request, score);
                } catch (Exception ex) {
                    DialogUtil.error("Erreur IA", ex.getMessage());
                }
            });

            HBox actions = new HBox(10, accept, reject, analyzeBtn);

            /* ---- card ---- */
            VBox card = new VBox(10);
            card.setStyle(
                    "-fx-background-color:white;" +
                            "-fx-padding:18;" +
                            "-fx-background-radius:12;" +
                            "-fx-border-radius:12;" +
                            "-fx-border-color:#e0e0e0;" +
                            "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 12,0,0,3);"
            );

            card.getChildren().addAll(name, amount, message, date, actions);

            return card;

        } catch (Exception e) {
            // En cas d'erreur, retourner une carte d'erreur
            VBox errorCard = new VBox(10);
            errorCard.setStyle("-fx-background-color:#ffeeee; -fx-padding:18; -fx-background-radius:12;");
            errorCard.getChildren().add(new Label("Erreur lors du chargement de la demande"));
            return errorCard;
        }
    }

    /* ================= ACCEPT ================= */
    private void acceptRequest(LoanRequest request){
=======
        User borrower = userService.getById(request.getBorrowerId());

        StringBuilder details = new StringBuilder();
        details.append("=== ANALYSE IA POUR ").append(borrower.getFullname()).append(" ===\n\n");
        details.append("Score de confiance: ").append(String.format("%.1f/100", score.getScore())).append("\n");
        details.append("Niveau: ").append(score.getLevel()).append("\n\n");

        details.append("Facteurs analysés:\n");
        for (var entry : score.getFactors().entrySet()) {
            details.append("• ").append(entry.getKey()).append(": ")
                    .append(String.format("%.0f%%", entry.getValue() * 100)).append("\n");
        }

        details.append("\n✅ Forces:\n");
        if (score.getStrengths().isEmpty()) {
            details.append("• Aucune force particulière\n");
        } else {
            for (String s : score.getStrengths()) {
                details.append("• ").append(s).append("\n");
            }
        }

        details.append("\n⚠️ Points d'attention:\n");
        if (score.getWeaknesses().isEmpty()) {
            details.append("• Aucun point faible\n");
        } else {
            for (String w : score.getWeaknesses()) {
                details.append("• ").append(w).append("\n");
            }
        }

        // Utiliser DialogUtil.confirm pour afficher (car info n'existe pas)
        DialogUtil.confirm("Analyse IA Détail", details.toString());
    }

    /* ================= CARD ================= */

    private VBox createRequestCard(LoanRequest request){

        User borrower = userService.getById(request.getBorrowerId());

        /* ---- borrower ---- */
        Label name = new Label(borrower.getFullname());
        name.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        /* ---- amount ---- */
        Label amount = new Label(request.getAmount() + " TND");
        amount.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#2ecc71;");

        /* ---- message ---- */
        Label message = new Label("Message: " + request.getMessage());
        message.setStyle("-fx-text-fill:#555;");

        /* ---- date ---- */
        Label date = new Label("Requested at: " + request.getCreatedAt());

        /* ---- buttons ---- */

        Button accept = new Button("✅ Accept");
        accept.setStyle("-fx-background-color:#27ae60; -fx-text-fill:white; -fx-font-weight:bold;");

        Button reject = new Button("❌ Reject");
        reject.setStyle("-fx-background-color:#e74c3c; -fx-text-fill:white; -fx-font-weight:bold;");

        // IA Button
        Button analyzeBtn = new Button("🤖 IA");
        analyzeBtn.setStyle("-fx-background-color:#9b59b6; -fx-text-fill:white; -fx-font-weight:bold;");

        accept.setOnAction(e -> acceptRequest(request));
        reject.setOnAction(e -> rejectRequest(request));

        analyzeBtn.setOnAction(e -> {
            try {
                ICreditScoringService.TrustScore score =
                        creditScoringService.calculateTrustScore(
                                request.getBorrowerId(),
                                currentUserId
                        );
                showIADetails(request, score);
            } catch (Exception ex) {
                DialogUtil.confirm("Erreur IA", ex.getMessage());
            }
        });

        HBox actions = new HBox(10, accept, reject, analyzeBtn);

        /* ---- card ---- */

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding:18;" +
                        "-fx-background-radius:12;" +
                        "-fx-border-radius:12;" +
                        "-fx-border-color:#e0e0e0;" +
                        "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 12,0,0,3);"
        );

        card.getChildren().addAll(name, amount, message, date, actions);

        return card;
    }

    /* ================= ACCEPT ================= */

    private void acceptRequest(LoanRequest request){

        boolean confirmed = DialogUtil.confirm(
                "Accept Loan Request",
                "You are about to ACCEPT this loan request.\n\n"
                        + "Amount: " + request.getAmount() + " TND\n\n"
                        + "A real loan will be created and the borrower will owe you money.\n"
                        + "Do you want to continue?"
        );

        if(!confirmed) return;

        try{


            requestService.acceptRequest(request.getId());

            DialogUtil.success(
                    "Loan Created",
                    "The loan has been successfully created."
            );

            loadRequests();
            loadIAInsights();

        }catch(Exception e){
            e.printStackTrace();
            DialogUtil.error("Accept Failed",
                    "Unable to accept the loan request.\n" + e.getMessage());
        }
    }

    /* ================= REJECT ================= */
    private void rejectRequest(LoanRequest request){

    private void rejectRequest(LoanRequest request){

        boolean confirmed = DialogUtil.confirm(
                "Reject Loan Request",
                "Are you sure you want to reject this loan request?"
        );

        if(!confirmed) return;

        try{

            requestService.rejectRequest(request.getId());

            DialogUtil.success(
                    "Request Rejected",
                    "The loan request has been rejected."
            );

            loadRequests();
            loadIAInsights();

        }catch(Exception e){
            e.printStackTrace();
            DialogUtil.error("Reject Failed",
                    "Unable to reject the loan request.\n" + e.getMessage());
        }
    }
}