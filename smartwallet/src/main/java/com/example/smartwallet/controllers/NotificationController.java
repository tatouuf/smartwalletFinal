package com.example.smartwallet.controllers;

import com.example.smartwallet.Services.ServiceNotification;
import com.example.smartwallet.entities.Notification;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.List;

public class NotificationController {

    @FXML
    private ListView<Notification> notificationList;

    private final ServiceNotification serviceNotification = new ServiceNotification();

    // ⚠️ Pour l’instant on met userId=1 (plus tard tu le remplaces par l’utilisateur connecté)
    private static final int CURRENT_USER_ID = 1;

    @FXML
    public void initialize() {

        // 1) Charger depuis la DB
        try {
            List<Notification> list = serviceNotification.recupererParUser(CURRENT_USER_ID);
            notificationList.getItems().setAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2) Affichage + couleurs
        notificationList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification n, boolean empty) {
                super.updateItem(n, empty);

                if (empty || n == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText("[" + n.getType().name() + "] " + n.getTitle() + " — " + n.getMessage());

                switch (n.getType()) {
                    case CRITICAL -> setStyle("-fx-background-color:#ff4d4d; -fx-text-fill:white; -fx-font-weight:bold;");
                    case SUCCESS  -> setStyle("-fx-background-color:#4CAF50; -fx-text-fill:white; -fx-font-weight:bold;");
                    case PENDING  -> setStyle("-fx-background-color:#FFA500; -fx-text-fill:white; -fx-font-weight:bold;");
                }
            }
        });
    }
}
