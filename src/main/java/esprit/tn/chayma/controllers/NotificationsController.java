package esprit.tn.chayma.controllers;

import esprit.tn.chayma.entities.Notification;
import esprit.tn.chayma.services.NotificationService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class NotificationsController {

    @FXML
    private ListView<Notification> notificationsList;

    @FXML
    private Button markReadBtn;

    @FXML
    private Button refreshBtn;

    private NotificationService notificationService = new NotificationService();
    private ObservableList<Notification> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadNotifications();
        notificationsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification n, boolean empty) {
                super.updateItem(n, empty);
                if (empty || n == null) {
                    setText(null);
                } else {
                    String read = n.isRead() ? " (lu)" : " (non lu)";
                    setText("[" + n.getType() + "] " + n.getMessage() + read + " - " + n.getCreatedAt());
                }
            }
        });

        markReadBtn.setOnAction(e -> onMarkRead());
        refreshBtn.setOnAction(e -> loadNotifications());
    }

    private void loadNotifications() {
        // Pour l'instant on récupère les notifications user_id = 0
        items.setAll(notificationService.listByUser(0));
        notificationsList.setItems(items);
    }

    private void onMarkRead() {
        Notification sel = notificationsList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        // Marquer comme lu dans la BDD
        boolean ok = notificationService.markAsRead(sel.getId());
        if (ok) {
            sel.setRead(true);
            notificationsList.refresh();
            DialogUtil.info("Succès", "Notification marquée comme lue");
        } else {
            DialogUtil.error("Erreur", "Impossible de marquer comme lu");
        }
    }
}
