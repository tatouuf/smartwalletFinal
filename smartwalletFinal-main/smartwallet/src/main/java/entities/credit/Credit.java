
package entities.credit;

import java.time.LocalDate;

public class Credit {

    // ===========================
    // 🔹 Attributs
    // ===========================
    private int idCredit;
    private String nomClient;
    private float montant;
    private LocalDate dateCredit;
    private String description;
    private StatutCredit statut;

    // ===========================
    // 🔹 Constructeurs
    // ===========================
    public Credit() {}

    public Credit(int idCredit, String nomClient, float montant,
                  LocalDate dateCredit, String description, StatutCredit statut) {
        this.idCredit = idCredit;
        this.nomClient = nomClient;
        this.montant = montant;
        this.dateCredit = dateCredit;
        this.description = description;
        this.statut = statut;
    }

    public Credit(String nomClient, float montant,
                  LocalDate dateCredit, String description, StatutCredit statut) {
        this.nomClient = nomClient;
        this.montant = montant;
        this.dateCredit = dateCredit;
        this.description = description;
        this.statut = statut;
    }

    // ===========================
    // 🔹 Getters & Setters
    // ===========================
    public int getIdCredit() { return idCredit; }
    public void setIdCredit(int idCredit) { this.idCredit = idCredit; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public float getMontant() { return montant; }
    public void setMontant(float montant) { this.montant = montant; }

    public LocalDate getDateCredit() { return dateCredit; }
    public void setDateCredit(LocalDate dateCredit) { this.dateCredit = dateCredit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatutCredit getStatut() { return statut; }
    public void setStatut(StatutCredit statut) { this.statut = statut; }

    // ===========================
    // 🔹 toString
    // ===========================
    @Override
    public String toString() {
        return "Credit{" +
                "idCredit=" + idCredit +
                ", nomClient='" + nomClient + '\'' +
                ", montant=" + montant +
                ", dateCredit=" + dateCredit +
                ", description='" + description + '\'' +
                ", statut=" + statut +
                '}';
    }
}

