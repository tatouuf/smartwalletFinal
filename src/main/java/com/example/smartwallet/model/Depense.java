package com.example.smartwallet.model;

import java.time.LocalDate;

public class Depense {

    private int id;
    private double montant;
    private String description;
    private LocalDate dateDepense;
    private String categorie;
    private int userId;

    public Depense() {}

    public Depense(double montant, String description, LocalDate dateDepense) {
        this.montant = montant;
        this.description = description;
        this.dateDepense = dateDepense;
    }

    public Depense(int id, double montant, String description, LocalDate dateDepense, String categorie) {
        this.id = id;
        this.montant = montant;
        this.description = description;
        this.dateDepense = dateDepense;
        this.categorie = categorie;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateDepense() { return dateDepense; }
    public void setDateDepense(LocalDate dateDepense) { this.dateDepense = dateDepense; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
