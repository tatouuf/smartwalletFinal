package com.example.smartwallet.controller;

import com.example.smartwallet.model.Budget;
import com.example.smartwallet.service.BudgetService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    /**
     * Ajouter un nouveau budget
     * @param budget Le budget à ajouter
     */
    @PostMapping
    public void add(@RequestBody Budget budget) {
        budgetService.save(budget);
    }

    /**
     * Récupérer tous les budgets d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Liste des budgets
     */
    @GetMapping("/user/{userId}")
    public List<Budget> getAll(@PathVariable int userId) {
        return budgetService.all(userId);
    }

    /**
     * Récupérer les budgets d'un mois
     * @param userId L'ID de l'utilisateur
     * @param mois Le mois
     * @param annee L'année
     * @return Liste des budgets du mois
     */
    @GetMapping("/user/{userId}/mois/{mois}/annee/{annee}")
    public List<Budget> getByMonth(@PathVariable int userId, @PathVariable int mois, @PathVariable int annee) {
        return budgetService.getBudgetsByMonth(userId, mois, annee);
    }

    /**
     * Récupérer un budget par catégorie
     * @param userId L'ID de l'utilisateur
     * @param categorie La catégorie
     * @param mois Le mois
     * @param annee L'année
     * @return Le budget trouvé
     */
    @GetMapping("/user/{userId}/categorie/{categorie}/mois/{mois}/annee/{annee}")
    public Budget getByCategory(@PathVariable int userId, @PathVariable String categorie,
                                @PathVariable int mois, @PathVariable int annee) {
        return budgetService.getBudgetByCategory(userId, categorie, mois, annee);
    }

    /**
     * Obtenir le total des budgets
     * @param userId L'ID de l'utilisateur
     * @return Le total des budgets
     */
    @GetMapping("/user/{userId}/total")
    public double getTotalBudgets(@PathVariable int userId) {
        return budgetService.getTotalBudgets(userId);
    }

    /**
     * Modifier un budget
     * @param budget Le budget à modifier
     */
    @PutMapping
    public void update(@RequestBody Budget budget) {
        budgetService.update(budget);
    }

    /**
     * Supprimer un budget
     * @param budgetId L'ID du budget à supprimer
     */
    @DeleteMapping("/{budgetId}")
    public void delete(@PathVariable int budgetId) {
        budgetService.delete(budgetId);
    }

    /**
     * Mettre à jour le montant utilisé du budget
     * @param budgetId L'ID du budget
     * @param montant Le montant à ajouter
     */
    @PutMapping("/{budgetId}/update-amount/{montant}")
    public void updateUsedAmount(@PathVariable int budgetId, @PathVariable double montant) {
        budgetService.updateUsedAmount(budgetId, montant);
    }
}
