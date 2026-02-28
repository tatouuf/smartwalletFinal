package entities.service;

import java.time.LocalDateTime;

public class Favori {
    private int id;
    private int idUser;
    private int idService;
    private LocalDateTime dateAjout;
    private String notePersonnelle;

    // Constructeurs
    public Favori() {
        this.dateAjout = LocalDateTime.now();
    }

    public Favori(int idUser, int idService) {
        this.idUser = idUser;
        this.idService = idService;
        this.dateAjout = LocalDateTime.now();
    }

    public Favori(int id, int idUser, int idService, LocalDateTime dateAjout, String notePersonnelle) {
        this.id = id;
        this.idUser = idUser;
        this.idService = idService;
        this.dateAjout = dateAjout;
        this.notePersonnelle = notePersonnelle;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdService() { return idService; }
    public void setIdService(int idService) { this.idService = idService; }

    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }

    public String getNotePersonnelle() { return notePersonnelle; }
    public void setNotePersonnelle(String notePersonnelle) { this.notePersonnelle = notePersonnelle; }

    @Override
    public String toString() {
        return "Favori{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", idService=" + idService +
                ", dateAjout=" + dateAjout +
                '}';
    }
}