package com.example.smartwallet.service;

import dao.DepenseDAO;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class ExpensePredictionService {

    private final DepenseDAO depenseDAO = new DepenseDAO();

    /**
     * Prédit les dépenses pour le mois prochain en utilisant une régression linéaire simple.
     * @param userId L'ID de l'utilisateur
     * @return Le montant prédit pour le mois prochain
     */
    public double predictNextMonthExpenses(int userId) {
        Map<String, Double> monthlyTotals = depenseDAO.getMonthlyTotals(userId);
        
        if (monthlyTotals.size() < 2) {
            // Pas assez de données pour prédire, on retourne la moyenne ou 0
            return monthlyTotals.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }

        List<Double> yValues = new ArrayList<>(monthlyTotals.values());
        List<Integer> xValues = new ArrayList<>();
        for (int i = 0; i < yValues.size(); i++) {
            xValues.add(i + 1);
        }

        // Calcul de la régression linéaire (y = mx + b)
        double n = yValues.size();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += xValues.get(i);
            sumY += yValues.get(i);
            sumXY += xValues.get(i) * yValues.get(i);
            sumX2 += xValues.get(i) * xValues.get(i);
        }

        double m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double b = (sumY - m * sumX) / n;

        // Prédiction pour le mois suivant (x = n + 1)
        double nextMonthX = n + 1;
        double prediction = m * nextMonthX + b;

        return Math.max(0, prediction); // On ne retourne pas de valeur négative
    }
}
