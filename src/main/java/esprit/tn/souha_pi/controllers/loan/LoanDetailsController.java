package esprit.tn.souha_pi.controllers.loan;

<<<<<<< HEAD
import entities.User;
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import esprit.tn.souha_pi.entities.*;
import esprit.tn.souha_pi.services.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import esprit.tn.souha_pi.utils.DialogUtil;
<<<<<<< HEAD
import services.ServiceUser;
import utils.Session;

import java.sql.SQLException;
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import java.util.List;

public class LoanDetailsController {

    @FXML private Label lenderLabel;
    @FXML private Label borrowerLabel;
    @FXML private Label amountLabel;
    @FXML private Label remainingLabel;
    @FXML private Label statusLabel;
    @FXML private TextField amountField;

    @FXML private VBox paymentsContainer;

    private LoanService loanService = new LoanService();
    private LoanPaymentService paymentService = new LoanPaymentService();
<<<<<<< HEAD

    // CORRECTION: Utilisez le même nom de variable partout
    private ServiceUser userService = new ServiceUser();  // Renommé de ServiceUser à userService

    private Loan currentLoan;
    private User currentUser = Session.getCurrentUser();
    private int currentUserId = currentUser != null ? currentUser.getId() : 0;
=======
    private UserService userService = new UserService();

    private Loan currentLoan;
    private int currentUserId = 1;
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

    /* ================= LOAD LOAN ================= */
    public static boolean confirm(String title, String message){

        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        javafx.scene.control.ButtonType yes =
                new javafx.scene.control.ButtonType("Confirm", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);

        javafx.scene.control.ButtonType cancel =
                new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yes, cancel);

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == yes;
    }

    public void loadLoan(int loanId){
        try{

            currentLoan = loanService.getById(loanId);

            if(currentLoan == null){
                DialogUtil.error("Loan Not Found",
                        "The requested loan does not exist anymore.");
                return;
            }

            refresh();

        }catch(Exception e){
            e.printStackTrace();
            DialogUtil.error("System Error",
                    "Unable to load the loan details.\n" + e.getMessage());
        }
    }

