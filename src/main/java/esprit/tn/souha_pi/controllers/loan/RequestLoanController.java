package esprit.tn.souha_pi.controllers.loan;


import entities.User; // IMPORTANT → on utilise TON user du login/session
import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.services.LoanRequestService;
import esprit.tn.souha_pi.services.ia.ICreditScoringService;
import esprit.tn.souha_pi.services.ia.impl.CreditScoringService;
import esprit.tn.souha_pi.utils.DialogUtil;


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

<<<<<<< HEAD
import services.ServiceUser;
import utils.Session;

import java.sql.SQLException;
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import java.util.List;

public class RequestLoanController {

<<<<<<< HEAD
    // ================= UI =================
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    @FXML private ComboBox<User> userCombo;
    @FXML private TextField amountField;
    @FXML private TextArea messageField;
    @FXML private Label statusLabel;

<<<<<<< HEAD
    // IA UI
=======
    // IA Components
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    @FXML private VBox iaAnalysisBox;
    @FXML private Label iaProbabilityLabel;
    @FXML private ProgressBar iaProbabilityBar;
    @FXML private Label iaRecommendationLabel;
    @FXML private Label iaSuggestionLabel;
    @FXML private CheckBox iaOptimizeCheckbox;

<<<<<<< HEAD
    // ================= SERVICES =================
    private final ServiceUser userService = new ServiceUser();
    private final LoanRequestService requestService = new LoanRequestService();
    private final ICreditScoringService creditScoringService = new CreditScoringService();

    // ================= SESSION USER =================
    private User currentUser;
    private int currentUserId;

    // ================= IA STATE =================
=======
    private UserService userService = new UserService();
    private LoanRequestService requestService = new LoanRequestService();

    // IA Service
    private ICreditScoringService creditScoringService = new CreditScoringService();

    private int currentUserId = 1; // ⚠️ remplacer plus tard par user connecté

    // IA - Stocker l'analyse courante
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
    private ICreditScoringService.RepaymentProbability currentProbability;
    private double currentAmount = 0;
    private User currentLender = null;

<<<<<<< HEAD
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
=======
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
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        userCombo.valueProperty().addListener((obs, old, newVal) -> {
            currentLender = newVal;
            analyzeWithIA();
        });

        amountField.textProperty().addListener((obs, old, newVal) -> {
<<<<<<< HEAD
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
=======
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
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            iaAnalysisBox.setVisible(false);
            return;
        }

<<<<<<< HEAD
        try{

            currentProbability = creditScoringService.predictRepayment(
                    currentUserId,
                    currentAmount,
                    30
=======
        try {
            // Prédire probabilité de remboursement
            currentProbability = creditScoringService.predictRepayment(
                    currentUserId,
                    currentAmount,
                    30 // jours par défaut
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            );

            iaAnalysisBox.setVisible(true);

<<<<<<< HEAD
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
=======
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
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            iaAnalysisBox.setVisible(false);
        }
    }

<<<<<<< HEAD
    // =========================================================
    // SEND REQUEST
    // =========================================================
    @FXML
    private void sendRequest(){

        if(userCombo.getValue() == null){
            DialogUtil.error("Missing Borrower","Please select a person.");
=======
    @FXML
    private void sendRequest(){

        // -------- 1. Validate borrower --------
        if(userCombo.getValue() == null){
            DialogUtil.error(
                    "Missing Borrower",
                    "Please select a person to request the loan from."
            );
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            return;
        }

        User lender = userCombo.getValue();

<<<<<<< HEAD
=======
        // -------- 2. Validate amount --------
        if(amountField.getText().isEmpty()){
            DialogUtil.error(
                    "Missing Amount",
                    "Please enter the loan amount."
            );
            return;
        }

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        double amount;

        try{
            amount = Double.parseDouble(amountField.getText());
<<<<<<< HEAD
            if(amount <= 0) throw new Exception();
        }catch(Exception e){
            DialogUtil.error("Invalid Amount","Enter a valid positive amount.");
            return;
        }

        // analyse IA avant confirmation
=======
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
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        ICreditScoringService.RepaymentProbability prob =
                creditScoringService.predictRepayment(currentUserId, amount, 30);

        String warning = "";
<<<<<<< HEAD
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
=======
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
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        );

        if(!confirmed) return;

<<<<<<< HEAD
        // envoi
        try{

=======
        // -------- 5. Send request --------
        try{

            String msg = messageField.getText();

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            requestService.createRequest(
                    currentUserId,
                    lender.getId(),
                    finalAmount,
<<<<<<< HEAD
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
=======
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

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
}