package esprit.tn.souha_pi.entities;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class User {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty prenom = new SimpleStringProperty();
    private final StringProperty fullname = new SimpleStringProperty();
    private final StringProperty telephone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty(); // EN_ATTENTE, ACTIF, REJETE
    private final BooleanProperty active = new SimpleBooleanProperty();

    private LocalDateTime dateCreation;
    private LocalDateTime dateUpdate;

    public User() {
        this.dateCreation = LocalDateTime.now();
        this.role.set("UTILISATEUR");
        this.status.set("EN_ATTENTE"); // Par défaut, en attente
        this.active.set(false);
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty fullnameProperty() { return fullname; }
    public StringProperty telephoneProperty() { return telephone; }
    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty roleProperty() { return role; }
    public StringProperty statusProperty() { return status; }
    public BooleanProperty activeProperty() { return active; }

    // Getters
    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getFullname() { return fullname.get(); }
    public String getTelephone() { return telephone.get(); }
    public String getEmail() { return email.get(); }
    public String getPassword() { return password.get(); }
    public String getRole() { return role.get(); }
    public String getStatus() { return status.get(); }
    public boolean isActive() { return active.get(); }
    public LocalDateTime getDateCreation() { return dateCreation; }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setNom(String nom) {
        this.nom.set(nom);
        this.fullname.set(nom + " " + (prenom.get() != null ? prenom.get() : ""));
    }
    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
        this.fullname.set((nom.get() != null ? nom.get() : "") + " " + prenom);
    }
    public void setFullname(String fullname) { this.fullname.set(fullname); }
    public void setTelephone(String telephone) { this.telephone.set(telephone); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPassword(String password) { this.password.set(password); }
    public void setRole(String role) { this.role.set(role); }
    public void setStatus(String status) { this.status.set(status); }
    public void setActive(boolean active) { this.active.set(active); }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}