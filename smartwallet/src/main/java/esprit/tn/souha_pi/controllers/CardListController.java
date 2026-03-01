package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.BankCardService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import utils.Session;

import java.io.IOException;
import java.util.List;

public class CardListController {

    @FXML private FlowPane cardsContainer;

    private BankCardService cardService = new BankCardService();
    entities.User currentUser = Session.getCurrentUser();


    @FXML
    public void initialize() {
        entities.User currentUser = Session.getCurrentUser();
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
                controller.setCard(card);  // ← CORRIGÉ: setCard() au lieu de setData()

                cardsContainer.getChildren().add(cardNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Ajouter le bouton "Ajouter une carte"
        Button addButton = new Button("+ Ajouter une carte");
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> addCard());
        cardsContainer.getChildren().add(addButton);
    }

    @FXML
    private void addCard() {
        WalletLayoutController.instance.loadPage("wallet/card_add.fxml");
    }
}