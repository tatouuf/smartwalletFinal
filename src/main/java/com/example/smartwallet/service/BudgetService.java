package com.example.smartwallet.service;

import com.example.smartwallet.model.Budget;
import dao.BudgetDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetDAO budgetDAO;

    public BudgetService() {
        this.budgetDAO = new BudgetDAO();
    }

    /**
     * Ajouter un nouveau budget
     */
    public void save(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Le budget ne peut pas être null");
        }
        budgetDAO.ajouterBudget(budget);
    }

    /**
     * Récupérer tous les budgets d'un utilisateur
     */
    public List<Budget> all(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return budgetDAO.obtenirTousBudgets(userId);
    }

    /**
     * Récupérer les budgets d'un mois spécifique
     */
    public List<Budget> getBudgetsByMonth(int userId, int mois, int annee) {
        if (userId <= 0 || mois < 1 || mois > 12 || annee <= 0) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        return budgetDAO.obtenirBudgetsMois(userId, mois, annee);
    }

    /**
     * Récupérer un budget par catégorie
     */
    public Budget getBudgetByCategory(int userId, String categorie, int mois, int annee) {
        if (userId <= 0 || categorie == null || categorie.isEmpty()) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        return budgetDAO.obtenirBudgetParCategorie(userId, categorie, mois, annee);
    }

    /**
     * Obtenir le total des budgets
     */
    public double getTotalBudgets(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return budgetDAO.getTotalBudgets(userId);
    }

    /**
     * Modifier un budget existant
     */
    public void update(Budget budget) {
        if (budget == null || budget.getId() <= 0) {
            throw new IllegalArgumentException("Le budget est invalide");
        }
        budgetDAO.modifierBudget(budget);
    }

    /**
     * Supprimer un budget
     */
    public void delete(int budgetId) {
        if (budgetId <= 0) {
            throw new IllegalArgumentException("L'ID du budget doit être positif");
        }
        budgetDAO.supprimerBudget(budgetId);
    }

    /**
     * Mettre à jour le montant actuel utilisé
     */
    public void updateUsedAmount(int budgetId, double montant) {
        if (budgetId <= 0 || montant < 0) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        budgetDAO.mettreAJourMontantActuel(budgetId, montant);
    }
}
