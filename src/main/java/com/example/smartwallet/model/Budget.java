package com.example.smartwallet.model;

import java.time.LocalDate;

public class Budget {

    private int id;
    private String categorie;
    private double montantMax;
    private double montantActuel;
    private int mois;
    private int annee;
    private int userId;
    private String description;
    private LocalDate dateCreation;

    public Budget() {}

    public Budget(String categorie, double montantMax, int mois, int annee) {
        this.categorie = categorie;
        this.montantMax = montantMax;
        this.mois = mois;
        this.annee = annee;
        this.montantActuel = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getMontantMax() { return montantMax; }
    public void setMontantMax(double montantMax) { this.montantMax = montantMax; }

    public double getMontantActuel() { return montantActuel; }
    public void setMontantActuel(double montantActuel) { this.montantActuel = montantActuel; }

    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}
