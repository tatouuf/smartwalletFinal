package entities;

import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private int user_id;
    private String message;
    private ReclamationStatuts statut;
    private String reponse;
    private Integer admin_id;
    private LocalDateTime date_envoi;
    private LocalDateTime date_reponse;

    // NEW AI FIELDS
    private String category;
    private String sentiment;
    private boolean isUrgent;

    // Constructors
    public Reclamation() {}

    public Reclamation(int id, int user_id, String message, ReclamationStatuts statut,
                       String reponse, Integer admin_id, LocalDateTime date_envoi,
                       LocalDateTime date_reponse, String category, String sentiment, boolean isUrgent) {
        this.id = id;
        this.user_id = user_id;
        this.message = message;
        this.statut = statut;
        this.reponse = reponse;
        this.admin_id = admin_id;
        this.date_envoi = date_envoi;
        this.date_reponse = date_reponse;
        this.category = category;
        this.sentiment = sentiment;
        this.isUrgent = isUrgent;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ReclamationStatuts getStatut() { return statut; }
    public void setStatut(ReclamationStatuts statut) { this.statut = statut; }

    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public Integer getAdmin_id() { return admin_id; }
    public void setAdmin_id(Integer admin_id) { this.admin_id = admin_id; }

    public LocalDateTime getDate_envoi() { return date_envoi; }
    public void setDate_envoi(LocalDateTime date_envoi) { this.date_envoi = date_envoi; }

    public LocalDateTime getDate_reponse() { return date_reponse; }
    public void setDate_reponse(LocalDateTime date_reponse) { this.date_reponse = date_reponse; }

    // NEW: AI getters/setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public boolean isUrgent() { return isUrgent; }
    public void setUrgent(boolean urgent) { isUrgent = urgent; }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", message='" + message + '\'' +
                ", statut=" + statut +
                ", category='" + category + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", isUrgent=" + isUrgent +
                ", date_envoi=" + date_envoi +
                '}';
    }
}