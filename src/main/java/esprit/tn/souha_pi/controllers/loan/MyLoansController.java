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
import utils.Session;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class MyLoansController {

    @FXML
    private VBox cardsContainer;
    @FXML
    private Label totalBorrowedLabel;
    @FXML
    private Label totalLentLabel;

    private LoanService loanService = new LoanService();
    private ServiceUser userService = new ServiceUser();
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    private User currentUser = Session.getCurrentUser();
    private int currentUserId = currentUser != null ? currentUser.getId() : 0;

    @FXML
    public void initialize(){
        if (currentUser != null) {
            try {
                loadLoans();
                calculateTotals();
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
        Label error = new Label("❌ " + message);
        error.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-padding: 20; -fx-alignment: center;");
        error.setWrapText(true);
        cardsContainer.getChildren().add(error);
    }

    /* ================= CALCUL DES STATISTIQUES ================= */
    private void calculateTotals() throws SQLException {
        List<Loan> loans = loanService.getLoansForUser(currentUserId);

        double totalBorrowed = 0;
        double totalLent = 0;

        for (Loan loan : loans) {
            if (loan.getBorrowerId() == currentUserId) {
                totalBorrowed += loan.getRemainingAmount();
            } else {
                totalLent += loan.getRemainingAmount();
            }
        }

        if (totalBorrowedLabel != null) {
            totalBorrowedLabel.setText(df.format(totalBorrowed) + " TND");
        }

        if (totalLentLabel != null) {
            totalLentLabel.setText(df.format(totalLent) + " TND");
        }
    }

    /* ================= LOAD LOANS ================= */
    private void loadLoans() throws SQLException {
        cardsContainer.getChildren().clear();

        List<Loan> loans = loanService.getLoansForUser(currentUserId);

        if(loans == null || loans.isEmpty()){
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setStyle("-fx-padding: 40;");

            Label icon = new Label("📭");
            icon.setStyle("-fx-font-size: 48px;");

            Label empty = new Label("Aucun prêt trouvé");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");

            Label suggestion = new Label("Commencez par demander un prêt ou en accorder un");
            suggestion.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");

            emptyBox.getChildren().addAll(icon, empty, suggestion);
            cardsContainer.getChildren().add(emptyBox);
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
        String otherUserEmail = "";
        if (otherUser != null) {
            otherUserName = otherUser.getPrenom() + " " + otherUser.getNom();
            otherUserEmail = otherUser.getEmail();
        }

        /* ===== ROLE ===== */
        Label roleLabel = new Label(isBorrower ? "📤 Vous avez emprunté" : "📥 Vous avez prêté");
        roleLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: " + (isBorrower ? "#e74c3c" : "#2ecc71") + ";" +
                        "-fx-padding: 5 12;" +
                        "-fx-background-radius: 20;"
        );

        /* ===== USER INFO ===== */
        Label nameLabel = new Label(otherUserName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label emailLabel = new Label(otherUserEmail);
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        /* ===== AMOUNTS ===== */
        HBox amountsBox = new HBox(20);

        Label principalLabel = new Label("💰 Total: " + df.format(loan.getPrincipalAmount()) + " TND");
        principalLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155;");

        Label remainingLabel = new Label("⏳ Restant: " + df.format(loan.getRemainingAmount()) + " TND");
        remainingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #c0392b;");

        amountsBox.getChildren().addAll(principalLabel, remainingLabel);

        /* ===== STATUS ===== */
        Label statusLabel = new Label(loan.getStatus());
        statusLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 10;" +
                        "-fx-background-radius: 15;" +
                        "-fx-background-color: " + (loan.getStatus().equals("PAID") ? "#27ae60" : "#f39c12")
        );

        /* ===== HEADER ===== */
        HBox header = new HBox(10, roleLabel, statusLabel);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        /* ===== CARD ===== */
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 18;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        card.getChildren().addAll(header, nameLabel, emailLabel, amountsBox);

        /* ===== PROGRESS BAR (optionnel) ===== */
        if (!loan.getStatus().equals("PAID") && loan.getPrincipalAmount() > 0) {
            double progress = (loan.getPrincipalAmount() - loan.getRemainingAmount()) / loan.getPrincipalAmount();
            Label progressLabel = new Label(String.format("Progression: %.0f%%", progress * 100));
            progressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4f46e5;");
            card.getChildren().add(progressLabel);
        }

        /* ===== CLICK ACTION ===== */
        card.setOnMouseClicked(e -> {
            card.setStyle(card.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(79,70,229,0.3), 15, 0, 0, 3);");
            WalletLayoutController.instance.openLoanDetails(loan.getId());
        });

        /* ===== HOVER EFFECT ===== */
        String baseStyle = card.getStyle();
        card.setOnMouseEntered(e ->
                card.setStyle(
                        "-fx-background-color: #f8fafc;" +
                                "-fx-padding: 18;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-radius: 12;" +
                                "-fx-border-color: #4f46e5;" +
                                "-fx-effect: dropshadow(gaussian, rgba(79,70,229,0.15), 15, 0, 0, 5);"
                )
        );

        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        return card;
    }

    /* ================= NAVIGATION ================= */
    @FXML
    private void goBack() {
        WalletLayoutController.instance.goDashboard();
    }
}