<<<<<<< HEAD
    /* ================= REFRESH ================= */
    private void refresh(){
        try {
            // CORRECTION: Utilisez userService au lieu de ServiceUser
=======

    /* ================= REFRESH ================= */

    private void refresh(){

        try {

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            User lender = userService.getById(currentLoan.getLenderId());
            User borrower = userService.getById(currentLoan.getBorrowerId());

            if(lender == null || borrower == null){
                DialogUtil.error("Data Error",
                        "The lender or borrower account no longer exists.");
                return;
            }

            lenderLabel.setText("Lender: " + lender.getFullname());
            borrowerLabel.setText("Borrower: " + borrower.getFullname());
            amountLabel.setText("Total Amount: " + currentLoan.getPrincipalAmount() + " TND");
            remainingLabel.setText("Remaining: " + currentLoan.getRemainingAmount() + " TND");

<<<<<<< HEAD
            // Mettre à jour le statut si présent
            if (statusLabel != null) {
                statusLabel.setText("Status: " + currentLoan.getStatus());
            }

=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            loadPaymentCards();

        }catch(Exception e){
            DialogUtil.error("Refresh Error",
<<<<<<< HEAD
                    "Unable to refresh loan information.\n" + e.getMessage());
            e.printStackTrace();
=======
                    "Unable to refresh loan information.");
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        }
    }

    /* ================= PAYMENT CARDS ================= */
<<<<<<< HEAD
    private void loadPaymentCards(){
        paymentsContainer.getChildren().clear();

        try{
=======

    private void loadPaymentCards(){

        paymentsContainer.getChildren().clear();

        try{

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            List<LoanPayment> payments = paymentService.getByLoan(currentLoan.getId());

            if(payments == null || payments.isEmpty()){
                Label empty = new Label("No payments yet");
                empty.setStyle("-fx-text-fill:gray; -fx-font-size:14px;");
                paymentsContainer.getChildren().add(empty);
                return;
            }

            for(LoanPayment payment : payments){
                VBox card = createPaymentCard(payment);
                paymentsContainer.getChildren().add(card);
            }

        }catch(Exception e){
            DialogUtil.error("Payment Error",
<<<<<<< HEAD
                    "Unable to load payment history.\n" + e.getMessage());
=======
                    "Unable to load payment history.");
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        }
    }

    /* ================= CARD UI ================= */
<<<<<<< HEAD
    private VBox createPaymentCard(LoanPayment payment) throws SQLException {
        // CORRECTION: Utilisez userService au lieu de ServiceUser
        User payer = userService.getById(payment.getPayerId());
        User receiver = userService.getById(payment.getReceiverId());

        String payerName = (payer != null) ? payer.getFullname() : "Unknown User";
        String receiverName = (receiver != null) ? receiver.getFullname() : "Unknown User";

        Label amount = new Label(payment.getAmountPaid() + " TND");
        amount.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#2ecc71;");

        Label payerLabel = new Label("From: " + payerName);
        Label receiverLabel = new Label("To: " + receiverName);
=======

    private VBox createPaymentCard(LoanPayment payment){

        User payer = userService.getById(payment.getPayerId());
        User receiver = userService.getById(payment.getReceiverId());

        Label amount = new Label(payment.getAmountPaid() + " TND");
        amount.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#2ecc71;");

        Label payerLabel = new Label("From: " + payer.getFullname());
        Label receiverLabel = new Label("To: " + receiver.getFullname());
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
        Label date = new Label("Date: " + payment.getPaymentDate());

        VBox info = new VBox(5, payerLabel, receiverLabel, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, amount, spacer);

        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding:15;" +
                        "-fx-background-radius:12;" +
                        "-fx-border-radius:12;" +
                        "-fx-border-color:#e0e0e0;" +
                        "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 10,0,0,3);"
        );

        card.getChildren().addAll(header, info);

        return card;
    }

    /* ================= PAY LOAN ================= */
<<<<<<< HEAD
    @FXML
    private void payLoan(){
        try{
            /* ---------- INPUT VALIDATION ---------- */
=======

    @FXML
    private void payLoan(){

        try{

            /* ---------- INPUT VALIDATION ---------- */

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            String text = amountField.getText();

            if(text == null || text.isBlank()){
                DialogUtil.error("Invalid Amount", "Please enter an amount.");
                return;
            }

            double amount;
<<<<<<< HEAD
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            try{
                amount = Double.parseDouble(text);
            }catch(NumberFormatException ex){
                DialogUtil.error("Invalid Amount", "Please enter a valid number.");
                return;
            }

            if(amount <= 0){
                DialogUtil.error("Invalid Amount", "Amount must be greater than 0.");
                return;
            }

            if(amount > currentLoan.getRemainingAmount()){
                DialogUtil.error("Invalid Amount",
                        "You cannot pay more than the remaining amount (" +
                                currentLoan.getRemainingAmount() + " TND).");
                return;
            }

            /* ---------- CONFIRMATION DIALOG ---------- */
<<<<<<< HEAD
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            boolean confirmed = DialogUtil.confirm(
                    "Confirm Payment",
                    "You are about to pay " + amount + " TND for this loan.\n\n" +
                            "Remaining after payment: " + (currentLoan.getRemainingAmount() - amount) + " TND\n\n" +
                            "Do you want to continue?"
            );

            if(!confirmed){
                return;
            }

            /* ---------- PROCESS PAYMENT ---------- */
<<<<<<< HEAD
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            loanService.payLoan(currentLoan.getId(), currentUserId, amount);

            DialogUtil.success("Payment Completed",
                    "Payment of " + amount + " TND was successfully processed.");

            amountField.clear();
<<<<<<< HEAD
=======

>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
            loadLoan(currentLoan.getId());

        }catch(Exception e){
            DialogUtil.error("Payment Failed", e.getMessage());
        }
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
