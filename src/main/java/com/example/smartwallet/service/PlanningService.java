package com.example.smartwallet.service;

import com.example.smartwallet.model.Planning;
import dao.PlanningDAO;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PlanningService {

    private final PlanningDAO planningDAO;

    public PlanningService() {
        this.planningDAO = new PlanningDAO();
    }

    /**
     * Ajouter un nouveau planning
     * @param planning Le planning à ajouter
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public void save(Planning planning) {
        if (planning == null) {
            throw new IllegalArgumentException("Le planning ne peut pas être null");
        }

        // Valider le nom
        if (planning.getNom() == null || planning.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du planning ne peut pas être vide");
        }

        // Valider le type
        if (planning.getType() == null || planning.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Le type du planning ne peut pas être vide");
        }

        // Valider le type est dans la liste autorisée
        if (!isValidType(planning.getType())) {
            throw new IllegalArgumentException("Type de planning invalide. Types autorisés: Personnel, Familial, Professionnel, Retraite");
        }

        // Valider les montants
        if (planning.getRevenuPrevu() < 0) {
            throw new IllegalArgumentException("Le revenu prévu ne peut pas être négatif");
        }

        if (planning.getEpargnePrevue() < 0) {
            throw new IllegalArgumentException("L'épargne prévue ne peut pas être négative");
        }

        // Valider le pourcentage d'épargne
        if (planning.getPourcentageEpargne() < 0 || planning.getPourcentageEpargne() > 100) {
            throw new IllegalArgumentException("Le pourcentage d'épargne doit être entre 0 et 100");
        }

        // Valider le statut
        if (planning.getStatut() == null || planning.getStatut().trim().isEmpty()) {
            throw new IllegalArgumentException("Le statut du planning ne peut pas être vide");
        }

        if (!isValidStatus(planning.getStatut())) {
            throw new IllegalArgumentException("Statut invalide. Statuts autorisés: En cours, Terminé, Suspendu, Annulé");
        }

        // Valider les mois et années
        if (planning.getMois() < 1 || planning.getMois() > 12) {
            throw new IllegalArgumentException("Le mois doit être entre 1 et 12");
        }

        if (planning.getAnnee() <= 0) {
            throw new IllegalArgumentException("L'année doit être positive");
        }

        // Valider l'épargne prévue <= revenu prévu
        if (planning.getEpargnePrevue() > planning.getRevenuPrevu()) {
            throw new IllegalArgumentException("L'épargne prévue ne peut pas dépasser le revenu prévu");
        }

        // Définir la date de création
        planning.setDateCreation(LocalDate.now());
        planning.setDateModification(LocalDate.now());

        planningDAO.ajouterPlanning(planning);
    }

    /**
     * Récupérer tous les plannings d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Liste des plannings de l'utilisateur
     * @throws IllegalArgumentException si l'ID utilisateur est invalide
     */
    public List<Planning> all(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return planningDAO.obtenirTousPlannings(userId);
    }

    /**
     * Récupérer les plannings d'un mois spécifique
     * @param userId L'ID de l'utilisateur
     * @param mois Le mois (1-12)
     * @param annee L'année
     * @return Liste des plannings du mois
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public List<Planning> getByMonth(int userId, int mois, int annee) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }

        if (mois < 1 || mois > 12) {
            throw new IllegalArgumentException("Le mois doit être entre 1 et 12");
        }

        if (annee <= 0) {
            throw new IllegalArgumentException("L'année doit être positive");
        }

        return planningDAO.obtenirPlanningsMois(userId, mois, annee);
    }

    /**
     * Obtenir le nombre total de plannings
     * @param userId L'ID de l'utilisateur
     * @return Le nombre total de plannings
     * @throws IllegalArgumentException si l'ID utilisateur est invalide
     */
    public int getTotalCount(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        return planningDAO.getTotalPlannings(userId);
    }

    /**
     * Modifier un planning existant
     * @param planning Le planning à modifier
     * @throws IllegalArgumentException si le planning est invalide
     */
    public void update(Planning planning) {
        if (planning == null) {
            throw new IllegalArgumentException("Le planning ne peut pas être null");
        }

        if (planning.getId() <= 0) {
            throw new IllegalArgumentException("L'ID du planning doit être positif");
        }

        // Valider le nom
        if (planning.getNom() == null || planning.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du planning ne peut pas être vide");
        }

        // Valider le type
        if (planning.getType() == null || planning.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Le type du planning ne peut pas être vide");
        }

        if (!isValidType(planning.getType())) {
            throw new IllegalArgumentException("Type de planning invalide");
        }

        // Valider les montants
        if (planning.getRevenuPrevu() < 0) {
            throw new IllegalArgumentException("Le revenu prévu ne peut pas être négatif");
        }

        if (planning.getEpargnePrevue() < 0) {
            throw new IllegalArgumentException("L'épargne prévue ne peut pas être négative");
        }

        // Valider le pourcentage
        if (planning.getPourcentageEpargne() < 0 || planning.getPourcentageEpargne() > 100) {
            throw new IllegalArgumentException("Le pourcentage doit être entre 0 et 100");
        }

        // Valider le statut
        if (planning.getStatut() == null || planning.getStatut().trim().isEmpty()) {
            throw new IllegalArgumentException("Le statut ne peut pas être vide");
        }

        if (!isValidStatus(planning.getStatut())) {
            throw new IllegalArgumentException("Statut invalide");
        }

        // Valider l'épargne <= revenu
        if (planning.getEpargnePrevue() > planning.getRevenuPrevu()) {
            throw new IllegalArgumentException("L'épargne ne peut pas dépasser le revenu");
        }

        // Définir la date de modification
        planning.setDateModification(LocalDate.now());

        planningDAO.modifierPlanning(planning);
    }

    /**
     * Supprimer un planning
     * @param planningId L'ID du planning à supprimer
     * @throws IllegalArgumentException si l'ID est invalide
     */
    public void delete(int planningId) {
        if (planningId <= 0) {
            throw new IllegalArgumentException("L'ID du planning doit être positif");
        }
        planningDAO.supprimerPlanning(planningId);
    }

    /**
     * Valider un planning
     * @param planning Le planning à valider
     * @return true si le planning est valide, false sinon
     */
    public boolean validate(Planning planning) {
        if (planning == null) {
            return false;
        }

        // Vérifier le nom
        if (planning.getNom() == null || planning.getNom().trim().isEmpty()) {
            return false;
        }

        // Vérifier le type
        if (planning.getType() == null || !isValidType(planning.getType())) {
            return false;
        }

        // Vérifier les montants
        if (planning.getRevenuPrevu() < 0 || planning.getEpargnePrevue() < 0) {
            return false;
        }

        // Vérifier l'épargne ne dépasse pas revenu
        if (planning.getEpargnePrevue() > planning.getRevenuPrevu()) {
            return false;
        }

        // Vérifier le pourcentage
        if (planning.getPourcentageEpargne() < 0 || planning.getPourcentageEpargne() > 100) {
            return false;
        }

        // Vérifier le statut
        if (planning.getStatut() == null || !isValidStatus(planning.getStatut())) {
            return false;
        }

        // Vérifier les mois et années
        if (planning.getMois() < 1 || planning.getMois() > 12 || planning.getAnnee() <= 0) {
            return false;
        }

        return true;
    }

    /**
     * Obtenir le taux d'épargne en pourcentage
     * @param planning Le planning
     * @return Le taux d'épargne ou 0.0 si revenu est 0
     */
    public double getSavingsRate(Planning planning) {
        if (planning == null || planning.getRevenuPrevu() <= 0) {
            return 0.0;
        }
        return (planning.getEpargnePrevue() / planning.getRevenuPrevu()) * 100;
    }

    /**
     * Vérifier si un planning est complété
     * @param planning Le planning
     * @return true si le planning est complété, false sinon
     */
    public boolean isCompleted(Planning planning) {
        return planning != null && "Terminé".equalsIgnoreCase(planning.getStatut());
    }

    /**
     * Vérifier si un planning est suspendu
     * @param planning Le planning
     * @return true si le planning est suspendu, false sinon
     */
    public boolean isSuspended(Planning planning) {
        return planning != null && "Suspendu".equalsIgnoreCase(planning.getStatut());
    }

    /**
     * Vérifier si un planning est en cours
     * @param planning Le planning
     * @return true si le planning est en cours, false sinon
     */
    public boolean isActive(Planning planning) {
        return planning != null && "En cours".equalsIgnoreCase(planning.getStatut());
    }

    /**
     * Vérifier si un planning est annulé
     * @param planning Le planning
     * @return true si le planning est annulé, false sinon
     */
    public boolean isCancelled(Planning planning) {
        return planning != null && "Annulé".equalsIgnoreCase(planning.getStatut());
    }

    /**
     * Vérifier si un type de planning est valide
     * @param type Le type à vérifier
     * @return true si le type est valide, false sinon
     */
    private boolean isValidType(String type) {
        if (type == null) {
            return false;
        }
        return type.equalsIgnoreCase("Personnel") ||
               type.equalsIgnoreCase("Familial") ||
               type.equalsIgnoreCase("Professionnel") ||
               type.equalsIgnoreCase("Retraite");
    }

    /**
     * Vérifier si un statut est valide
     * @param status Le statut à vérifier
     * @return true si le statut est valide, false sinon
     */
    private boolean isValidStatus(String status) {
        if (status == null) {
            return false;
        }
        return status.equalsIgnoreCase("En cours") ||
               status.equalsIgnoreCase("Terminé") ||
               status.equalsIgnoreCase("Suspendu") ||
               status.equalsIgnoreCase("Annulé");
    }

    /**
     * Calculer le montant à épargner en fonction du revenu
     * @param revenu Le revenu prévu
     * @param pourcentage Le pourcentage d'épargne
     * @return Le montant à épargner
     */
    public double calculateSavingsAmount(double revenu, int pourcentage) {
        if (revenu < 0 || pourcentage < 0 || pourcentage > 100) {
            return 0.0;
        }
        return (revenu * pourcentage) / 100.0;
    }

    /**
     * Vérifier si l'épargne est suffisante par rapport au revenu
     * @param revenu Le revenu prévu
     * @param epargne L'épargne prévue
     * @return true si l'épargne est valide, false sinon
     */
    public boolean isSavingsValid(double revenu, double epargne) {
        return revenu >= 0 && epargne >= 0 && epargne <= revenu;
    }
}

