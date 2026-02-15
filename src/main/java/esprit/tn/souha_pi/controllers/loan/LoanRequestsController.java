package esprit.tn.souha_pi.controllers.loan;

import esprit.tn.souha_pi.entities.LoanRequest;
import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.LoanRequestService;
import esprit.tn.souha_pi.services.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import esprit.tn.souha_pi.utils.DialogUtil;

import java.util.List;

public class LoanRequestsController {

    @FXML
    private VBox requestsContainer;

    @FXML
    private Label statusLabel;

    private LoanRequestService requestService = new LoanRequestService();
    private UserService userService = new UserService();

    // ⚠️ remplacer par user connecté plus tard
    private int currentUserId = 1;

    @FXML
    public void initialize(){
        loadRequests();
    }

    /* ================= LOAD ================= */

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

        accept.setOnAction(e -> acceptRequest(request));
        reject.setOnAction(e -> rejectRequest(request));

        HBox actions = new HBox(10, accept, reject);

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

        }catch(Exception e){
            e.printStackTrace();
            DialogUtil.error("Accept Failed",
                    "Unable to accept the loan request.\n" + e.getMessage());
        }
    }

    /* ================= REJECT ================= */

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

        }catch(Exception e){
            e.printStackTrace();
            DialogUtil.error("Reject Failed",
                    "Unable to reject the loan request.\n" + e.getMessage());
        }
    }
}
