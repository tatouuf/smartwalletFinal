package esprit.tn.souha_pi.entities;

import java.time.LocalDateTime;

public class RecurringPayment {
    private int id;
    private int userId;
    private String title;
    private String description;
    private double amount;
    private String frequency; // MENSUEL, HEBDOMADAIRE, ANNUEL
    private LocalDateTime startDate;
    private LocalDateTime nextDueDate;
    private String status; // actif, inactif, termine
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecurringPayment() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDateTime nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}