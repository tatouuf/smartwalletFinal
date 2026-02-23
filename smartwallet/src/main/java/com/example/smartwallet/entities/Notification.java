package com.example.smartwallet.entities;

import java.time.LocalDateTime;

public class Notification {

    public enum Type { CRITICAL, SUCCESS, PENDING }
    public enum Status { UNREAD, READ }

    private int id;
    private int userId;
    private String title;
    private String message;
    private Type type;
    private Status status;
    private LocalDateTime createdAt;
    private Integer recurringId;
    private Integer reminderId;

    // ✅ constructeur vide (OBLIGATOIRE si tu fais new Notification())
    public Notification() {}

    // constructeur complet (si tu en as besoin)
    public Notification(int id, int userId, String title, String message,
                        Type type, Status status, LocalDateTime createdAt,
                        Integer recurringId, Integer reminderId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.recurringId = recurringId;
        this.reminderId = reminderId;
    }

    // ✅ GETTERS
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Type getType() { return type; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Integer getRecurringId() { return recurringId; }
    public Integer getReminderId() { return reminderId; }

    // ✅ SETTERS (ceux que ton erreur demande)
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setType(Type type) { this.type = type; }
    public void setStatus(Status status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setRecurringId(Integer recurringId) { this.recurringId = recurringId; }
    public void setReminderId(Integer reminderId) { this.reminderId = reminderId; }

    @Override
    public String toString() {
        return "[" + type + "] " + title + " - " + message;
    }
}