package com.example.smartwallet.controller;

import com.example.smartwallet.model.Depense;
import com.example.smartwallet.service.DepenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/depenses")
public class DepenseController {

    @Autowired
    private DepenseService depenseService;

    /**
     * Ajouter une nouvelle dépense
     * @param depense La dépense à ajouter
     */
    @PostMapping
    public void add(@RequestBody Depense depense) {
        depenseService.save(depense);
    }

    /**
     * Récupérer toutes les dépenses d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Liste des dépenses
     */
    @GetMapping("/user/{userId}")
    public List<Depense> getByUser(@PathVariable int userId) {
        return depenseService.all(userId);
    }

    /**
     * Récupérer les dépenses par catégorie
     * @param userId L'ID de l'utilisateur
     * @param categorie La catégorie
     * @return Liste des dépenses de la catégorie
     */
    @GetMapping("/user/{userId}/categorie/{categorie}")
    public List<Depense> getByCategory(@PathVariable int userId, @PathVariable String categorie) {
        return depenseService.getByCategory(userId, categorie);
    }

    /**
     * Récupérer les dépenses d'un mois
     * @param userId L'ID de l'utilisateur
     * @param mois Le mois
     * @param annee L'année
     * @return Liste des dépenses du mois
     */
    @GetMapping("/user/{userId}/mois/{mois}/annee/{annee}")
    public List<Depense> getByMonth(@PathVariable int userId, @PathVariable int mois, @PathVariable int annee) {
        return depenseService.getByMonth(userId, mois, annee);
    }

    /**
     * Obtenir le total des dépenses
     * @param userId L'ID de l'utilisateur
     * @return Le total des dépenses
     */
    @GetMapping("/user/{userId}/total")
    public double getTotalAmount(@PathVariable int userId) {
        return depenseService.getTotalAmount(userId);
    }

    /**
     * Obtenir le total des dépenses d'un mois
     * @param userId L'ID de l'utilisateur
     * @param mois Le mois
     * @param annee L'année
     * @return Le total du mois
     */
    @GetMapping("/user/{userId}/total/mois/{mois}/annee/{annee}")
    public double getTotalByMonth(@PathVariable int userId, @PathVariable int mois, @PathVariable int annee) {
        return depenseService.getTotalByMonth(userId, mois, annee);
    }

    /**
     * Modifier une dépense
     * @param depense La dépense à modifier
     */
    @PutMapping
    public void update(@RequestBody Depense depense) {
        depenseService.update(depense);
    }

    /**
     * Supprimer une dépense
     * @param depenseId L'ID de la dépense à supprimer
     */
    @DeleteMapping("/{depenseId}")
    public void delete(@PathVariable int depenseId) {
        depenseService.delete(depenseId);
    }
}
