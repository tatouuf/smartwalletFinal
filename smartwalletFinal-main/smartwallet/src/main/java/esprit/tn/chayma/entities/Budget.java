package esprit.tn.chayma.entities;

import java.time.LocalDate;

public class Budget {
    private int id;
    private int userId;
    private Integer categorieId;  // Changé de int à Integer pour accepter null
    private double montantMax;
    private Integer mois;         // Changé de int à Integer pour accepter null
    private Integer annee;        // Changé de int à Integer pour accepter null
    private Integer planningId;

    // NOUVEAUX CHAMPS
    private double montantActuel;  // Pour suivre le montant dépensé
    private String categorie;      // Nom de la catégorie (pour affichage)
    private String description;
    private LocalDate dateCreation;



    // Constructeurs
    public Budget() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getCategorieId() { return categorieId; }
    public void setCategorieId(Integer categorieId) { this.categorieId = categorieId; }

    public double getMontantMax() { return montantMax; }
    public void setMontantMax(double montantMax) { this.montantMax = montantMax; }

    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Integer getPlanningId() { return planningId; }
    public void setPlanningId(Integer planningId) { this.planningId = planningId; }

    // NOUVEAUX GETTERS/SETTERS
    public double getMontantActuel() { return montantActuel; }
    public void setMontantActuel(double montantActuel) { this.montantActuel = montantActuel; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}