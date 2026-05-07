package esprit.tn.souha_pi.entities;

import java.time.LocalDateTime;

public class Reminder {
    private int id;
    private int recurringId;
    private int userId;
    private int remindBeforeDays; // Jours avant la date d'échéance
    private boolean isEnabled;
    private LocalDateTime createdAt;

    public Reminder() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecurringId() { return recurringId; }
    public void setRecurringId(int recurringId) { this.recurringId = recurringId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRemindBeforeDays() { return remindBeforeDays; }
    public void setRemindBeforeDays(int remindBeforeDays) { this.remindBeforeDays = remindBeforeDays; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}