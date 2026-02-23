package esprit.tn.chayma.entities;

import java.time.LocalDate;

public class Planning {
    private int id;
    private int userId;
    private String nom;
    private String description;
    private String type;
    private Integer mois;
    private Integer annee;
    private double revenuPrevu;
    private double epargnePrevue;
    private Integer pourcentageEpargne;
    private String statut; // EN_COURS, TERMINE, ANNULE
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Planning() {}
    @Override
    public String toString() {
        return nom + " - " + type + " (" + mois + "/" + annee + ")";
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public double getRevenuPrevu() { return revenuPrevu; }
    public void setRevenuPrevu(double revenuPrevu) { this.revenuPrevu = revenuPrevu; }

    public double getEpargnePrevue() { return epargnePrevue; }
    public void setEpargnePrevue(double epargnePrevue) { this.epargnePrevue = epargnePrevue; }

    public Integer getPourcentageEpargne() { return pourcentageEpargne; }
    public void setPourcentageEpargne(Integer pourcentageEpargne) { this.pourcentageEpargne = pourcentageEpargne; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}
