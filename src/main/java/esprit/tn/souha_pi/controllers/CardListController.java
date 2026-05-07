package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.BankCardService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import tests.MainFxml;  // AJOUT IMPORTANT
import utils.Session;

import java.io.IOException;
import java.util.List;

public class CardListController {

    @FXML private FlowPane cardsContainer;

    private BankCardService cardService = new BankCardService();
    private entities.User currentUser;

    @FXML
    public void initialize() {
        currentUser = Session.getCurrentUser();
        loadCards();
    }

    private void loadCards() {
        if (currentUser == null) return;

        cardsContainer.getChildren().clear();
        List<BankCard> cards = cardService.getAllByUser(currentUser.getId());

        for (BankCard card : cards) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/wallet/card_item.fxml")
                );
                Node cardNode = loader.load();

                CardItemController controller = loader.getController();
                controller.setCard(card);

                cardsContainer.getChildren().add(cardNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Ajouter le bouton "Ajouter une carte"
        Button addButton = new Button("+ Ajouter une carte");
        addButton.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20; -fx-cursor: hand;");
        addButton.setOnAction(e -> addCard());
        cardsContainer.getChildren().add(addButton);
    }

    @FXML
    private void addCard() {
        MainFxml.getInstance().openPopup(
                "/fxml/wallet/card_add.fxml",
                "Ajouter une carte",
                500, 600,
                true
        );
    }
}