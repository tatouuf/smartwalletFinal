package esprit.tn.chayma.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Depense {
    private int id;
    private int planningId;
    private int userId;
    private String description;
    private double montant;
    private String categorie;
    private LocalDate dateDepense;
    private LocalDateTime dateCreation;
    private String commentaire;

    // Constructeurs
    public Depense() {
        this.dateDepense = LocalDate.now();
        this.dateCreation = LocalDateTime.now();
    }

    // Constructeur utilisé dans PlanningService.initTestData()
    public Depense(int id, int planningId, String description, double montant, String categorie, String commentaire) {
        this.id = id;
        this.planningId = planningId;
        this.description = description;
        this.montant = montant;
        this.categorie = categorie;
        this.commentaire = commentaire;
        this.dateDepense = LocalDate.now();
        this.dateCreation = LocalDateTime.now();
    }

    // Constructeur utilisé dans DepensesClientController
    public Depense(double montant, String description, LocalDate dateDepense, String categorie, int userId) {
        this.montant = montant;
        this.description = description;
        this.dateDepense = dateDepense;
        this.categorie = categorie;
        this.userId = userId;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlanningId() { return planningId; }
    public void setPlanningId(int planningId) { this.planningId = planningId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public LocalDate getDateDepense() { return dateDepense; }
    public void setDateDepense(LocalDate dateDepense) { this.dateDepense = dateDepense; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    @Override
    public String toString() {
        return String.format("%s: %.2f DT - %s", description, montant, dateDepense != null ? dateDepense : "");
    }
}