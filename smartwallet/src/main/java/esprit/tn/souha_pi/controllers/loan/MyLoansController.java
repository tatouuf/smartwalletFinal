package esprit.tn.souha_pi.controllers.loan;

import entities.User;
import esprit.tn.souha_pi.controllers.WalletLayoutController;
import esprit.tn.souha_pi.entities.Loan;
import esprit.tn.souha_pi.services.LoanService;
import services.ServiceUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import utils.Session;

import java.sql.SQLException;
import java.util.List;

public class MyLoansController {

    @FXML
    private VBox cardsContainer;

    private LoanService loanService = new LoanService();
    private ServiceUser userService = new ServiceUser();

    private User currentUser = Session.getCurrentUser();
    private int currentUserId = currentUser != null ? currentUser.getId() : 0;

    @FXML
    public void initialize(){
        if (currentUser != null) {
            try {
                loadLoans();
            } catch (SQLException e) {
                afficherErreur("Erreur de chargement des prêts: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            afficherErreur("Veuillez vous connecter pour voir vos prêts");
        }
    }

    private void afficherErreur(String message) {
        cardsContainer.getChildren().clear();
        Label error = new Label(message);
        error.setStyle("-fx-text-fill:red; -fx-font-size:14px;");
        cardsContainer.getChildren().add(error);
    }

    /* ================= LOAD LOANS ================= */
    private void loadLoans() throws SQLException {
        cardsContainer.getChildren().clear();

        List<Loan> loans = loanService.getLoansForUser(currentUserId);

        if(loans == null || loans.isEmpty()){
            Label empty = new Label("Aucun prêt trouvé");
            empty.setStyle("-fx-text-fill:gray; -fx-font-size:16px;");
            cardsContainer.getChildren().add(empty);
            return;
        }

        for(Loan loan : loans){
            VBox card = createLoanCard(loan);
            cardsContainer.getChildren().add(card);
        }
    }

    /* ================= CARD CREATION ================= */
    private VBox createLoanCard(Loan loan){
        boolean isBorrower = loan.getBorrowerId() == currentUserId;
        int otherUserId = isBorrower ? loan.getLenderId() : loan.getBorrowerId();

        // Gérer le cas où l'autre utilisateur pourrait être null
        User otherUser = null;
        try {
            otherUser = userService.getById(otherUserId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement de l'utilisateur: " + e.getMessage());
        }

        String otherUserName = "Utilisateur inconnu";
        if (otherUser != null) {
            otherUserName = otherUser.getPrenom() + " " + otherUser.getNom();
        }

        /* ===== ROLE ===== */
        Label roleLabel = new Label(isBorrower ? "Vous avez emprunté" : "Vous avez prêté");
        roleLabel.setStyle(
                "-fx-font-size:14px;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-color:" + (isBorrower ? "#e74c3c" : "#2ecc71") + ";" +
                        "-fx-padding:5 12;" +
                        "-fx-background-radius:20;"
        );

        /* ===== USER NAME ===== */
        Label nameLabel = new Label(otherUserName);
        nameLabel.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        /* ===== AMOUNT ===== */
        Label amountLabel = new Label("Total: " + loan.getPrincipalAmount() + " TND");
        amountLabel.setStyle("-fx-font-size:15px;");

        /* ===== REMAINING ===== */
        Label remainingLabel = new Label("Restant: " + loan.getRemainingAmount() + " TND");
        remainingLabel.setStyle("-fx-font-size:15px; -fx-text-fill:#c0392b;");

        /* ===== STATUS ===== */
        Label statusLabel = new Label(loan.getStatus());
        statusLabel.setStyle(
                "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;" +
                        "-fx-padding:4 10;" +
                        "-fx-background-radius:15;" +
                        "-fx-background-color:" + (loan.getStatus().equals("PAID") ? "#27ae60" : "#f39c12")
        );

        /* ===== HEADER ===== */
        HBox header = new HBox(10, roleLabel, statusLabel);

        /* ===== CARD ===== */
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding:18;" +
                        "-fx-background-radius:12;" +
                        "-fx-border-radius:12;" +
                        "-fx-border-color:#e0e0e0;" +
                        "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 10,0,0,3);"
        );

        card.getChildren().addAll(header, nameLabel, amountLabel, remainingLabel);

        /* ===== CLICK ACTION ===== */
        card.setOnMouseClicked(e ->
                WalletLayoutController.instance.openLoanDetails(loan.getId())
        );

        /* ===== HOVER EFFECT ===== */
        card.setOnMouseEntered(e ->
                card.setStyle(
                        "-fx-background-color:#f8f9fa;" +
                                "-fx-padding:18;" +
                                "-fx-background-radius:12;" +
                                "-fx-border-radius:12;" +
                                "-fx-border-color:#d0d0d0;" +
                                "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.18), 15,0,0,5);"
                )
        );

        card.setOnMouseExited(e ->
                card.setStyle(
                        "-fx-background-color:white;" +
                                "-fx-padding:18;" +
                                "-fx-background-radius:12;" +
                                "-fx-border-radius:12;" +
                                "-fx-border-color:#e0e0e0;" +
                                "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 10,0,0,3);"
                )
        );

        return card;
    }
}