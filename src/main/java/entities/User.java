package entities;

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
    private final ObjectProperty<Role> role = new SimpleObjectProperty<>();  // ObjectProperty pour enum
    private final StringProperty status = new SimpleStringProperty();
    private final BooleanProperty is_actif = new SimpleBooleanProperty();

    private LocalDateTime date_creation;
    private LocalDateTime date_update;

    // Constructeur avec status
    public User(int id, String nom, String prenom, String telephone, String email,
                String password, Role role, String status, LocalDateTime date_creation,
                LocalDateTime date_update, boolean is_actif) {
        this.id.set(id);
        this.nom.set(nom);
        this.prenom.set(prenom);
        this.telephone.set(telephone);
        this.email.set(email);
        this.password.set(password);
        this.role.set(role);
        this.status.set(status);
        this.date_creation = date_creation;
        this.date_update = date_update;
        this.is_actif.set(is_actif);
        updateFullName();
    }
    // Constructeur par défaut

    public User() {
        this.date_creation = LocalDateTime.now();
        this.role.set(Role.USER);
        this.status.set("PENDING");
        this.is_actif.set(false);
    }
    // Dans entities/User.java
    public User(int id, String nom, String prenom, String telephone, String email,
                String password, Role role, LocalDateTime date_creation,
                LocalDateTime date_update, boolean is_actif) {
        this.id.set(id);
        this.nom.set(nom);
        this.prenom.set(prenom);
        this.telephone.set(telephone);
        this.email.set(email);
        this.password.set(password);
        this.role.set(role);  // Convertit l'enum en String
        this.status.set("PENDING");   // Valeur par défaut pour status
        this.date_creation = date_creation;
        this.date_update = date_update;
        this.is_actif.set(is_actif);
        updateFullName();
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty fullnameProperty() { return fullname; }
    public StringProperty telephoneProperty() { return telephone; }
    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public ObjectProperty<Role> roleProperty() { return role; }  // ObjectProperty
    public StringProperty statusProperty() { return status; }
    public BooleanProperty is_actifProperty() { return is_actif; }

    // Getters
    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getFullname() { return fullname.get(); }
    public String getTelephone() { return telephone.get(); }
    public String getEmail() { return email.get(); }
    public String getPassword() { return password.get(); }
    public Role getRole() { return role.get(); }                 // Retourne Role enum
    public String getStatus() { return status.get(); }
    public boolean isIs_actif() { return is_actif.get(); }
    public LocalDateTime getDate_creation() { return date_creation; }
    public LocalDateTime getDate_update() { return date_update; }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setNom(String nom) { this.nom.set(nom); updateFullName(); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); updateFullName(); }
    private void updateFullName() { this.fullname.set((nom.get() + " " + prenom.get()).trim()); }
    public void setFullname(String fullname) { this.fullname.set(fullname); }
    public void setTelephone(String telephone) { this.telephone.set(telephone); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPassword(String password) { this.password.set(password); }
    public void setRole(Role role) { this.role.set(role); }      // Setter avec enum
    public void setStatus(String status) { this.status.set(status); }
    public void setIs_actif(boolean is_actif) { this.is_actif.set(is_actif); }
    public void setDate_creation(LocalDateTime date_creation) { this.date_creation = date_creation; }
    public void setDate_update(LocalDateTime date_update) { this.date_update = date_update; }

    // Helper methods
    public boolean isAdmin() { return Role.ADMIN.equals(getRole()); }
    public boolean isUser() { return Role.USER.equals(getRole()); }
    public boolean isPending() { return "PENDING".equalsIgnoreCase(getStatus()); }
    public boolean isApproved() { return "APPROVED".equalsIgnoreCase(getStatus()); }
    public boolean isRejected() { return "REJECTED".equalsIgnoreCase(getStatus()); }
    public boolean isActive() { return is_actif.get(); }

    @Override
    public String toString() {
        return "User{id=" + getId() + ", nom='" + getNom() + "', prenom='" + getPrenom() +
                "', email='" + getEmail() + "', role=" + getRole() + "', status='" + getStatus() + "'}";
    }
}