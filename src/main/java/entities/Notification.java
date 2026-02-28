package entities;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String type; // FRIEND_REQUEST, FRIEND_ACCEPTED, RECLAMATION_REPLY, NEW_RECLAMATION, MILESTONE_USERS, MILESTONE_PROFIT
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Integer relatedId; // ID of related entity (amitie_id, reclamation_id, etc.)

    // Constructors
    public Notification() {
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(int userId, String type, String message, Integer relatedId) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getRelatedId() { return relatedId; }
    public void setRelatedId(Integer relatedId) { this.relatedId = relatedId; }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}