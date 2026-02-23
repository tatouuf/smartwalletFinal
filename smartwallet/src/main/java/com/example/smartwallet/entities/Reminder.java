package com.example.smartwallet.entities;

import java.time.LocalDateTime;

public class Reminder {

    private int id;
    private int recurringId;
    private int userId;
    private int remindBeforeDays;
    private boolean isEnabled;
    private LocalDateTime createdAt;

    public Reminder(int id, int recurringId, int userId, int remindBeforeDays, boolean isEnabled, LocalDateTime createdAt) {
        this.id = id;
        this.recurringId = recurringId;
        this.userId = userId;
        this.remindBeforeDays = remindBeforeDays;
        this.isEnabled = isEnabled;
        this.createdAt = createdAt;
    }

    public Reminder(int recurringId, int userId, int remindBeforeDays, boolean isEnabled) {
        this.recurringId = recurringId;
        this.userId = userId;
        this.remindBeforeDays = remindBeforeDays;
        this.isEnabled = isEnabled;
    }

    public int getId() { return id; }
    public int getRecurringId() { return recurringId; }
    public int getUserId() { return userId; }
    public int getRemindBeforeDays() { return remindBeforeDays; }
    public boolean isEnabled() { return isEnabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
}
