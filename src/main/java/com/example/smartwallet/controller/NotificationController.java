package com.example.smartwallet.controller;

import com.example.smartwallet.model.Notification;
import dao.NotificationDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.List;

public class NotificationController {

    private static final NotificationDAO notificationDAO = new NotificationDAO();
    private static final int USER_ID = 1; // Hardcoded user ID for now

    public static void showInformation(String title, String message) {
        saveAndShowAlert("INFO", title, message, AlertType.INFORMATION);
    }

    public static void showError(String title, String message) {
        saveAndShowAlert("ERROR", title, message, AlertType.ERROR);
    }

    public static void showWarning(String title, String message) {
        saveAndShowAlert("WARNING", title, message, AlertType.WARNING);
    }

    private static void saveAndShowAlert(String type, String title, String message, AlertType alertType) {
        // 1. Save to database (notifidepbud table via DAO)
        Notification notification = new Notification(USER_ID, title, message, type);
        notificationDAO.addNotification(notification);

        // 2. Show UI alert
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static ObservableList<Notification> getNotificationHistory() {
        List<Notification> history = notificationDAO.getAllNotifications(USER_ID);
        return FXCollections.observableArrayList(history);
    }

    public static void clearHistory() {
        notificationDAO.clearAllNotifications(USER_ID);
    }
}
