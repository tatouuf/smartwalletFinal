package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.services.NotificationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationsController {

    @FXML
    private ListView<Notification> notificationList;

    private int userId = 1; // Remplacer par l'ID de l'utilisateur connecté

    @FXML
    public void initialize() {
        // Crée une instance de NotificationService pour récupérer les notifications
        NotificationService ns = new NotificationService();

        // Récupère les notifications non lues de l'utilisateur connecté
        notificationList.setItems(FXCollections.observableArrayList(ns.afficherParUser(userId)));

        // Personnaliser l'affichage de chaque élément dans la ListView
        notificationList.setCellFactory(param -> new NotificationCell());
    }
}