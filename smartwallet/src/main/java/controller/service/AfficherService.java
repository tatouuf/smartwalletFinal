package controller.service;

import entities.service.Services;
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

    // 🔹 Méthode pour recevoir l'objet Services
    public void setService(Services s) {
        idaffser.setText(String.valueOf(s.getId()));
        prixaffser.setText(String.valueOf(s.getPrix()));
        typeaffser.setText(s.getTypeService().name());
        statutaffserv.setText(s.getStatut().name());
        iduseraff.setText(String.valueOf(s.getId()));
        localiaffserv.setText(s.getLocalisation());
        adresseaffserv.setText(s.getAdresse());
        typeseraffser.setText(s.getTypeService().name());
        descaffserv.setText(s.getDescription());
    }
}
