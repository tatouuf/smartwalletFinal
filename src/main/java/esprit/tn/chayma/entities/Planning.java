package esprit.tn.chayma.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Planning {
    private int id;
    private int userId;
    private String nom;
    private String description;
    private String categorie;
    private double budgetTotal;
    private double depensesActuelles;
    private LocalDateTime dateCreation;
    private LocalDateTime dateEcheance;
    private String statut;
    private List<Depense> depenses;

    // Champs supplémentaires
    private Integer mois;
    private Integer annee;
    private double revenuPrevu;
    private double epargnePrevue;
    private Integer pourcentageEpargne;
    private String type;

    public Planning() {
        this.depenses = new ArrayList<>();
        this.depensesActuelles = 0;
        this.dateCreation = LocalDateTime.now();
        this.statut = "EN_COURS";
    }

    public Planning(int id, int userId, String nom, String description, String categorie,
                    double budgetTotal, LocalDateTime dateEcheance) {
        this.id = id;
        this.userId = userId;
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.budgetTotal = budgetTotal;
        this.dateEcheance = dateEcheance;
        this.depenses = new ArrayList<>();
        this.depensesActuelles = 0;
        this.dateCreation = LocalDateTime.now();
        this.statut = "EN_COURS";
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getBudgetTotal() { return budgetTotal; }
    public void setBudgetTotal(double budgetTotal) { this.budgetTotal = budgetTotal; }

    public double getDepensesActuelles() { return depensesActuelles; }
    public void setDepensesActuelles(double depensesActuelles) { this.depensesActuelles = depensesActuelles; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDateTime dateEcheance) { this.dateEcheance = dateEcheance; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public List<Depense> getDepenses() { return depenses; }
    public void setDepenses(List<Depense> depenses) { this.depenses = depenses; }

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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Méthodes utilitaires
    public double getReste() {
        return budgetTotal - depensesActuelles;
    }

    public double getPourcentage() {
        if (budgetTotal == 0) return 0;
        return (depensesActuelles / budgetTotal) * 100;
    }

    public void ajouterDepense(Depense depense) {
        if (depense != null) {
            this.depenses.add(depense);
            this.depensesActuelles += depense.getMontant();
            if (this.depensesActuelles > this.budgetTotal) {
                this.statut = "DEPASSE";
            } else if (this.depensesActuelles == this.budgetTotal) {
                this.statut = "TERMINE";
            }
        }
    }

    public String getTypeServiceString() {
        return type != null ? type : "Standard";
    }

    public String getStatutString() {
        return statut != null ? statut : "EN_COURS";
    }

    @Override
    public String toString() {
        return String.format("📋 %s - %.2f/%.2f DT - %s",
                nom, depensesActuelles, budgetTotal, statut);
    }
}