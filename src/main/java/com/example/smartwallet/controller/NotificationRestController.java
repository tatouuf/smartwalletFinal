package com.example.smartwallet.controller;

import com.example.smartwallet.model.Notification;
import dao.NotificationDAO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * 🔔 Récupérer toutes les alertes/notifications d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Liste des notifications
     */
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable int userId) {
        return notificationDAO.getAllNotifications(userId);
    }

    /**
     * 🗑️ Effacer l'historique des notifications d'un utilisateur
     * @param userId L'ID de l'utilisateur
     */
    @DeleteMapping("/user/{userId}")
    public void clearUserNotifications(@PathVariable int userId) {
        notificationDAO.clearAllNotifications(userId);
    }
}
