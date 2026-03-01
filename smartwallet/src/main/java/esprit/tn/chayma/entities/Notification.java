package esprit.tn.chayma.entities;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private int userId;
    private String title;
    private String message;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private boolean isRead;

    // Constructeur avec tous les paramètres
    public Notification(int userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = "UNREAD";  // Par défaut, une notification est non lue
        this.isRead = false;     // Notification non lue
        this.createdAt = LocalDateTime.now();  // Heure de création de la notification
    }

    // Constructeur vide pour d'autres usages (par exemple, pour la lecture depuis la DB)
    public Notification() {}

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}