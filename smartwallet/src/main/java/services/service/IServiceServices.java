package services.service;

import entities.service.Services;

import java.sql.SQLException;
import java.util.List;

public interface IServiceServices {
    void ajouterServices (Services S) throws SQLException;
    void modifierServices (Services S) throws SQLException;
    void supprimerServices (Services S) throws SQLException;
    List<Services> recupererServices() throws SQLException;
    void modifierServiceStatut(Services s) throws SQLException;
}

