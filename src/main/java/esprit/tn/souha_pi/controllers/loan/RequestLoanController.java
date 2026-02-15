package esprit.tn.souha_pi.controllers.loan;

import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.LoanRequestService;
import esprit.tn.souha_pi.services.UserService;

import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RequestLoanController {

    @FXML private ComboBox<User> userCombo;
    @FXML private TextField amountField;
    @FXML private TextArea messageField;
    @FXML private Label statusLabel;

    private UserService userService = new UserService();
    private LoanRequestService requestService = new LoanRequestService();

    private int currentUserId = 1; // ⚠️ remplacer plus tard par user connecté

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

        // -------- 3. Confirmation Dialog --------
        boolean confirmed = DialogUtil.confirm(
                "Confirm Loan Request",
                "You are about to send a loan request.\n\n"
                        + "Lender: " + lender.getFullname() + "\n"
                        + "Amount: " + amount + " TND\n\n"
                        + "The lender will receive a notification and may accept it.\n"
                        + "Do you want to continue?"
        );

        if(!confirmed) return;
      /*  if(lender.getId() == currentUserId){
            DialogUtil.error(
                    "Invalid Operation",
                    "You cannot request a loan from yourself."
            );
            return;
        }*/

        // -------- 4. Send request --------
        try{

            String message = messageField.getText();

            requestService.createRequest(
                    currentUserId,
                    lender.getId(),
                    amount,
                    message
            );

            DialogUtil.success(
                    "Request Sent",
                    "Your loan request has been sent successfully."
            );

            // reset form
            amountField.clear();
            messageField.clear();
            userCombo.setValue(null);

        }catch(Exception e){
            e.printStackTrace();
            DialogUtil.error(
                    "Request Failed",
                    "Unable to send the loan request.\n" + e.getMessage()
            );
        }
    }

}
