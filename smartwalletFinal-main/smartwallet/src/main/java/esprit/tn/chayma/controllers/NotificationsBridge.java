package esprit.tn.chayma.controllers;

/**
 * Gestionnaire de liaison entre les modules Dépenses et Notifications
 * Permet une communication bidirectionnelle entre les deux modules
 */
public class NotificationsBridge {

    private static NotificationsBridge instance = null;
    private NotificationsController notificationsController = null;
    private DepensesController depensesController = null;

    private NotificationsBridge() {
    }

    /**
     * Obtient l'instance singleton du bridge
     */
    public static NotificationsBridge getInstance() {
        if (instance == null) {
            instance = new NotificationsBridge();
        }
        return instance;
    }

    /**
     * Enregistre le contrôleur Notifications
     */
    public void registerNotificationsController(NotificationsController controller) {
        this.notificationsController = controller;
        System.out.println("[NotificationsBridge] NotificationsController enregistré");
    }

    /**
     * Enregistre le contrôleur Dépenses
     */
    public void registerDepensesController(DepensesController controller) {
        this.depensesController = controller;

        // Configurer le callback du module dépenses
        if (depensesController != null) {
            depensesController.setNotificationCallback(v -> {
                System.out.println("[NotificationsBridge] Callback déclenché: mise à jour notifications");
                refreshNotifications();
            });
        }
        System.out.println("[NotificationsBridge] DepensesController enregistré");
    }

    /**
     * Rafraîchit le module Notifications
     * Appelé automatiquement quand une dépense est ajoutée/modifiée/supprimée
     */
    public void refreshNotifications() {
        if (notificationsController != null) {
            System.out.println("[NotificationsBridge] Rafraîchissement notifications");
            notificationsController.refreshNotifications();
        } else {
            System.out.println("[NotificationsBridge] ERREUR: NotificationsController non enregistré");
        }
    }

    /**
     * Rafraîchit le module Dépenses
     */
    public void refreshDepenses() {
        if (depensesController != null) {
            System.out.println("[NotificationsBridge] Rafraîchissement dépenses");
            // Peut être appelé si nécessaire
        }
    }
}

