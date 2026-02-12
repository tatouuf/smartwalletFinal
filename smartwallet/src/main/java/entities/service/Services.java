package entities.service;

import entities.user.User;

public class Services {

    private int id;
    private float prix;
    private String localisation; // format "lat,lon"
    private String adresse;
    private String description;
    private String type;          // texte libre
    private Statut statut;
    private TypeService typeService;
    private User user;

    // ======== Constructeurs ========
    public Services() {}

    // Pour ajout
    public Services(float prix, String localisation, String adresse, String description,
                    String type, Statut statut, TypeService typeService, User user) {
        this.prix = prix;
        this.localisation = localisation;
        this.adresse = adresse;
        this.description = description;
        this.type = type;
        this.statut = statut;
        this.typeService = typeService;
        this.user = user;
    }

    // Pour modification (avec id)
    public Services(int id, float prix, String localisation, String adresse, String description,
                    String type, Statut statut, TypeService typeService, User user) {
        this(prix, localisation, adresse, description, type, statut, typeService, user);
        this.id = id;
    }

    // ======== Getters ========
    public int getId() { return id; }
    public float getPrix() { return prix; }
    public String getLocalisation() { return localisation; }
    public String getAdresse() { return adresse; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public Statut getStatut() { return statut; }
    public TypeService getTypeService() { return typeService; }
    public User getUser() { return user; }

    // ======== Setters ========
    public void setId(int id) { this.id = id; }
    public void setPrix(float prix) { this.prix = prix; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public void setTypeService(TypeService typeService) { this.typeService = typeService; }
    public void setUser(User user) { this.user = user; }

    // ======== toString ========
    @Override
    public String toString() {
        return "Services{" +
                "id=" + id +
                ", prix=" + prix +
                ", localisation='" + localisation + '\'' +
                ", adresse='" + adresse + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", statut=" + statut +
                ", typeService=" + typeService +
                ", user=" + (user != null ? user.getNom() : "null") +
                '}';
    }
}
