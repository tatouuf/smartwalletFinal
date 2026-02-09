package com.example.smartwallet.model;

import javax.persistence.*;

@Entity
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private Double revenuPrevu;
    private Double epargnePrevue;
    private Integer mois;
    private Integer annee;

    @ManyToOne
    private User user;
}
