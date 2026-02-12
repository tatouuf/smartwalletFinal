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

    public void setService(Services s) {

        if (s == null) return;

        idaffser.setText(String.valueOf(s.getId()));
        prixaffser.setText(String.valueOf(s.getPrix()));

        typeaffser.setText(s.getType() != null ? s.getType() : "");

        typeseraffser.setText(
                s.getTypeService() != null ? s.getTypeService().name() : ""
        );

        statutaffserv.setText(
                s.getStatut() != null ? s.getStatut().name() : ""
        );

        iduseraff.setText(
                s.getUser() != null ? String.valueOf(s.getUser().getId()) : ""
        );

        localiaffserv.setText(
                s.getLocalisation() != null ? s.getLocalisation() : ""
        );

        adresseaffserv.setText(
                s.getAdresse() != null ? s.getAdresse() : ""
        );

        descaffserv.setText(
                s.getDescription() != null ? s.getDescription() : ""
        );
    }
}
