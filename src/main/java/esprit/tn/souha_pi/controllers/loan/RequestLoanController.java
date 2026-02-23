package esprit.tn.souha_pi.controllers.loan;

import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.services.LoanRequestService;
import esprit.tn.souha_pi.services.UserService;
import esprit.tn.souha_pi.services.ia.ICreditScoringService;
import esprit.tn.souha_pi.services.ia.impl.CreditScoringService;

import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class RequestLoanController {

    @FXML private ComboBox<User> userCombo;
    @FXML private TextField amountField;
    @FXML private TextArea messageField;
    @FXML private Label statusLabel;

    // IA Components
    @FXML private VBox iaAnalysisBox;
    @FXML private Label iaProbabilityLabel;
    @FXML private ProgressBar iaProbabilityBar;
    @FXML private Label iaRecommendationLabel;
    @FXML private Label iaSuggestionLabel;
    @FXML private CheckBox iaOptimizeCheckbox;

    private UserService userService = new UserService();
    private LoanRequestService requestService = new LoanRequestService();

    // IA Service
    private ICreditScoringService creditScoringService = new CreditScoringService();

    private int currentUserId = 1; // ⚠️ remplacer plus tard par user connecté

    // IA - Stocker l'analyse courante
    private ICreditScoringService.RepaymentProbability currentProbability;
    private double currentAmount = 0;
    private User currentLender = null;

    @FXML
    public void initialize(){

        List<User> users = userService.getAll();
        userCombo.getItems().addAll(users);

        userCombo.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(User u, boolean empty){
                super.updateItem(u, empty);
                setText(empty ? "" : u.getFullname());
            }
        });

        userCombo.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(User u, boolean empty){
                super.updateItem(u, empty);
                setText(empty ? "" : u.getFullname());
            }
        });

        // IA - Listeners
        userCombo.valueProperty().addListener((obs, old, newVal) -> {
            currentLender = newVal;
            analyzeWithIA();
        });

        amountField.textProperty().addListener((obs, old, newVal) -> {
            try {
                currentAmount = Double.parseDouble(newVal);
                analyzeWithIA();
            } catch (NumberFormatException e) {
                // Ignorer
            }
        });

        // IA - Initialiser la boîte IA
        if (iaAnalysisBox != null) {
            iaAnalysisBox.setVisible(false);
        }
    }

    // IA - Analyser avec IA
    private void analyzeWithIA() {
        if (iaAnalysisBox == null) return;

        if (currentLender == null || currentAmount <= 0) {
            iaAnalysisBox.setVisible(false);
            return;
        }

        try {
            // Prédire probabilité de remboursement
            currentProbability = creditScoringService.predictRepayment(
                    currentUserId,
                    currentAmount,
                    30 // jours par défaut
            );

            iaAnalysisBox.setVisible(true);

            // Afficher probabilité
            double probPercent = currentProbability.getProbability() * 100;
            iaProbabilityLabel.setText(String.format("Probabilité: %.0f%%", probPercent));
            iaProbabilityBar.setProgress(currentProbability.getProbability());

            // Couleur selon probabilité
            if (currentProbability.getProbability() > 0.7) {
                iaProbabilityLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else if (currentProbability.getProbability() > 0.4) {
                iaProbabilityLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            } else {
                iaProbabilityLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }

            // Niveau
            iaRecommendationLabel.setText("Niveau: " + currentProbability.getLevel());

            // Facteurs
            if (!currentProbability.getFactors().isEmpty()) {
                String firstFactor = currentProbability.getFactors().get(0);
                iaSuggestionLabel.setText("💡 " + firstFactor);
            } else {
                iaSuggestionLabel.setText("💡 Analyse en cours...");
            }

        } catch (Exception e) {
            iaAnalysisBox.setVisible(false);
        }
    }

    @FXML
    private void sendRequest(){

        // -------- 1. Validate borrower --------
        if(userCombo.getValue() == null){
            DialogUtil.error(
                    "Missing Borrower",
                    "Please select a person to request the loan from."
            );
            return;
        }

        User lender = userCombo.getValue();

        // -------- 2. Validate amount --------
        if(amountField.getText().isEmpty()){
            DialogUtil.error(
                    "Missing Amount",
                    "Please enter the loan amount."
            );
            return;
        }

        double amount;

        try{
            amount = Double.parseDouble(amountField.getText());
        }catch(NumberFormatException e){
            DialogUtil.error(
                    "Invalid Amount",
                    "Please enter a valid numeric amount."
            );
            return;
        }

        if(amount <= 0){
            DialogUtil.error(
                    "Invalid Amount",
                    "Loan amount must be greater than 0."
            );
            return;
        }

        // -------- 3. Vérifier que ce n'est pas soi-même --------
        if(lender.getId() == currentUserId){
            DialogUtil.error(
                    "Invalid Operation",
                    "You cannot request a loan from yourself."
            );
            return;
        }

        // IA - Vérifier la probabilité
        ICreditScoringService.RepaymentProbability prob =
                creditScoringService.predictRepayment(currentUserId, amount, 30);

        String warning = "";
        if (prob.getProbability() < 0.3) {
            warning = "\n\n⚠️ ATTENTION: L'IA prédit une faible probabilité de remboursement!\n";
        } else if (prob.getProbability() < 0.5) {
            warning = "\n\n⚠️ PRUDENCE: Probabilité de remboursement moyenne.\n";
        }

        // IA - Optimisation si cochée
        double finalAmount = amount;
        if (iaOptimizeCheckbox != null && iaOptimizeCheckbox.isSelected() && prob.getProbability() < 0.6) {

            try {
                // Créer demande temporaire pour suggestions (sans duration)
                LoanRequest tempRequest = new LoanRequest();
                tempRequest.setBorrowerId(currentUserId);
                tempRequest.setLenderId(lender.getId());
                tempRequest.setAmount(amount);
                tempRequest.setMessage("TEMP");

                // Obtenir score de confiance
                ICreditScoringService.TrustScore trustScore =
                        creditScoringService.calculateTrustScore(currentUserId, lender.getId());

                // Obtenir termes suggérés
                ICreditScoringService.SuggestedTerms terms =
                        creditScoringService.suggestTerms(tempRequest, trustScore.getScore());

                boolean useOptimized = DialogUtil.confirm(
                        "Optimisation IA",
                        "L'IA suggère d'optimiser votre demande:\n\n" +
                                "• Montant recommandé: " + String.format("%.2f", terms.getRecommendedAmount()) + " TND\n" +
                                (terms.getWarning() != null ? "\n⚠️ " + terms.getWarning() + "\n" : "") +
                                "\nVoulez-vous utiliser ce montant optimisé?"
                );

                if (useOptimized) {
                    finalAmount = terms.getRecommendedAmount();
                    amountField.setText(String.format("%.2f", finalAmount));
                }
            } catch (Exception e) {
                // Ignorer l'erreur d'optimisation
            }
        }

        // -------- 4. Confirmation Dialog avec IA --------
        String message = "You are about to send a loan request.\n\n"
                + "Lender: " + lender.getFullname() + "\n"
                + "Amount: " + finalAmount + " TND\n"
                + "IA Analysis: " + String.format("%.0f%%", prob.getProbability() * 100) + " probability\n"
                + "Level: " + prob.getLevel() + warning + "\n"
                + "The lender will receive a notification and may accept it.\n"
                + "Do you want to continue?";

        boolean confirmed = DialogUtil.confirm(
                "Confirm Loan Request",
                message
        );

        if(!confirmed) return;

        // -------- 5. Send request --------
        try{

            String msg = messageField.getText();

            requestService.createRequest(
                    currentUserId,
                    lender.getId(),
                    finalAmount,
                    msg
            );

            DialogUtil.success(
                    "Request Sent",
                    "Your loan request has been sent successfully."
            );

            // reset form
            amountField.clear();
            messageField.clear();
            userCombo.setValue(null);

            // IA - Cacher analyse
            if (iaAnalysisBox != null) {
                iaAnalysisBox.setVisible(false);
            }

        }catch(Exception e){
            e.printStackTrace();
            DialogUtil.error(
                    "Request Failed",
                    "Unable to send the loan request.\n" + e.getMessage()
            );
        }
    }

}