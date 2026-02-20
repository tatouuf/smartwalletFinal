package entities;

import java.time.LocalDateTime;

public class Users {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String password;
    private Role role;              // Enum dans package enums
    private LocalDateTime dateCreation;
    private LocalDateTime dateUpdate;
    private boolean isActif=false;

    public Users() {
    }

    public Users(int id, String nom, String prenom, String telephone, String email, String password, Role role, LocalDateTime dateCreation, LocalDateTime dateUpdate, boolean isActif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dateCreation = dateCreation;
        this.dateUpdate = dateUpdate;
        this.isActif = isActif;
    }

    public Users(String nom, String prenom, String telephone, String email, String password, boolean isActif) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.isActif = isActif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public boolean isActif() {
        return isActif;
    }

    public void setActif(boolean actif) {
        isActif = actif;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", dateCreation=" + dateCreation +
                ", dateUpdate=" + dateUpdate +
                ", isActif=" + isActif +
                '}';
    }
}
