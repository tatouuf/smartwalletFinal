package entities;

import java.time.LocalDateTime;

public class Amitie {
    private int id;
    private int user_id;
    private int friend_id;
    private StatutAmitie statut;
    private LocalDateTime dateCreation; // This maps to 'created_at' in database

    // Constructors
    public Amitie() {}

    public Amitie(int id, int user_id, int friend_id, StatutAmitie statut, LocalDateTime dateCreation) {
        this.id = id;
        this.user_id = user_id;
        this.friend_id = friend_id;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getFriend_id() { return friend_id; }
    public void setFriend_id(int friend_id) { this.friend_id = friend_id; }

    public StatutAmitie getStatut() { return statut; }
    public void setStatut(StatutAmitie statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return "Amitie{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", friend_id=" + friend_id +
                ", statut=" + statut +
                ", dateCreation=" + dateCreation +
                '}';
    }
}