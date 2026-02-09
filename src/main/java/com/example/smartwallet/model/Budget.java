package com.example.smartwallet.model;

import javax.persistence.*;

@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montantMax;
    private Integer mois;
    private Integer annee;

    @ManyToOne
    private User user;

    @ManyToOne
    private Categorie categorie;
}
