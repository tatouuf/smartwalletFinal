package esprit.tn.chayma.services;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Service pour initialiser et exécuter les vérifications périodiques des notifications intelligentes
 */
public class NotificationInitializationService {

    private final IntelligentNotificationService intelligentNotif;
    private final NotificationService notificationService;
    private int userId;
    private boolean isRunning = false;

    public NotificationInitializationService() {
        this.intelligentNotif = new IntelligentNotificationService();
        this.notificationService = new NotificationService();
    }

    /**
     * Initialise le service avec l'ID utilisateur
     */
    public void initialize(int userId) {
        this.userId = userId;
    }

    /**
     * Exécute une vérification unique de tous les indicateurs
     */
    public void performCheck() {
        Task<Void> checkTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    intelligentNotif.runFullCheck(userId);
                    System.out.println("[NotificationCheck] Vérification complète effectuée pour userId=" + userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread thread = new Thread(checkTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Lance les vérifications périodiques (toutes les N minutes)
     * À appeler au démarrage de l'application
     */
    public void startPeriodicChecks(int intervalMinutes) {
        if (isRunning) return;
        isRunning = true;

        Task<Void> periodicTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (isRunning) {
                    try {
                        // Exécuter la vérification
                        intelligentNotif.runFullCheck(userId);
                        System.out.println("[PeriodicCheck] Vérification périodique à " + java.time.LocalTime.now());

                        // Attendre avant la prochaine vérification
                        Thread.sleep(intervalMinutes * 60 * 1000L);
                    } catch (InterruptedException e) {
                        System.out.println("[PeriodicCheck] Arrêt des vérifications périodiques");
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(periodicTask);
        thread.setDaemon(true);
        thread.setName("NotificationCheckThread");
        thread.start();
    }

    /**
     * Arrête les vérifications périodiques
     */
    public void stopPeriodicChecks() {
        isRunning = false;
    }

    /**
     * Obtient le nombre de notifications non lues
     */
    public int getUnreadCount() {
        return notificationService.getUnreadCount(userId);
    }

    /**
     * Teste rapidement si le système fonctionne
     */
    public void testNotificationSystem() {
        notificationService.notifyInfo(userId, "Test Système",
            "✓ Système de notifications intelligent actif et fonctionnel");
        System.out.println("[TEST] Notification de test envoyée");
    }
}

