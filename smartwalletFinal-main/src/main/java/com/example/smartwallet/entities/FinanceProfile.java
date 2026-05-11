package com.example.smartwallet.entities;

public class FinanceProfile {
    private int id;
    private int userId;
    private double monthlyIncome;
    private double currentBalance;
    private String currency;

    public FinanceProfile() {}

    public FinanceProfile(int id, int userId, double monthlyIncome, double currentBalance, String currency) {
        this.id = id;
        this.userId = userId;
        this.monthlyIncome = monthlyIncome;
        this.currentBalance = currentBalance;
        this.currency = currency;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public double getCurrentBalance() { return currentBalance; }
    public String getCurrency() { return currency; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }
    public void setCurrency(String currency) { this.currency = currency; }
}