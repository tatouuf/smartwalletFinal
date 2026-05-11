package esprit.tn.souha_pi.controllers.loan;

import entities.User;
import esprit.tn.souha_pi.entities.Loan;
import esprit.tn.souha_pi.entities.LoanPayment;
import esprit.tn.souha_pi.services.LoanPaymentService;
import esprit.tn.souha_pi.services.LoanService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import services.ServiceUser;
import utils.Session;

import java.sql.SQLException;
import java.util.List;

public class LoanDetailsController {

    @FXML private Label lenderLabel;
    @FXML private Label borrowerLabel;
    @FXML private Label amountLabel;
    @FXML private Label remainingLabel;
    @FXML private Label statusLabel;
    @FXML private TextField amountField;
    @FXML private VBox paymentsContainer;
    public static int selectedLoanId;
    private final LoanService loanService = new LoanService();
    private final LoanPaymentService paymentService = new LoanPaymentService();
    private final ServiceUser userService = new ServiceUser();

    private Loan currentLoan;

    private final User currentUser = Session.getCurrentUser();
    private final int currentUserId = currentUser != null ? currentUser.getId() : 0;

    @FXML
    public void initialize() {
        if (selectedLoanId > 0) {
            loadLoan(selectedLoanId);
        }
        if (paymentsContainer != null) {
            paymentsContainer.setSpacing(12);
            paymentsContainer.setPadding(new Insets(10));
        }

        if (amountField != null) {
            amountField.setPromptText("Montant à payer");
            amountField.setStyle(
                    "-fx-background-color: #F8FAFC;" +
                            "-fx-border-color: #E5E7EB;" +
                            "-fx-border-radius: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-padding: 10 14;" +
                            "-fx-font-size: 14px;"
            );
        }
    }

    /* ================= LOAD LOAN ================= */

    public void loadLoan(int loanId) {
        try {
            currentLoan = loanService.getById(loanId);

            if (currentLoan == null) {
                DialogUtil.error(
                        "Prêt introuvable",
                        "Le prêt demandé n'existe plus."
                );
                return;
            }

            refresh();

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error(
                    "Erreur système",
                    "Impossible de charger les détails du prêt.\n" + e.getMessage()
            );
        }
    }

    /* ================= REFRESH ================= */

    private void refresh() {
        if (currentLoan == null) {
            DialogUtil.error("Erreur", "Aucun prêt sélectionné.");
            return;
        }

        try {
            User lender = userService.getById(currentLoan.getLenderId());
            User borrower = userService.getById(currentLoan.getBorrowerId());

            if (lender == null || borrower == null) {
                DialogUtil.error(
                        "Erreur de données",
                        "Le prêteur ou l'emprunteur n'existe plus."
                );
                return;
            }

            setLabelStyle(lenderLabel, "Prêteur", lender.getFullname(), "#4F46E5");
            setLabelStyle(borrowerLabel, "Emprunteur", borrower.getFullname(), "#0F766E");

            if (amountLabel != null) {
                amountLabel.setText(String.format("Montant total : %.2f TND", currentLoan.getPrincipalAmount()));
                amountLabel.setStyle(
                        "-fx-text-fill: #4F46E5;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: 800;" +
                                "-fx-background-color: #EEF2FF;" +
                                "-fx-padding: 10 14;" +
                                "-fx-background-radius: 14;"
                );
            }

            if (remainingLabel != null) {
                String color = currentLoan.getRemainingAmount() <= 0 ? "#16A34A" : "#DC2626";
                String bg = currentLoan.getRemainingAmount() <= 0 ? "#DCFCE7" : "#FEE2E2";

                remainingLabel.setText(String.format("Restant : %.2f TND", currentLoan.getRemainingAmount()));
                remainingLabel.setStyle(
                        "-fx-text-fill: " + color + ";" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: 800;" +
                                "-fx-background-color: " + bg + ";" +
                                "-fx-padding: 10 14;" +
                                "-fx-background-radius: 14;"
                );
            }

            if (statusLabel != null) {
                statusLabel.setText(currentLoan.getStatus());
                statusLabel.setStyle(getStatusStyle(currentLoan.getStatus()));
            }

            loadPaymentCards();

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error(
                    "Erreur actualisation",
                    "Impossible d'actualiser les informations du prêt.\n" + e.getMessage()
            );
        }
    }

    private void setLabelStyle(Label label, String title, String value, String color) {
        if (label == null) {
            return;
        }

        label.setText(title + " : " + safeText(value));
        label.setStyle(
                "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-background-color: white;" +
                        "-fx-padding: 10 14;" +
                        "-fx-background-radius: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 10, 0, 0, 3);"
        );
    }

    /* ================= PAYMENT CARDS ================= */

