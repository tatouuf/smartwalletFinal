package esprit.tn.souha_pi.entities;

import java.time.LocalDate;

public class Budget {
    private int id;
    private int userId;
    private Integer categorieId;
    private double montantMax;
    private Integer mois;
    private Integer annee;
    private Integer planningId;
    private double montantActuel;
    private String description;
    private LocalDate dateCreation;
    private String categorie;

    public Budget() {}

    // Constructeur minimal
    public Budget(int id, int userId, double montantMax, Integer mois, Integer annee, String categorie) {
        this.id = id;
        this.userId = userId;
        this.montantMax = montantMax;
        this.mois = mois;
        this.annee = annee;
        this.categorie = categorie;
    }

    // Getters / Setters
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

    public double getMontantActuel() { return montantActuel; }
    public void setMontantActuel(double montantActuel) { this.montantActuel = montantActuel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
}
