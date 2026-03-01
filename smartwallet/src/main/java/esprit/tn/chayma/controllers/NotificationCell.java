package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Notification;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class NotificationCell extends ListCell<Notification> {

    @Override
    protected void updateItem(Notification notification, boolean empty) {
        super.updateItem(notification, empty);

        if (empty || notification == null) {
            setGraphic(null); // Si la notification est vide, on ne l'affiche pas
        } else {
            VBox vbox = new VBox();

            // Titre de la notification
            Text title = new Text(notification.getTitle());
            title.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-fill:#FBCFE8;");

            // Message de la notification
            Text message = new Text(notification.getMessage());
            message.setStyle("-fx-font-size:14px; -fx-fill:white;");

            // Ajouter les éléments à un VBox (un conteneur vertical)
            vbox.getChildren().addAll(title, message);

            // Ajouter le VBox comme contenu de la cellule
            setGraphic(vbox);
        }
    }
}