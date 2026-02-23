package com.example.smartwallet.entities;

import java.time.LocalDate;

public class RecurringPayment {

    public enum Frequency {
        MONTHLY,
        YEARLY,
        WEEKLY
    }

    private int id;
    private int userId;
    private String name;
    private double amount;
    private Frequency frequency;
    private LocalDate nextPaymentDate;
    private boolean active;

    // ✅ CONSTRUCTEUR VIDE (OBLIGATOIRE)
    public RecurringPayment() {
    }

    // Constructeur avec ID
    public RecurringPayment(int id, int userId, String name, double amount,
                            Frequency frequency, LocalDate nextPaymentDate, boolean active) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.nextPaymentDate = nextPaymentDate;
        this.active = active;
    }

    // Constructeur sans ID (pour INSERT)
    public RecurringPayment(int userId, String name, double amount,
                            Frequency frequency, LocalDate nextPaymentDate, boolean active) {
        this.userId = userId;
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.nextPaymentDate = nextPaymentDate;
        this.active = active;
    }

    @Override
    public String toString() {
        return name + " | " + String.format("%.2f", amount) + " TND | " + frequency;
    }

    // ================= GETTERS =================

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public Frequency getFrequency() { return frequency; }
    public LocalDate getNextPaymentDate() { return nextPaymentDate; }
    public boolean isActive() { return active; }

    // ================= SETTERS =================

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }
    public void setNextPaymentDate(LocalDate nextPaymentDate) { this.nextPaymentDate = nextPaymentDate; }
    public void setActive(boolean active) { this.active = active; }
}
