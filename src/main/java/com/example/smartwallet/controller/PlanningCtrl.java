package com.example.smartwallet.controller;

import com.example.smartwallet.model.Planning;
import com.example.smartwallet.service.PlanningService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
@RequestMapping("/api/plannings")
public class PlanningCtrl {
    private static final Logger LOGGER = Logger.getLogger(PlanningCtrl.class.getName());

    @Autowired
    private PlanningService planningService;

    @PostMapping
    public void add(@RequestBody Planning planning) {
        try {
            if (planning == null || planning.getNom() == null || planning.getNom().isEmpty()) {
                LOGGER.log(Level.WARNING, "Planning invalide: nom manquant");
                return;
            }
            planningService.save(planning);
            LOGGER.log(Level.INFO, "Planning ajoute avec succes: " + planning.getNom());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du planning", e);
        }
    }

    @GetMapping("/user/{userId}")
    public List<Planning> getAll(@PathVariable int userId) {
        try {
            if (userId <= 0) {
                LOGGER.log(Level.WARNING, "userId invalide: " + userId);
                return new ArrayList<>();
            }
            List<Planning> plannings = planningService.all(userId);
            LOGGER.log(Level.INFO, "Recuperation de " + plannings.size() + " plannings pour userId: " + userId);
            return plannings;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recuperation des plannings", e);
            return new ArrayList<>();
        }
    }

    @GetMapping("/user/{userId}/mois/{mois}/annee/{annee}")
    public List<Planning> getByMonth(@PathVariable int userId, @PathVariable int mois, @PathVariable int annee) {
        try {
            if (userId <= 0 || mois < 1 || mois > 12 || annee < 2000) {
                LOGGER.log(Level.WARNING, "Parametres invalides: userId=" + userId + ", mois=" + mois + ", annee=" + annee);
                return new ArrayList<>();
            }
            List<Planning> plannings = planningService.getByMonth(userId, mois, annee);
            LOGGER.log(Level.INFO, "Plannings recuperes pour " + mois + "/" + annee + ": " + plannings.size());
            return plannings;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recuperation des plannings du mois", e);
            return new ArrayList<>();
        }
    }

    @GetMapping("/user/{userId}/count")
    public int getTotalCount(@PathVariable int userId) {
        try {
            if (userId <= 0) {
                LOGGER.log(Level.WARNING, "userId invalide: " + userId);
                return 0;
            }
            int count = planningService.getTotalCount(userId);
            LOGGER.log(Level.INFO, "Nombre de plannings pour userId " + userId + ": " + count);
            return count;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des plannings", e);
            return 0;
        }
    }

    @PutMapping
    public void update(@RequestBody Planning planning) {
        try {
            if (planning == null || planning.getId() <= 0) {
                LOGGER.log(Level.WARNING, "Planning invalide pour modification");
                return;
            }
            planningService.update(planning);
            LOGGER.log(Level.INFO, "Planning modifie: ID=" + planning.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du planning", e);
        }
    }

    @DeleteMapping("/{planningId}")
    public void delete(@PathVariable int planningId) {
        try {
            if (planningId <= 0) {
                LOGGER.log(Level.WARNING, "planningId invalide: " + planningId);
                return;
            }
            planningService.delete(planningId);
            LOGGER.log(Level.INFO, "Planning supprime: ID=" + planningId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du planning", e);
        }
    }

    @PostMapping("/validate")
    public boolean validate(@RequestBody Planning planning) {
        try {
            if (planning == null) {
                LOGGER.log(Level.WARNING, "Planning null pour validation");
                return false;
            }
            boolean isValid = planningService.validate(planning);
            LOGGER.log(Level.INFO, "Validation du planning: " + (isValid ? "Valide" : "Invalide"));
            return isValid;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la validation du planning", e);
            return false;
        }
    }

    @PostMapping("/savings-rate")
    public double getSavingsRate(@RequestBody Planning planning) {
        try {
            if (planning == null) {
                LOGGER.log(Level.WARNING, "Planning null pour calcul du taux d'epargne");
                return 0.0;
            }
            double rate = planningService.getSavingsRate(planning);
            LOGGER.log(Level.INFO, "Taux d'epargne calcule: " + rate + "%");
            return rate;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du taux d'epargne", e);
            return 0.0;
        }
    }

    @PostMapping("/is-completed")
    public boolean isCompleted(@RequestBody Planning planning) {
        try {
            if (planning == null) {
                LOGGER.log(Level.WARNING, "Planning null pour verifier completude");
                return false;
            }
            boolean completed = planningService.isCompleted(planning);
            LOGGER.log(Level.INFO, "Statut de completude: " + completed);
            return completed;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la verification de completude", e);
            return false;
        }
    }

    @PostMapping("/is-active")
    public boolean isActive(@RequestBody Planning planning) {
        try {
            if (planning == null) {
                LOGGER.log(Level.WARNING, "Planning null pour verifier l'activite");
                return false;
            }
            boolean active = planningService.isActive(planning);
            LOGGER.log(Level.INFO, "Statut d'activite: " + active);
            return active;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la verification d'activite", e);
            return false;
        }
    }
}

