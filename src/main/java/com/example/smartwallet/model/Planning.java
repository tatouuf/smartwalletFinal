package com.example.smartwallet.model;

import java.time.LocalDate;

public class Planning {
    private int id;
    private int userId;
    private String nom;
    private String description;
    private String type;
    private int mois;
    private int annee;
    private double revenuPrevu;
    private double epargnePrevue;
    private int pourcentageEpargne;
    private String statut;
    private LocalDate dateCreation;
    private LocalDate dateModification;

    public Planning() {}

    public Planning(String nom, String description, String type, int mois, int annee, double revenuPrevu, double epargnePrevue, int pourcentageEpargne, String statut, int userId) {
        this.nom = nom;
        this.description = description;
        this.type = type;
        this.mois = mois;
        this.annee = annee;
        this.revenuPrevu = revenuPrevu;
        this.epargnePrevue = epargnePrevue;
        this.pourcentageEpargne = pourcentageEpargne;
        this.statut = statut;
        this.userId = userId;
    }

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

    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public double getRevenuPrevu() { return revenuPrevu; }
    public void setRevenuPrevu(double revenuPrevu) { this.revenuPrevu = revenuPrevu; }

    public double getEpargnePrevue() { return epargnePrevue; }
    public void setEpargnePrevue(double epargnePrevue) { this.epargnePrevue = epargnePrevue; }

    public int getPourcentageEpargne() { return pourcentageEpargne; }
    public void setPourcentageEpargne(int pourcentageEpargne) { this.pourcentageEpargne = pourcentageEpargne; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public LocalDate getDateModification() { return dateModification; }
    public void setDateModification(LocalDate dateModification) { this.dateModification = dateModification; }
}
