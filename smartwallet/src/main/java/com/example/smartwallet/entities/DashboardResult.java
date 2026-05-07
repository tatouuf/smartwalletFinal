package com.example.smartwallet.entities;

public class DashboardResult {

    private double monthlyBudget;
    private double savingsGoal;

    private int score;                 // 0..100
    private String riskLevel;          // LOW / MEDIUM / HIGH

    private double recurringMonthly;   // total abonnements / mois
    private double remainingThisMonth; // budget restant

    public DashboardResult(double monthlyBudget, double savingsGoal, int score, String riskLevel,
                           double recurringMonthly, double remainingThisMonth) {
        this.monthlyBudget = monthlyBudget;
        this.savingsGoal = savingsGoal;
        this.score = score;
        this.riskLevel = riskLevel;
        this.recurringMonthly = recurringMonthly;
        this.remainingThisMonth = remainingThisMonth;
    }

    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double monthlyBudget) { this.monthlyBudget = monthlyBudget; }

    public double getSavingsGoal() { return savingsGoal; }
    public void setSavingsGoal(double savingsGoal) { this.savingsGoal = savingsGoal; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public double getRecurringMonthly() { return recurringMonthly; }
    public void setRecurringMonthly(double recurringMonthly) { this.recurringMonthly = recurringMonthly; }

    public double getRemainingThisMonth() { return remainingThisMonth; }
    public void setRemainingThisMonth(double remainingThisMonth) { this.remainingThisMonth = remainingThisMonth; }
}