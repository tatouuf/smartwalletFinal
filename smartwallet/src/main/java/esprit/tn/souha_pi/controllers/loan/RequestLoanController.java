package esprit.tn.souha_pi.controllers.loan;

import entities.User; // IMPORTANT → on utilise TON user du login/session
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

    // ================= UI =================
    @FXML private ComboBox<User> userCombo;
    @FXML private TextField amountField;
    @FXML private TextArea messageField;
    @FXML private Label statusLabel;

    // IA UI
    @FXML private VBox iaAnalysisBox;
    @FXML private Label iaProbabilityLabel;
    @FXML private ProgressBar iaProbabilityBar;
    @FXML private Label iaRecommendationLabel;
    @FXML private Label iaSuggestionLabel;
    @FXML private CheckBox iaOptimizeCheckbox;

    // ================= SERVICES =================
    private final ServiceUser userService = new ServiceUser();
    private final LoanRequestService requestService = new LoanRequestService();
    private final ICreditScoringService creditScoringService = new CreditScoringService();

    // ================= SESSION USER =================
    private User currentUser;
    private int currentUserId;

    // ================= IA STATE =================
    private ICreditScoringService.RepaymentProbability currentProbability;
    private double currentAmount = 0;
    private User currentLender = null;

    // =========================================================
    // INITIALIZE
    // =========================================================
    @FXML
    public void initialize() {

        // récupérer utilisateur connecté
        currentUser = Session.getCurrentUser();

        if(currentUser == null){
            DialogUtil.error("Session Error","User not logged in.");
            return;
        }

        currentUserId = currentUser.getId();

        // charger seulement les vrais utilisateurs (pas admin + pas soi-même)
        try {
            List<User> users = userService.recupererUsersOnly();
            users.removeIf(u -> u.getId() == currentUserId);
            userCombo.getItems().setAll(users);
        } catch (SQLException e) {
            DialogUtil.error("Database Error", e.getMessage());
        }

        // affichage correct dans la ComboBox
        userCombo.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? "" : u.getNom() + " " + u.getPrenom());
            }
        });

        userCombo.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? "" : u.getNom() + " " + u.getPrenom());
            }
        });

        // IA listeners
        userCombo.valueProperty().addListener((obs, old, newVal) -> {
            currentLender = newVal;
            analyzeWithIA();
        });

        amountField.textProperty().addListener((obs, old, newVal) -> {
            try{
                currentAmount = Double.parseDouble(newVal);
                analyzeWithIA();
            }catch(Exception ignored){}
        });

        if(iaAnalysisBox != null)
            iaAnalysisBox.setVisible(false);
    }

    // =========================================================
    // IA ANALYSIS
    // =========================================================
    private void analyzeWithIA(){

        if(iaAnalysisBox == null) return;

        if(currentLender == null || currentAmount <= 0){
            iaAnalysisBox.setVisible(false);
            return;
        }

        try{

            currentProbability = creditScoringService.predictRepayment(
                    currentUserId,
                    currentAmount,
                    30
            );

            iaAnalysisBox.setVisible(true);

            double prob = currentProbability.getProbability();
            double percent = prob * 100;

            iaProbabilityLabel.setText(String.format("Probabilité: %.0f%%", percent));
            iaProbabilityBar.setProgress(prob);

            // couleur
            if(prob > 0.7)
                iaProbabilityLabel.setStyle("-fx-text-fill:#27ae60; -fx-font-weight:bold;");
            else if(prob > 0.4)
                iaProbabilityLabel.setStyle("-fx-text-fill:#f39c12; -fx-font-weight:bold;");
            else
                iaProbabilityLabel.setStyle("-fx-text-fill:#e74c3c; -fx-font-weight:bold;");

            iaRecommendationLabel.setText("Niveau: " + currentProbability.getLevel());

            if(!currentProbability.getFactors().isEmpty())
                iaSuggestionLabel.setText("💡 " + currentProbability.getFactors().get(0));
            else
                iaSuggestionLabel.setText("💡 Analyse en cours...");

        }catch(Exception e){
            iaAnalysisBox.setVisible(false);
        }
    }

    // =========================================================
    // SEND REQUEST
    // =========================================================
    @FXML
    private void sendRequest(){

        if(userCombo.getValue() == null){
            DialogUtil.error("Missing Borrower","Please select a person.");
            return;
        }

        User lender = userCombo.getValue();

        double amount;

        try{
            amount = Double.parseDouble(amountField.getText());
            if(amount <= 0) throw new Exception();
        }catch(Exception e){
            DialogUtil.error("Invalid Amount","Enter a valid positive amount.");
            return;
        }

        // analyse IA avant confirmation
        ICreditScoringService.RepaymentProbability prob =
                creditScoringService.predictRepayment(currentUserId, amount, 30);

        String warning = "";
        if (prob.getProbability() < 0.3)
            warning = "\n⚠️ Risque très élevé de non remboursement";
        else if (prob.getProbability() < 0.5)
            warning = "\n⚠️ Risque moyen";

        // optimisation IA
        double finalAmount = amount;

        if(iaOptimizeCheckbox != null && iaOptimizeCheckbox.isSelected() && prob.getProbability() < 0.6){

            try{

                LoanRequest temp = new LoanRequest();
                temp.setBorrowerId(currentUserId);
                temp.setLenderId(lender.getId());
                temp.setAmount(amount);

                ICreditScoringService.TrustScore trust =
                        creditScoringService.calculateTrustScore(currentUserId, lender.getId());

                ICreditScoringService.SuggestedTerms terms =
                        creditScoringService.suggestTerms(temp, trust.getScore());

                boolean accept = DialogUtil.confirm(
                        "Optimisation IA",
                        "Montant recommandé: "+String.format("%.2f",terms.getRecommendedAmount())+" TND\n\nUtiliser?"
                );

                if(accept){
                    finalAmount = terms.getRecommendedAmount();
                    amountField.setText(String.format("%.2f", finalAmount));
                }

            }catch(Exception ignored){}
        }

        // confirmation
        boolean confirmed = DialogUtil.confirm(
                "Confirm Loan",
                "Lender: "+lender.getNom()+
                        "\nAmount: "+finalAmount+" TND"+
                        "\nProbabilité: "+String.format("%.0f%%",prob.getProbability()*100)+
                        warning+
                        "\n\nContinue?"
        );

        if(!confirmed) return;

        // envoi
        try{

            requestService.createRequest(
                    currentUserId,
                    lender.getId(),
                    finalAmount,
                    messageField.getText()
            );

            DialogUtil.success("Success","Loan request sent!");

            amountField.clear();
            messageField.clear();
            userCombo.setValue(null);
            iaAnalysisBox.setVisible(false);

        }catch(Exception e){
            DialogUtil.error("Error", e.getMessage());
        }
    }
}