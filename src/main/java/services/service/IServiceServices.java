package services.service;

import entities.service.Services;
import java.sql.SQLException;
import java.util.List;

public interface IServiceServices {
    void ajouterServices(Services s) throws SQLException;
    void modifierServices(Services s) throws SQLException;
    void supprimerServices(Services s) throws SQLException;
    List<Services> recupererServices() throws SQLException;
    void modifierServiceStatut(Services s) throws SQLException;
}