package com.example.smartwallet.controller.javafx;

import com.example.smartwallet.controller.NotificationController;
import com.example.smartwallet.controller.NotificationController.NotificationLog;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.time.format.DateTimeFormatter;

public class NotificationJavaFXController {

    @FXML
    private ListView<NotificationLog> notificationList;

    @FXML
    public void initialize() {
        notificationList.setItems(NotificationController.getNotificationHistory());

        notificationList.setCellFactory(param -> new ListCell<NotificationLog>() {
            @Override
            protected void updateItem(NotificationLog item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    setText(String.format("[%s] %s: %s - %s",
                            item.getTimestamp().format(formatter),
                            item.getType(),
                            item.getTitle(),
                            item.getMessage()));

                    // Style based on type
                    if ("ERROR".equals(item.getType())) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if ("WARNING".equals(item.getType())) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
    }

    @FXML
    private void clearNotifications() {
        NotificationController.clearHistory();
    }
}