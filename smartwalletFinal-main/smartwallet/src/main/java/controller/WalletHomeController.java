package controller;

import entities.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tests.MainFxml;
import utils.Session;

public class WalletHomeController {

    @FXML private Label userNameLabel;

    @FXML
    public void initialize() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
        }
    }

    @FXML
    private void goWallet() {
        MainFxml.getInstance().showWalletInscription();
    }

    @FXML
    private void goCards() {
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/card_add.fxml",
                "Mes Cartes Bancaires",
                950, 650,
                true
        );
    }

    @FXML
    private void goSend() {
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/send.fxml",
                "Envoyer de l'argent",
                500, 600,
                true
        );
    }

    @FXML
    private void goWithdraw() {
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/withdraw.fxml",
                "Retirer de l'argent",
                500, 500,
                true
        );
    }

    @FXML
    private void list() {
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/cards.fxml",
                "----------",
                500, 500,
                true
        );
    }

    @FXML
    private void goHistory() {
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/history.fxml",
                "Historique des transactions",
                900, 600,
                true
        );
    }

     @FXML
    private void goLoans() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Prêts");
        dialog.setHeaderText(null);

        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        VBox root = new VBox(18);
        root.setPrefWidth(520);
        root.setPadding(new Insets(26));
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f4f7ff);" +
                        "-fx-background-radius: 24;"
        );

        VBox header = new VBox(6);

        Label badge = new Label("SMARTWALLET LOANS");
        badge.setStyle(
                "-fx-text-fill: #4f46e5;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-letter-spacing: 1px;" +
                        "-fx-background-color: #eef2ff;" +
                        "-fx-padding: 7 12;" +
                        "-fx-background-radius: 999;"
        );

        Label title = new Label("Gestion des prêts");
        title.setStyle(
                "-fx-text-fill: #111827;" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: 900;"
        );

        Label subtitle = new Label("Choisissez une action pour consulter, demander ou gérer vos prêts.");
        subtitle.setWrapText(true);
        subtitle.setStyle(
                "-fx-text-fill: #6b7280;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;"
        );

        header.getChildren().addAll(badge, title, subtitle);

        VBox options = new VBox(14);

        VBox myLoansCard = createLoanMenuCard(
                "📋",
                "Mes Prêts",
                "Consulter vos prêts empruntés et prêtés.",
                "#4f46e5",
                "#eef2ff"
        );

        VBox requestLoanCard = createLoanMenuCard(
                "➕",
                "Demander un prêt",
                "Créer une nouvelle demande de prêt.",
                "#16a34a",
                "#dcfce7"
        );

        VBox receivedRequestsCard = createLoanMenuCard(
                "📩",
                "Demandes reçues",
                "Voir les demandes de prêt envoyées par d’autres utilisateurs.",
                "#f59e0b",
                "#fef3c7"
        );

        myLoansCard.setOnMouseClicked(e -> {
            dialog.close();
            MainFxml.getInstance().openPopup(
                    "/fxml/loan/myloans.fxml",
                    "Mes Prêts",
                    900,
                    600,
                    true
            );
        });

        requestLoanCard.setOnMouseClicked(e -> {
            dialog.close();
            MainFxml.getInstance().openPopup(
                    "/fxml/loan/request.fxml",
                    "Demander un prêt",
                    600,
                    700,
                    true
            );
        });

        receivedRequestsCard.setOnMouseClicked(e -> {
            dialog.close();
            MainFxml.getInstance().openPopup(
                    "/fxml/loan/requests.fxml",
                    "Demandes de prêt reçues",
                    900,
                    600,
                    true
            );
        });

        options.getChildren().addAll(myLoansCard, requestLoanCard, receivedRequestsCard);

        root.getChildren().addAll(header, options);

        dialog.getDialogPane().setContent(root);

        dialog.getDialogPane().setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-padding: 0;"
        );

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setOnShown(e -> {
            stage.getScene().getRoot().setStyle(
                    "-fx-background-color: transparent;"
            );
        });

        Button close = (Button) dialog.getDialogPane().lookupButton(closeButton);
        close.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 9 22;" +
                        "-fx-background-radius: 999;"
        );

        dialog.showAndWait();
    }

    private VBox createLoanMenuCard(
            String iconText,
            String titleText,
            String descriptionText,
            String mainColor,
            String softColor
    ) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setCursor(Cursor.HAND);

        String normalStyle =
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 18, 0, 0, 6);";

        String hoverStyle =
                "-fx-background-color: " + softColor + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: " + mainColor + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.18), 24, 0, 0, 10);" +
                        "-fx-translate-y: -2;";

        card.setStyle(normalStyle);

        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(iconText);
        icon.setMinSize(52, 52);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle(
                "-fx-background-color: " + softColor + ";" +
                        "-fx-text-fill: " + mainColor + ";" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 16;"
        );

        VBox textBox = new VBox(4);

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-text-fill: #111827;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 900;"
        );

        Label description = new Label(descriptionText);
        description.setWrapText(true);
        description.setStyle(
                "-fx-text-fill: #6b7280;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: 600;"
        );

        textBox.getChildren().addAll(title, description);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label arrow = new Label("›");
        arrow.setStyle(
                "-fx-text-fill: " + mainColor + ";" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: 900;"
        );

        row.getChildren().addAll(icon, textBox, spacer, arrow);
        card.getChildren().add(row);

        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(normalStyle));

        return card;
    }


    @FXML
    private void goServices() {
        MainFxml.getInstance().openServiceClientPopup();
    }

    @FXML
    private void goFriends() {
        MainFxml.getInstance().openFriendsListPopup();
    }

    @FXML
    private void goReclamations() {
        MainFxml.getInstance().openReclamationUserPopup();
    }

    @FXML
    private void goAssurances() {
        showFeatureComingSoon("Assurances");
    }

    @FXML
    private void goBudgetExpenses() {
        MainFxml.getInstance().openBudgetExpensesPopup();
    }

    private void showFeatureComingSoon(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fonctionnalité à venir");
        alert.setHeaderText(null);
        alert.setContentText("La fonctionnalité '" + featureName + "' sera disponible prochainement !");
        alert.showAndWait();
    }

    @FXML
    private void logout() {
        MainFxml.getInstance().logout();
    }
}