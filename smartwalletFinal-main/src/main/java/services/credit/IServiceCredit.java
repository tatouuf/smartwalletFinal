
package services.credit;

import entities.credit.Credit;
import java.sql.SQLException;
import java.util.List;

public interface IServiceCredit {
    void ajouterCredit(Credit c) throws SQLException;
    void modifierCredit(Credit c) throws SQLException;
    void supprimerCredit(Credit c) throws SQLException;
    List<Credit> recupererCredits() throws SQLException;
}
