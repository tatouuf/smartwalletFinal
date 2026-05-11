package esprit.tn.souha_pi.entities;

import java.time.LocalDateTime;

public class Wallet {
    private int id;
    private int userId;
    private double balance;
    private String numeroCompte;
    private String type;
    private String status; // PENDING, ACTIF, REJECTED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Champs wallet supplémentaires
    private String cin;
    private String rib;
    private String adresse;
    private String rne;
    private String raisonSociale;
    private String matriculeFiscale;
    private String devise;
    private Double plafondJournalier;
    private Boolean sansContact;
    private Boolean paiementEtranger;
    private Boolean retraitDistributeur;
    private Boolean decouvertAutorise;
    private String couleurCarte;
    private String formule;

    // Champs utilisateur via joins
    private String userNom;
    private String userPrenom;
    private String userEmail;

    public Wallet() {
        this.type = "Standard";
        this.status = "PENDING";
        this.balance = 0.0;
        this.devise = "TND";
    }

    public Wallet(int id, int userId, double balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.type = "Standard";
        this.status = "PENDING";
        this.devise = "TND";
    }

    public Wallet(int id, int userId, double balance, String type, String status) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.type = type;
        this.status = status;
        this.devise = "TND";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }


    public String getRib() {
        return rib;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }


    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }


    public String getRne() {
        return rne;
    }

    public void setRne(String rne) {
        this.rne = rne;
    }


    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }


    public String getMatriculeFiscale() {
        return matriculeFiscale;
    }

    public void setMatriculeFiscale(String matriculeFiscale) {
        this.matriculeFiscale = matriculeFiscale;
    }


    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }


    public Double getPlafondJournalier() {
        return plafondJournalier;
    }

    public void setPlafondJournalier(Double plafondJournalier) {
        this.plafondJournalier = plafondJournalier;
    }


    public Boolean getSansContact() {
        return sansContact;
    }

    public void setSansContact(Boolean sansContact) {
        this.sansContact = sansContact;
    }


    public Boolean getPaiementEtranger() {
        return paiementEtranger;
    }

    public void setPaiementEtranger(Boolean paiementEtranger) {
        this.paiementEtranger = paiementEtranger;
    }


    public Boolean getRetraitDistributeur() {
        return retraitDistributeur;
    }

    public void setRetraitDistributeur(Boolean retraitDistributeur) {
        this.retraitDistributeur = retraitDistributeur;
    }


    public Boolean getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(Boolean decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }


    public String getCouleurCarte() {
        return couleurCarte;
    }

    public void setCouleurCarte(String couleurCarte) {
        this.couleurCarte = couleurCarte;
    }


    public String getFormule() {
        return formule;
    }

    public void setFormule(String formule) {
        this.formule = formule;
    }


    public String getUserNom() {
        return userNom;
    }

    public void setUserNom(String userNom) {
        this.userNom = userNom;
    }


    public String getUserPrenom() {
        return userPrenom;
    }

    public void setUserPrenom(String userPrenom) {
        this.userPrenom = userPrenom;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserFullName() {
        if (userNom != null && userPrenom != null) {
            return userPrenom + " " + userNom;
        } else if (userNom != null) {
            return userNom;
        } else if (userPrenom != null) {
            return userPrenom;
        } else {
            return "Utilisateur #" + userId;
        }
    }

    public boolean isActif() {
        return "ACTIF".equalsIgnoreCase(status);
    }

    public boolean isEnAttente() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isRejete() {
        return "REJECTED".equalsIgnoreCase(status);
    }

    public boolean isIncomplete() {
        return isEmpty(cin) || isEmpty(rib) || isEmpty(adresse);
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", userId=" + userId +
                ", balance=" + balance +
                ", numeroCompte='" + numeroCompte + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", rib='" + rib + '\'' +
                ", cin='" + cin + '\'' +
                ", devise='" + devise + '\'' +
                ", user=" + getUserFullName() +
                '}';
    }
}