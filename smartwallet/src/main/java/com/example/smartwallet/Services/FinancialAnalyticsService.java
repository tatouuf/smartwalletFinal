package com.example.smartwallet.Services;

import com.example.smartwallet.entities.DashboardResult;
import com.example.smartwallet.entities.RecurringPayment;

import java.util.List;

public class FinancialAnalyticsService {

    public DashboardResult compute(double monthlyBudget, double savingsGoal, List<RecurringPayment> recurring) {

        double recurringMonthly = 0.0;

        if (recurring != null) {
            for (RecurringPayment rp : recurring) {
                if (!rp.isActive()) continue;

                // On ramène tout en "monthly equivalent"
                double a = rp.getAmount();
                switch (rp.getFrequency()) {
                    case WEEKLY -> recurringMonthly += (a * 4.0);
                    case MONTHLY -> recurringMonthly += a;
                    case YEARLY -> recurringMonthly += (a / 12.0);
                }
            }
        }

        double remaining = monthlyBudget - recurringMonthly;

        int score = score(monthlyBudget, savingsGoal, recurringMonthly, remaining);
        String risk = (remaining < 0) ? "HIGH" : (remaining < monthlyBudget * 0.2 ? "MEDIUM" : "LOW");

        return new DashboardResult(monthlyBudget, savingsGoal, score, risk, recurringMonthly, remaining);
    }

    private int score(double monthlyBudget, double savingsGoal, double recurringMonthly, double remaining) {
        if (monthlyBudget <= 0) return 10;

        double ratio = recurringMonthly / monthlyBudget; // part fixe
        int s = 100;

        if (ratio > 0.7) s -= 40;
        else if (ratio > 0.5) s -= 25;
        else if (ratio > 0.35) s -= 12;

        if (remaining < 0) s -= 25;
        if (savingsGoal > remaining) s -= 10;

        if (s < 0) s = 0;
        if (s > 100) s = 100;
        return s;
    }
}