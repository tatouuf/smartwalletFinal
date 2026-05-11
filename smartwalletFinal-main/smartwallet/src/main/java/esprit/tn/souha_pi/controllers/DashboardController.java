package esprit.tn.souha_pi.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import entities.User;
import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.services.WalletService;
import esprit.tn.souha_pi.utils.DialogUtil;
import esprit.tn.souha_pi.utils.EventBus;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tests.MainFxml;
import utils.Session;

import java.awt.image.BufferedImage;

public class DashboardController {

    @FXML private Label balanceLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label ribLabel;
    @FXML private Label cinLabel;
    @FXML private Label numeroCompteLabel;
    @FXML private Label noWalletLabel;
    @FXML private Label cardCountLabel;
    @FXML private Label qrTextLabel;

    @FXML private ImageView qrImageView;

    @FXML private FlowPane cardsContainer;

    @FXML private Button addCardButton;
    @FXML private Button createWalletButton;

    private static DashboardController instance;

    private final WalletService walletService = new WalletService();
    private final BankCardService cardService = new BankCardService();

    private User currentUser;
    private String currentRib = "";

    private static final int MAX_CARDS = 5;

    public DashboardController() {
        instance = this;
    }

    @FXML
    public void initialize() {
        currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            if (welcomeLabel != null) {
                welcomeLabel.setText("Bienvenue");
            }

            showNoWallet("Utilisateur non connecté.");
            return;
        }

        if (welcomeLabel != null) {
            welcomeLabel.setText("Bienvenue " + safe(currentUser.getFullname(), ""));
        }

