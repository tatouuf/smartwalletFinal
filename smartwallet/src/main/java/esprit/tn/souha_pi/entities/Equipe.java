package esprit.tn.souha_pi.entities;

public class Equipe {

    private int id;
    private String nom;
    private String logo;
    private String game;
    private String categorie;
    private int coachId;

    // Constructeur vide
    public Equipe() {
    }

    // Constructeur avec paramètres
    public Equipe(int id, String nom, String logo, String game, String categorie, int coachId) {
        this.id = id;
        this.nom = nom;
        this.logo = logo;
        this.game = game;
        this.categorie = categorie;
        this.coachId = coachId;
    }

    // Getters & Setters

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", logo='" + logo + '\'' +
                ", game='" + game + '\'' +
                ", categorie='" + categorie + '\'' +
                ", coachId=" + coachId +
                '}';
    }
}
