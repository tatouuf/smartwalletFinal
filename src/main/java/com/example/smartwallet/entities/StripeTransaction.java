package com.example.smartwallet.entities;

import java.time.LocalDateTime;

public class StripeTransaction {
    private int id;
    private int userId;
    private int profileId; // Linked to FinanceProfile
    private String stripePaymentIntentId; // Stripe identifier (pi_...)
    private double amount;
    private String currency;
    private String status; // PENDING, SUCCEEDED, FAILED
    private LocalDateTime createdAt;

    public StripeTransaction() {}

    public StripeTransaction(int id, int userId, int profileId, String stripePaymentIntentId, 
                             double amount, String currency, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.profileId = profileId;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public StripeTransaction(int userId, int profileId, String stripePaymentIntentId, 
                             double amount, String currency, String status) {
        this.userId = userId;
        this.profileId = profileId;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "StripeTransaction{" +
                "id=" + id +
                ", stripeId='" + stripePaymentIntentId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
