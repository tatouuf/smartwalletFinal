package entities;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String password;
    private Role role;              // Enum dans package enums
    private LocalDateTime date_creation;
    private LocalDateTime date_update;
    private boolean is_actif = false;

    public User() {
    }

    public User(int id, String nom, String prenom, String telephone, String email, String password, Role role, LocalDateTime date_creation, LocalDateTime date_update, boolean is_actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.date_creation = date_creation;
        this.date_update = date_update;
        this.is_actif = is_actif;
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

    public LocalDateTime getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(LocalDateTime date_creation) {
        this.date_creation = date_creation;
    }

    public LocalDateTime getDate_update() {
        return date_update;
    }
    public String getFullname() {
        return
                nom + " " + prenom
                ;
    }

    public void setDate_update(LocalDateTime date_update) {
        this.date_update = date_update;
    }

    public boolean isIs_actif() {
        return is_actif;
    }

    public void setIs_actif(boolean is_actif) {
        this.is_actif = is_actif;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", date_creation=" + date_creation +
                ", date_update=" + date_update +
                ", is_actif=" + is_actif +
                '}';
    }
}