        refreshDashboard();
    }

    public void refreshDashboard() {
        if (currentUser == null) {
            showNoWallet("Utilisateur non connecté.");
            return;
        }

        try {
            Wallet wallet = walletService.getByUserId(currentUser.getId());

            if (wallet == null) {
                showNoWallet("Vous n'avez pas encore de wallet.");
                clearWalletLabels();
                clearCards();
                return;
            }

            hideNoWallet();

            if (balanceLabel != null) {
                balanceLabel.setText(String.format("%.2f TND", wallet.getBalance()));
            }

            updateWalletMeta(wallet);
            afficherCartes();

        } catch (Exception e) {
            e.printStackTrace();

            if (balanceLabel != null) {
                balanceLabel.setText("0.00 TND");
            }

            showNoWallet("Vous n'avez pas encore de wallet.");
            clearWalletLabels();
            clearCards();
        }
    }

    private void updateWalletMeta(Wallet wallet) {
        String rib = safeGetter(wallet::getRib, "");
        String cin = safeGetter(wallet::getCin, "Non renseigné");
        String numeroCompte = safeGetter(wallet::getNumeroCompte, "---");

        currentRib = rib;

        if (ribLabel != null) {
            ribLabel.setText(rib == null || rib.isBlank() ? "Non renseigné" : rib);
        }

        if (cinLabel != null) {
            cinLabel.setText(cin);
        }

        if (numeroCompteLabel != null) {
            numeroCompteLabel.setText(numeroCompte);
        }

        generateQrCode(rib);
    }

    private void clearWalletLabels() {
        currentRib = "";

        if (ribLabel != null) {
            ribLabel.setText("Non renseigné");
        }

        if (cinLabel != null) {
            cinLabel.setText("Non renseigné");
        }

        if (numeroCompteLabel != null) {
            numeroCompteLabel.setText("---");
        }

        if (qrImageView != null) {
            qrImageView.setImage(null);
        }

        if (qrTextLabel != null) {
            qrTextLabel.setText("RIB non disponible");
        }
    }

    private void showNoWallet(String message) {
        if (noWalletLabel != null) {
            noWalletLabel.setText(message);
            noWalletLabel.setVisible(true);
            noWalletLabel.setManaged(true);
        }

        if (createWalletButton != null) {
            createWalletButton.setVisible(true);
            createWalletButton.setManaged(true);
        }

        if (addCardButton != null) {
            addCardButton.setDisable(true);
        }
    }

    private void hideNoWallet() {
        if (noWalletLabel != null) {
            noWalletLabel.setVisible(false);
            noWalletLabel.setManaged(false);
        }

        if (createWalletButton != null) {
            createWalletButton.setVisible(false);
            createWalletButton.setManaged(false);
        }

        if (addCardButton != null) {
            addCardButton.setDisable(false);
        }
    }

    private void clearCards() {
        if (cardsContainer != null) {
            cardsContainer.getChildren().clear();
        }

        if (cardCountLabel != null) {
            cardCountLabel.setText("Cartes: 0/" + MAX_CARDS);
        }
    }

    private void afficherCartes() {
        if (cardsContainer == null || currentUser == null) {
            return;
        }

        cardsContainer.getChildren().clear();

        try {
            var cartes = cardService.getAllByUser(currentUser.getId());
            int nombreCartes = cartes.size();

            if (cardCountLabel != null) {
                cardCountLabel.setText("Cartes: " + nombreCartes + "/" + MAX_CARDS);
            }

            updateAddCardButton(nombreCartes);

            if (cartes.isEmpty()) {
                VBox emptyBox = new VBox(8);
                emptyBox.setStyle(
                        "-fx-background-color: #f8fafc;" +
                                "-fx-background-radius: 18;" +
                                "-fx-padding: 26;" +
                                "-fx-border-color: #e5e7eb;" +
                                "-fx-border-radius: 18;"
                );

                Label icon = new Label("💳");
                icon.setStyle("-fx-font-size: 38px;");

                Label title = new Label("Aucune carte associée");
                title.setStyle(
                        "-fx-text-fill: #111827;" +
                                "-fx-font-size: 17px;" +
                                "-fx-font-weight: 900;"
                );

                Label subtitle = new Label("Ajoutez une carte pour alimenter votre wallet.");
                subtitle.setStyle(
                        "-fx-text-fill: #64748b;" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: 600;"
                );

                emptyBox.getChildren().addAll(icon, title, subtitle);
                cardsContainer.getChildren().add(emptyBox);
                return;
            }

            for (BankCard carte : cartes) {
                VBox cardBox = creerVignetteCarte(carte);
                cardsContainer.getChildren().add(cardBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Erreur", "Impossible de charger les cartes.");
        }
    }

    private void updateAddCardButton(int nombreCartes) {
        if (addCardButton == null) {
            return;
        }

        if (nombreCartes >= MAX_CARDS) {
            addCardButton.setDisable(true);
            addCardButton.setText("Maximum atteint (" + MAX_CARDS + "/" + MAX_CARDS + ")");
            addCardButton.setStyle(
                    "-fx-background-color: #94a3b8;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 14;" +
                            "-fx-padding: 11 18;" +
                            "-fx-font-size: 14px;"
            );
        } else {
            addCardButton.setDisable(false);
            addCardButton.setText("+ Ajouter une carte (" + nombreCartes + "/" + MAX_CARDS + ")");
            addCardButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #5646e1, #9947c1, #e6479b);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 14;" +
                            "-fx-padding: 11 18;" +
                            "-fx-font-size: 14px;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    private VBox creerVignetteCarte(BankCard carte) {
        VBox cardBox = new VBox(12);
        cardBox.setPrefWidth(330);
        cardBox.setCursor(Cursor.HAND);
        cardBox.setStyle(cardStyle(false));

        HBox top = new HBox(10);
        top.setStyle("-fx-alignment: center-left;");

        Label chip = new Label("💳");
        chip.setStyle(
                "-fx-background-color: rgba(255,255,255,0.18);" +
                        "-fx-background-radius: 14;" +
                        "-fx-min-width: 44;" +
                        "-fx-min-height: 44;" +
                        "-fx-alignment: center;" +
                        "-fx-font-size: 22px;"
        );

        VBox typeBox = new VBox(2);

        Label typeLabel = new Label(safe(carte.getCardType(), "CARD"));
        typeLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: 900;"
        );

        Label ribSmall = new Label("RIB: " + safe(carte.getRib(), "-"));
        ribSmall.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.75);" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: 600;"
        );

        typeBox.getChildren().addAll(typeLabel, ribSmall);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label("ACTIVE");
        status.setStyle(
                "-fx-background-color: rgba(34,197,94,0.95);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-padding: 6 10;" +
                        "-fx-background-radius: 999;"
        );

        top.getChildren().addAll(chip, typeBox, spacer, status);

        String numero = safe(carte.getCardNumber(), "0000");
        String last4 = numero.length() >= 4 ? numero.substring(numero.length() - 4) : numero;

        Label numberLabel = new Label("**** **** **** " + last4);
        numberLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: 900;" +
                        "-fx-letter-spacing: 2px;"
        );

        HBox infoBox = new HBox(20);

        VBox holderBox = new VBox(3);
        Label holderTitle = new Label("Titulaire");
        holderTitle.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 10px; -fx-font-weight: 800;");
        Label holderLabel = new Label(safe(carte.getCardHolder(), "-"));
        holderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 900;");
        holderBox.getChildren().addAll(holderTitle, holderLabel);

        VBox expiryBox = new VBox(3);
        Label expiryTitle = new Label("Expiration");
        expiryTitle.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 10px; -fx-font-weight: 800;");
        Label expiryLabel = new Label(String.valueOf(carte.getExpiryDate()));
        expiryLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 900;");
        expiryBox.getChildren().addAll(expiryTitle, expiryLabel);

        infoBox.getChildren().addAll(holderBox, expiryBox);

        HBox actionsBox = new HBox(8);

        Button envoyerBtn = new Button("Envoyer");
        envoyerBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #5646e1;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 7 10;"
        );
        envoyerBtn.setOnAction(e -> ouvrirEnvoi(carte));

        Button topUpBtn = new Button("Recharger");
        topUpBtn.setStyle(
                "-fx-background-color: #22c55e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 7 10;"
        );
        topUpBtn.setOnAction(e -> ouvrirTopUp(carte));

        Button detailsBtn = new Button("Détails");
        detailsBtn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.18);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 7 10;" +
                        "-fx-border-color: rgba(255,255,255,0.28);" +
                        "-fx-border-radius: 10;"
        );
        detailsBtn.setOnAction(e -> afficherDetails(carte));

        actionsBox.getChildren().addAll(envoyerBtn, topUpBtn, detailsBtn);

        cardBox.getChildren().addAll(top, numberLabel, infoBox, actionsBox);

        cardBox.setOnMouseEntered(e -> cardBox.setStyle(cardStyle(true)));
        cardBox.setOnMouseExited(e -> cardBox.setStyle(cardStyle(false)));

        return cardBox;
    }

    private String cardStyle(boolean hover) {
        if (hover) {
            return "-fx-background-color: linear-gradient(to right, #4f46e5, #7c3aed, #e6479b);" +
                    "-fx-padding: 18;" +
                    "-fx-background-radius: 22;" +
                    "-fx-effect: dropshadow(gaussian, rgba(86,70,225,0.35), 26, 0, 0, 12);" +
                    "-fx-translate-y: -3;";
        }

        return "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
                "-fx-padding: 18;" +
                "-fx-background-radius: 22;" +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.22), 18, 0, 0, 8);" +
                "-fx-translate-y: 0;";
    }

    private void ouvrirEnvoi(BankCard carte) {
        SendController.setCarteSource(carte);

        openWindow(
                "/fxml/wallet/send.fxml",
                "Envoyer de l'argent",
                620,
                700
        );
    }

    private void ouvrirTopUp(BankCard carte) {
        TopUpController.setSelectedCard(carte);

        openWindow(
                "/fxml/wallet/topup.fxml",
                "Recharger mon wallet",
                620,
                700
        );
    }

    private void afficherDetails(BankCard carte) {
        String message = String.format(
                "Détails de la carte:\n\n" +
                        "Type: %s\n" +
                        "Titulaire: %s\n" +
                        "Numéro: %s\n" +
                        "RIB: %s\n" +
                        "Expiration: %s\n" +
                        "CVV: %s",
                safe(carte.getCardType(), "-"),
                safe(carte.getCardHolder(), "-"),
                safe(carte.getCardNumber(), "-"),
                safe(carte.getRib(), "-"),
                String.valueOf(carte.getExpiryDate()),
                safe(carte.getCvv(), "-")
        );

        DialogUtil.success("Détails de la carte", message);
    }

    @FXML
    private void createWallet() {
        MainFxml.getInstance().showWalletInscription();
    }

    @FXML
    private void addCard() {
        try {
            if (currentUser == null) {
                DialogUtil.error("Erreur", "Utilisateur non connecté.");
                return;
            }

            var cartes = cardService.getAllByUser(currentUser.getId());
            int nombreCartes = cartes.size();

            if (nombreCartes >= MAX_CARDS) {
                DialogUtil.error(
                        "Limite atteinte",
                        "Vous avez déjà atteint la limite maximale de " + MAX_CARDS + " cartes."
                );
                return;
            }

            Stage cardStage = MainFxml.getInstance().openPopup(
                    "/fxml/wallet/card_add.fxml",
                    "Ajouter une carte (" + nombreCartes + "/" + MAX_CARDS + ")",
                    500,
                    620,
                    true
            );

            if (cardStage != null) {
                cardStage.setOnHidden(event -> refreshDashboard());
                cardStage.centerOnScreen();
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error(
                    "Erreur",
                    "Impossible d'ouvrir le formulaire d'ajout de carte."
            );
        }
    }

    @FXML
    private void openSendWindow() {
        DialogUtil.error(
                "Carte requise",
                "Veuillez cliquer sur le bouton Envoyer d'une carte pour choisir la carte source."
        );
    }

    @FXML
    private void openReceiveWindow() {
        openWindow(
                "/fxml/wallet/receive.fxml",
                "Recevoir de l'argent",
                620,
                700
        );
    }

    @FXML
    private void showTopUpInfo() {
        DialogUtil.error(
                "Carte requise",
                "Veuillez cliquer sur le bouton Recharger d'une carte pour choisir la carte source."
        );
    }

    @FXML
    private void openHistoryWindow() {
        openWindow(
                "/fxml/wallet/history.fxml",
                "Historique des transactions",
                1050,
                720
        );
    }

    private void openWindow(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(width);
            stage.setMinHeight(height);
            stage.setResizable(true);

            stage.initModality(Modality.NONE);

            stage.setOnHidden(event -> refreshDashboard());

            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();

            DialogUtil.error(
                    "Erreur",
                    "Impossible d'ouvrir la fenêtre : " + title + "\n" + e.getMessage()
            );
        }
    }

    @FXML
    private void goSend() {
        openSendWindow();
    }

    @FXML
    private void goReceive() {
        openReceiveWindow();
    }

    @FXML
    private void goHistory() {
        openHistoryWindow();
    }

    @FXML
    private void goLoans() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Prêts");
        dialog.setHeaderText(null);

        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        VBox root = new VBox(16);
        root.setPrefWidth(500);
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f4f7ff);" +
                        "-fx-background-radius: 24;" +
                        "-fx-padding: 26;"
        );

        Label badge = new Label("SMARTWALLET LOANS");
        badge.setStyle(
                "-fx-text-fill: #4f46e5;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: 900;" +
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

        Label subtitle = new Label("Choisissez une action pour consulter ou demander un prêt.");
        subtitle.setWrapText(true);
        subtitle.setStyle(
                "-fx-text-fill: #6b7280;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;"
        );

        VBox myLoansCard = createLoanMenuCard("📋", "Mes Prêts", "Consulter vos prêts empruntés et prêtés.");
        VBox requestLoanCard = createLoanMenuCard("➕", "Demander un prêt", "Créer une nouvelle demande de prêt.");
        VBox receivedRequestsCard = createLoanMenuCard("📩", "Demandes reçues", "Voir les demandes de prêt reçues.");

        myLoansCard.setOnMouseClicked(e -> {
            dialog.close();
            MainFxml.getInstance().openPopup("/fxml/loan/myloans.fxml", "Mes Prêts", 950, 650, true);
        });

        requestLoanCard.setOnMouseClicked(e -> {
            dialog.close();
            MainFxml.getInstance().openPopup("/fxml/loan/request.fxml", "Demander un prêt", 650, 720, true);
        });

        receivedRequestsCard.setOnMouseClicked(e -> {
            dialog.close();
            MainFxml.getInstance().openPopup("/fxml/loan/requests.fxml", "Demandes de prêt reçues", 950, 650, true);
        });

        root.getChildren().addAll(badge, title, subtitle, myLoansCard, requestLoanCard, receivedRequestsCard);

        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        dialog.showAndWait();
    }

    private VBox createLoanMenuCard(String iconText, String titleText, String descriptionText) {
        VBox card = new VBox(6);
        card.setCursor(Cursor.HAND);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;" +
                        "-fx-padding: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 15, 0, 0, 5);"
        );

        HBox row = new HBox(12);
        row.setStyle("-fx-alignment: center-left;");

        Label icon = new Label(iconText);
        icon.setStyle(
                "-fx-background-color: #eef2ff;" +
                        "-fx-background-radius: 14;" +
                        "-fx-min-width: 48;" +
                        "-fx-min-height: 48;" +
                        "-fx-alignment: center;" +
                        "-fx-font-size: 22px;"
        );

        VBox texts = new VBox(3);

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-text-fill: #111827;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: 900;"
        );

        Label description = new Label(descriptionText);
        description.setWrapText(true);
        description.setStyle(
                "-fx-text-fill: #6b7280;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: 600;"
        );

        texts.getChildren().addAll(title, description);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label arrow = new Label("›");
        arrow.setStyle(
                "-fx-text-fill: #5646e1;" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: 900;"
        );

        row.getChildren().addAll(icon, texts, spacer, arrow);
        card.getChildren().add(row);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #eef2ff;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #5646e1;" +
                        "-fx-border-radius: 18;" +
                        "-fx-padding: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(86,70,225,0.20), 20, 0, 0, 8);" +
                        "-fx-translate-y: -2;"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;" +
                        "-fx-padding: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 15, 0, 0, 5);" +
                        "-fx-translate-y: 0;"
        ));

        return card;
    }

    private void generateQrCode(String text) {
        try {
            if (qrImageView == null) {
                return;
            }

            if (text == null || text.trim().isEmpty()) {
                qrImageView.setImage(null);

                if (qrTextLabel != null) {
                    qrTextLabel.setText("RIB non disponible");
                }

                return;
            }

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    220,
                    220
            );

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            Image qrImage = SwingFXUtils.toFXImage(bufferedImage, null);

            qrImageView.setImage(qrImage);

            if (qrTextLabel != null) {
                qrTextLabel.setText("Scanner pour récupérer votre RIB");
            }

        } catch (WriterException e) {
            e.printStackTrace();

            if (qrTextLabel != null) {
                qrTextLabel.setText("Erreur génération QR");
            }
        }
    }

    @FXML
    private void copyRib() {
        if (currentRib == null || currentRib.trim().isEmpty()) {
            DialogUtil.error("RIB indisponible", "Aucun RIB à copier.");
            return;
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(currentRib);

        Clipboard.getSystemClipboard().setContent(content);

        DialogUtil.success("Copié", "RIB copié avec succès.");
    }

    public static void refreshStatic() {
        if (instance != null) {
            instance.refreshDashboard();
        }
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String safeGetter(ValueGetter getter, String fallback) {
        try {
            String value = getter.get();
            return safe(value, fallback);
        } catch (Exception e) {
            return fallback;
        }
    }

    @FunctionalInterface
    private interface ValueGetter {
        String get();
    }
}