package com.example.smartwallet.service;

import com.example.smartwallet.model.Depense;
import dao.DepenseDAO;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class DepenseService {

    private final DepenseDAO depenseDAO;

    public DepenseService() {
        this.depenseDAO = new DepenseDAO();
    }

    /**
     * Ajouter une nouvelle dépense
     */
    public void save(Depense depense) {
        if (depense == null) {
            throw new IllegalArgumentException("La dépense ne peut pas être null");
        }
        if (depense.getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        if (depense.getDescription() == null || depense.getDescription().isEmpty()) {
            throw new IllegalArgumentException("La description ne peut pas être vide");
        }
        depenseDAO.ajouterDepense(depense);
    }

    /**
     * Récupérer toutes les dépenses d'un utilisateur
     */
    public List<Depense> all(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return depenseDAO.obtenirToutesDepenses(userId);
    }

    /**
     * Récupérer les dépenses par catégorie
     */
    public List<Depense> getByCategory(int userId, String categorie) {
        if (userId <= 0 || categorie == null || categorie.isEmpty()) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        return depenseDAO.obtenirDepensesParCategorie(userId, categorie);
    }

    /**
     * Récupérer les dépenses d'un mois spécifique
     */
    public List<Depense> getByMonth(int userId, int mois, int annee) {
        if (userId <= 0 || mois < 1 || mois > 12 || annee <= 0) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        return depenseDAO.obtenirDepensesMois(userId, mois, annee);
    }

    /**
     * Obtenir le total des dépenses
     */
    public double getTotalAmount(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return depenseDAO.getTotalDepenses(userId);
    }

    /**
     * Obtenir le total des dépenses pour un mois
     */
    public double getTotalByMonth(int userId, int mois, int annee) {
        if (userId <= 0 || mois < 1 || mois > 12 || annee <= 0) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        return depenseDAO.getTotalDepensesMois(userId, mois, annee);
    }

    /**
     * Modifier une dépense existante
     */
    public void update(Depense depense) {
        if (depense == null || depense.getId() <= 0) {
            throw new IllegalArgumentException("La dépense est invalide");
        }
        if (depense.getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        depenseDAO.modifierDepense(depense);
    }

    /**
     * Supprimer une dépense
     */
    public void delete(int depenseId) {
        if (depenseId <= 0) {
            throw new IllegalArgumentException("L'ID de la dépense doit être positif");
        }
        depenseDAO.supprimerDepense(depenseId);
    }

    /**
     * Valider une dépense
     */
    public boolean validate(Depense depense) {
        return depense != null &&
               depense.getMontant() > 0 &&
               depense.getDescription() != null && !depense.getDescription().isEmpty() &&
               depense.getDateDepense() != null &&
               depense.getDateDepense().isBefore(LocalDate.now().plusDays(1));
    }
}

