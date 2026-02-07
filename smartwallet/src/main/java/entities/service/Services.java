package entities.service;

import entities.user.User;

public class Services {

    // ===========================
    // 🔹 Attributs
    // ===========================
    private int id;
    private float prix;
    private String description;
    private String type;
    private Statut statut;           // DISPONIBLE ou NON_DISPONIBLE
    private User user;               // Utilisateur associé au service
    private String localisation;     // simplifié (POINT géré côté DB)
    private String adresse;
    private TypeService typeService; // voiture, maison, etc.

    // ===========================
    // 🔹 Constructeurs
    // ===========================

    public Services() {}

    public Services(int id, float prix, String description, String type, Statut statut,
                    User user, String localisation, String adresse, TypeService typeService) {
        this.id = id;
        this.prix = prix;
        this.description = description;
        this.type = type;
        this.statut = statut;
        this.user = user;
        this.localisation = localisation;
        this.adresse = adresse;
        this.typeService = typeService;
    }

    public Services(float prix, String description, String type, Statut statut,
                    User user, String localisation, String adresse, TypeService typeService) {
        this.prix = prix;
        this.description = description;
        this.type = type;
        this.statut = statut;
        this.user = user;
        this.localisation = localisation;
        this.adresse = adresse;
        this.typeService = typeService;
    }

    // ===========================
    // 🔹 Getters & Setters
    // ===========================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getPrix() { return prix; }
    public void setPrix(float prix) { this.prix = prix; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public TypeService getTypeService() { return typeService; }
    public void setTypeService(TypeService typeService) { this.typeService = typeService; }

    // ===========================
    // 🔹 toString
    // ===========================
    @Override
    public String toString() {
        return "Services{" +
                "id=" + id +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", statut=" + statut +
                ", user=" + user +   // affichera le toString() de User
                ", localisation='" + localisation + '\'' +
                ", adresse='" + adresse + '\'' +
                ", typeService=" + typeService +
                '}';
    }
}