    private void loadPaymentCards() {
        if (paymentsContainer == null) {
            return;
        }

        paymentsContainer.getChildren().clear();

        try {
            List<LoanPayment> payments = paymentService.getByLoan(currentLoan.getId());

            if (payments == null || payments.isEmpty()) {
                VBox emptyBox = new VBox(8);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(25));
                emptyBox.setStyle(
                        "-fx-background-color: #F8FAFC;" +
                                "-fx-background-radius: 18;" +
                                "-fx-border-color: #E5E7EB;" +
                                "-fx-border-radius: 18;"
                );

                Label icon = new Label("💳");
                icon.setStyle("-fx-font-size: 34px;");

                Label empty = new Label("Aucun paiement pour le moment");
                empty.setStyle(
                        "-fx-text-fill: #64748B;" +
                                "-fx-font-size: 15px;" +
                                "-fx-font-weight: 700;"
                );

                emptyBox.getChildren().addAll(icon, empty);
                paymentsContainer.getChildren().add(emptyBox);
                return;
            }

            for (LoanPayment payment : payments) {
                VBox card = createPaymentCard(payment);
                paymentsContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error(
                    "Erreur paiement",
                    "Impossible de charger l'historique des paiements.\n" + e.getMessage()
            );
        }
    }

    /* ================= CARD UI ================= */

    private VBox createPaymentCard(LoanPayment payment) throws SQLException {
        User payer = userService.getById(payment.getPayerId());
        User receiver = userService.getById(payment.getReceiverId());

        String payerName = payer != null ? payer.getFullname() : "Utilisateur inconnu";
        String receiverName = receiver != null ? receiver.getFullname() : "Utilisateur inconnu";

        Label amount = new Label(String.format("%.2f TND", payment.getAmountPaid()));
        amount.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-text-fill: #16A34A;"
        );

        Label statusChip = new Label("PAYÉ");
        statusChip.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-background-color: #16A34A;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-padding: 6 12;" +
                        "-fx-background-radius: 999;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, amount, spacer, statusChip);
        header.setAlignment(Pos.CENTER_LEFT);

        Label payerLabel = new Label("De : " + payerName);
        payerLabel.setStyle(infoTextStyle("#334155"));

        Label receiverLabel = new Label("À : " + receiverName);
        receiverLabel.setStyle(infoTextStyle("#334155"));

        Label date = new Label("Date : " + payment.getPaymentDate());
        date.setStyle(infoTextStyle("#64748B"));

        VBox info = new VBox(6, payerLabel, receiverLabel, date);

        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #DCFCE7;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.10), 14, 0, 0, 5);"
        );

        card.getChildren().addAll(header, info);

        return card;
    }

    private String infoTextStyle(String color) {
        return "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 700;";
    }

    /* ================= PAY LOAN ================= */

    @FXML
    private void payLoan() {
        try {
            if (currentUser == null || currentUserId == 0) {
                DialogUtil.error("Connexion requise", "Vous devez être connecté pour payer un prêt.");
                return;
            }

            if (currentLoan == null) {
                DialogUtil.error("Erreur", "Aucun prêt sélectionné.");
                return;
            }



            if (!"ACTIVE".equalsIgnoreCase(currentLoan.getStatus())) {
                DialogUtil.error(
                        "Paiement impossible",
                        "Ce prêt n'est pas actif."
                );
                return;
            }

            String text = amountField != null ? amountField.getText() : null;

            if (text == null || text.isBlank()) {
                DialogUtil.error("Montant invalide", "Veuillez saisir un montant.");
                return;
            }

            double amount;

            try {
                amount = Double.parseDouble(text.trim().replace(",", "."));
            } catch (NumberFormatException ex) {
                DialogUtil.error("Montant invalide", "Veuillez saisir un nombre valide.");
                return;
            }

            if (amount <= 0) {
                DialogUtil.error("Montant invalide", "Le montant doit être supérieur à 0.");
                return;
            }

            if (amount > currentLoan.getRemainingAmount()) {
                DialogUtil.error(
                        "Montant invalide",
                        "Vous ne pouvez pas payer plus que le montant restant : " +
                                String.format("%.2f TND", currentLoan.getRemainingAmount())
                );
                return;
            }

            boolean confirmed = DialogUtil.confirm(
                    "Confirmer le paiement",
                    "Vous allez payer " + String.format("%.2f TND", amount) + ".\n\n" +
                            "Restant après paiement : " +
                            String.format("%.2f TND", currentLoan.getRemainingAmount() - amount) +
                            "\n\nVoulez-vous continuer ?"
            );

            if (!confirmed) {
                return;
            }

            loanService.payLoan(currentLoan.getId(), currentUserId, amount);

            DialogUtil.success(
                    "Paiement effectué",
                    "Le paiement de " + String.format("%.2f TND", amount) + " a été effectué avec succès."
            );

            if (amountField != null) {
                amountField.clear();
            }

            loadLoan(currentLoan.getId());

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Paiement échoué", e.getMessage());
        }
    }

    /* ================= HELPERS ================= */

    private String getStatusStyle(String status) {
        String clean = status == null ? "UNKNOWN" : status.toUpperCase();

        String bg;
        switch (clean) {
            case "PAID":
                bg = "#16A34A";
                break;
            case "ACTIVE":
                bg = "#F59E0B";
                break;
            case "OVERDUE":
                bg = "#DC2626";
                break;
            default:
                bg = "#64748B";
                break;
        }

        return "-fx-text-fill: white;" +
                "-fx-background-color: " + bg + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 900;" +
                "-fx-padding: 8 14;" +
                "-fx-background-radius: 999;";
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }
}