package entities.assurances;

import java.time.LocalDateTime;

public class Assurances {

    // ===========================
    // 🔹 Attributs
    // ===========================
    private int id;
    private String nomAssurance;
    private TypeAssurance typeAssurance; // ⚡ Enum au lieu de String
    private String description;
    private float prix;
    private int dureeMois;
    private String conditions;
    private LocalDateTime dateCreation;
    private Statut statut;             // ⚡ Enum au lieu de String

    // ===========================
    // 🔹 Constructeurs
    // ===========================

    public Assurances() {}

    public Assurances(int id, String nomAssurance, TypeAssurance typeAssurance, String description,
                      float prix, int dureeMois, String conditions,
                      LocalDateTime dateCreation, Statut statut) {
        this.id = id;
        this.nomAssurance = nomAssurance;
        this.typeAssurance = typeAssurance;
        this.description = description;
        this.prix = prix;
        this.dureeMois = dureeMois;
        this.conditions = conditions;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    public Assurances(String nomAssurance, TypeAssurance typeAssurance, String description,
                      float prix, int dureeMois, String conditions, Statut statut) {
        this.nomAssurance = nomAssurance;
        this.typeAssurance = typeAssurance;
        this.description = description;
        this.prix = prix;
        this.dureeMois = dureeMois;
        this.conditions = conditions;
        this.statut = statut;
    }

    // ===========================
    // 🔹 Getters & Setters
    // ===========================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomAssurance() { return nomAssurance; }
    public void setNomAssurance(String nomAssurance) { this.nomAssurance = nomAssurance; }

    public TypeAssurance getTypeAssurance() { return typeAssurance; }
    public void setTypeAssurance(TypeAssurance typeAssurance) { this.typeAssurance = typeAssurance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrix() { return prix; }
    public void setPrix(float prix) { this.prix = prix; }


    public int getDureeMois() { return dureeMois; }
    public void setDureeMois(int dureeMois) { this.dureeMois = dureeMois; }

    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    // ===========================
    // 🔹 toString
    // ===========================
    @Override
    public String toString() {
        return "Assurance{" +
                "id=" + id +
                ", nomAssurance='" + nomAssurance + '\'' +
                ", typeAssurance=" + typeAssurance +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", dureeMois=" + dureeMois +
                ", conditions='" + conditions + '\'' +
                ", dateCreation=" + dateCreation +
                ", statut=" + statut +
                '}';
    }
}
