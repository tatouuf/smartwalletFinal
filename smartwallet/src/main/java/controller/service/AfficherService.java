package controller.service;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AfficherService {

    @FXML
    private TextField idaffser;

    @FXML
    private TextField prixaffser;

    @FXML
    private TextField typeaffser;

    @FXML
    private TextField statutaffserv;

    @FXML
    private TextField iduseraff;

    @FXML
    private TextField localiaffserv;

    @FXML
    private TextField adresseaffserv;

    @FXML
    private TextField typeseraffser;

    @FXML
    private TextField descaffserv;

    // ===========================
    // 🔹 Setters pour passer les données
    // ===========================

    public void setId(int id) {
        this.idaffser.setText(String.valueOf(id));
    }

    public void setPrix(float prix) {
        this.prixaffser.setText(String.valueOf(prix));
    }

    public void setType(String type) {
        this.typeaffser.setText(type);
    }

    public void setStatut(String statut) {
        this.statutaffserv.setText(statut);
    }

    public void setIdUser(int idUser) {
        this.iduseraff.setText(String.valueOf(idUser));
    }

    public void setLocalisation(String localisation) {
        this.localiaffserv.setText(localisation);
    }

    public void setAdresse(String adresse) {
        this.adresseaffserv.setText(adresse);
    }

    public void setTypeService(String typeService) {
        this.typeseraffser.setText(typeService);
    }

    public void setDescription(String description) {
        this.descaffserv.setText(description);
    }
}
