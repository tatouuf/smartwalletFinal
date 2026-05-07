package entities.service;

import entities.User;
import org.locationtech.jts.geom.Point;

public class Services {

    private int id;
    private float prix;
    private Point localisation; // format "lat,lon"
    private String adresse;
    private String description;
    private String type;          // texte libre
    private Statut statut;
    private TypeService typeService;
    private User user;
    private String image;         // chemin ou URL de l'image
    private int duree;            // ← AJOUTER CE CHAMP (durée de location en jours)

    // ======== Constructeurs ========

    /** Constructeur vide */
    public Services() {}

    /** Constructeur pour ajout (sans id) */
    public Services(float prix, Point localisation, String adresse, String description,
                    String type, Statut statut, TypeService typeService, User user, String image) {
        this.prix = prix;
        this.localisation = localisation;
        this.adresse = adresse;
        this.description = description;
        this.type = type;
        this.statut = statut;
        this.typeService = typeService;
        this.user = user;
        this.image = image;
        this.duree = 0; // ← INITIALISER À 0
    }

    /** Constructeur pour modification (avec id) */
    public Services(int id, float prix, Point localisation, String adresse, String description,
                    String type, Statut statut, TypeService typeService, User user, String image) {
        this(prix, localisation, adresse, description, type, statut, typeService, user, image);
        this.id = id;
    }

    // ======== Getters ========

    public int getId() { return id; }
    public float getPrix() { return prix; }
    public Point getLocalisation() { return localisation; }
    public String getAdresse() { return adresse; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public Statut getStatut() { return statut; }
    public TypeService getTypeService() { return typeService; }
    public User getUser() { return user; }
    public String getImage() { return image; }
    public int getDuree() { return duree; }  // ← AJOUTER CE GETTER

    /** Retourner le nom du statut pour affichage */
    public String getStatutString() {
        return statut != null ? statut.name() : "";
    }

    /** Retourner le nom du typeService pour affichage */
    public String getTypeServiceString() {
        return typeService != null ? typeService.name() : "";
    }

    /** Retourner l'ID de l'utilisateur pour affichage */
    public int getUserId() {
        return user != null ? user.getId() : 0;
    }

    // ======== Setters ========

    public void setId(int id) { this.id = id; }
    public void setPrix(float prix) { this.prix = prix; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public void setTypeService(TypeService typeService) { this.typeService = typeService; }
    public void setUser(User user) { this.user = user; }
    public void setImage(String image) { this.image = image; }
    public void setLocalisation(Point location) { this.localisation = location; }
    public void setDuree(int duree) { this.duree = duree; }  // ← AJOUTER CE SETTER

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
                ", statut=" + (statut != null ? statut.name() : "null") +
                ", typeService=" + (typeService != null ? typeService.name() : "null") +
                ", user=" + (user != null ? user.getNom() : "null") +
                ", image='" + (image != null ? image : "null") + '\'' +
                ", duree=" + duree +  // ← AJOUTER DANS toString
                '}';
    }

    public static TypeService fromString(String value) {
        if (value == null) return null;
        switch(value.toLowerCase()) {
            case "voiture": return TypeService.VOITURE;
            case "maison": return TypeService.MAISON;
            case "standard": return TypeService.STANDARD;
            default: return null;
        }
    }

    // Et une méthode pour la conversion inverse
    public String getTypeServiceDBValue() {
        if (typeService == null) return null;
        switch(typeService) {
            case VOITURE: return "voiture";
            case MAISON: return "maison";
            case STANDARD: return "standard";
            default: return null;
        }
    }
}