package com.example.smartwallet.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDateTime;

public class NotificationController {

    private static final ObservableList<NotificationLog> notificationHistory = FXCollections.observableArrayList();

    public static void showInformation(String title, String message) {
        addNotification("INFO", title, message);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        addNotification("ERROR", title, message);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWarning(String title, String message) {
        addNotification("WARNING", title, message);
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void addNotification(String type, String title, String message) {
        notificationHistory.add(0, new NotificationLog(type, title, message, LocalDateTime.now()));
    }

    public static ObservableList<NotificationLog> getNotificationHistory() {
        return notificationHistory;
    }

    public static void clearHistory() {
        notificationHistory.clear();
    }

    public static class NotificationLog {
        private final String type;
        private final String title;
        private final String message;
        private final LocalDateTime timestamp;

        public NotificationLog(String type, String title, String message, LocalDateTime timestamp) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", type, title, message);
        }
    }
}
