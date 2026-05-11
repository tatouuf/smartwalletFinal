package esprit.tn.souha_pi.controllers.loan;

import entities.User;
import esprit.tn.souha_pi.entities.Loan;
import esprit.tn.souha_pi.services.LoanService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import services.ServiceUser;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.util.List;

public class MyLoansController {

    @FXML private VBox cardsContainer;

    private LoanService loanService = new LoanService();
    private ServiceUser userService = new ServiceUser();

    private User currentUser = Session.getCurrentUser();
    private int currentUserId = currentUser != null ? currentUser.getId() : 0;

    @FXML
    public void initialize() {
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

    private void loadLoans() throws SQLException {
        cardsContainer.getChildren().clear();

        List<Loan> loans = loanService.getLoansForUser(currentUserId);

        if (loans == null || loans.isEmpty()) {
            Label empty = new Label("Aucun prêt trouvé");
            empty.setStyle("-fx-text-fill:gray; -fx-font-size:16px;");
            cardsContainer.getChildren().add(empty);
            return;
        }

        for (Loan loan : loans) {
            VBox card = createLoanCard(loan);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createLoanCard(Loan loan) {
        boolean isBorrower = loan.getBorrowerId() == currentUserId;
        int otherUserId = isBorrower ? loan.getLenderId() : loan.getBorrowerId();

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

        String roleColor = isBorrower ? "#ef4444" : "#22c55e";
        String roleBgSoft = isBorrower ? "#fef2f2" : "#f0fdf4";
        String roleBorder = isBorrower ? "#fecaca" : "#bbf7d0";

        Label roleLabel = new Label(isBorrower ? "👤 Vous avez emprunté" : "💰 Vous avez prêté");
        roleLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: " + roleColor + ";" +
                        "-fx-padding: 7 15;" +
                        "-fx-background-radius: 999;"
        );

        Label statusLabel = new Label(loan.getStatus());
        statusLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 7 14;" +
                        "-fx-background-radius: 999;" +
                        "-fx-background-color:" + (loan.getStatus().equals("PAID") ? "#16a34a" : "#f59e0b") + ";"
        );

        HBox header = new HBox(10, roleLabel, statusLabel);
        header.setStyle("-fx-alignment: center-left;");

        Label nameLabel = new Label(otherUserName);
        nameLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        Label relationLabel = new Label(isBorrower ? "Prêteur / Contact" : "Emprunteur / Contact");
        relationLabel.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #6b7280;" +
                        "-fx-padding: 0 0 4 0;"
        );

        Label amountLabel = new Label(String.format("Total: %.2f TND", loan.getPrincipalAmount()));
        amountLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #4f46e5;" +
                        "-fx-background-color: #eef2ff;" +
                        "-fx-padding: 10 14;" +
                        "-fx-background-radius: 14;"
        );

        Label remainingLabel = new Label(String.format("Restant: %.2f TND", loan.getRemainingAmount()));
        remainingLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill:" + (loan.getRemainingAmount() <= 0 ? "#16a34a" : "#dc2626") + ";" +
                        "-fx-background-color:" + (loan.getRemainingAmount() <= 0 ? "#dcfce7" : "#fee2e2") + ";" +
                        "-fx-padding: 10 14;" +
                        "-fx-background-radius: 14;"
        );

        HBox amountsRow = new HBox(12, amountLabel, remainingLabel);
        amountsRow.setStyle("-fx-alignment: center-left;");

        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 22;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-color: " + roleBorder + ";" +
                        "-fx-border-width: 1.2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.12), 18, 0, 0, 8);" +
                        "-fx-cursor: hand;"
        );

        card.getChildren().addAll(
                header,
                nameLabel,
                relationLabel,
                amountsRow
        );

        card.setOnMouseEntered(e ->
                card.setStyle(
                        "-fx-background-color: " + roleBgSoft + ";" +
                                "-fx-padding: 22;" +
                                "-fx-background-radius: 22;" +
                                "-fx-border-radius: 22;" +
                                "-fx-border-color: " + roleColor + ";" +
                                "-fx-border-width: 1.4;" +
                                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.22), 24, 0, 0, 10);" +
                                "-fx-cursor: hand;" +
                                "-fx-translate-y: -2;"
                )
        );

        card.setOnMouseExited(e ->
                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-padding: 22;" +
                                "-fx-background-radius: 22;" +
                                "-fx-border-radius: 22;" +
                                "-fx-border-color: " + roleBorder + ";" +
                                "-fx-border-width: 1.2;" +
                                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.12), 18, 0, 0, 8);" +
                                "-fx-cursor: hand;" +
                                "-fx-translate-y: 0;"
                )
        );

        card.setOnMouseClicked(e -> {
            LoanDetailsController.selectedLoanId = loan.getId();

            MainFxml.getInstance().openPopup(
                    "/fxml/loan/LoanDetails.fxml",
                    "Détails du prêt",
                    800,
                    600,
                    true
            );
        });
        return card;
    }}