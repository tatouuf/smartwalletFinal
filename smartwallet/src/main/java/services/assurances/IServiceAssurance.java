package services.assurances;
import entities.assurances.Assurances;
import java.sql.SQLException;
import java.util.List;
public interface IServiceAssurance {
    // Ajouter une assurance
    void ajouterAssurance(Assurances A) throws SQLException;

    // Modifier une assurance
    void modifierAssurance(Assurances A) throws SQLException;

    // Supprimer une assurance
    void supprimerAssurance(Assurances A) throws SQLException;

    // Récupérer toutes les assurances
    List<Assurances> recupererAssurance() throws SQLException;

}
