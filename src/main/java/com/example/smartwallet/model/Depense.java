package com.example.smartwallet.model;

// REMPLACE javax.persistence PAR jakarta.persistence

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "depenses")
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;
    private String description;
    private LocalDate dateDepense;

    // Si tu veux utiliser des relations ManyToOne, ajoute ces champs :
    // Exemple avec une relation ManyToOne vers User (si tu as une classe User)
    // @ManyToOne
    // @JoinColumn(name = "user_id")
    // private User user;

    // Constructeurs
    public Depense() {
        this.dateDepense = LocalDate.now();
    }

    public Depense(Double montant, String description) {
        this.montant = montant;
        this.description = description;
        this.dateDepense = LocalDate.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDepense() {
        return dateDepense;
    }

    public void setDateDepense(LocalDate dateDepense) {
        this.dateDepense = dateDepense;
    }

    @Override
    public String toString() {
        return "Depense{" +
                "id=" + id +
                ", montant=" + montant +
                ", description='" + description + '\'' +
                ", dateDepense=" + dateDepense +
                '}';
    }
}