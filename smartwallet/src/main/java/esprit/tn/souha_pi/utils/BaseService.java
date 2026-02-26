package esprit.tn.souha_pi.utils;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseService {

    protected Connection cnx;

    public BaseService() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    protected void checkConnection() throws SQLException {
        try {
            if (cnx == null || cnx.isClosed()) {
                System.out.println("⚠️ Connexion fermée, obtention d'une nouvelle...");
                cnx = MyDataBase.getInstance().getConnection();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion: " + e.getMessage());
            cnx = MyDataBase.getInstance().getConnection();
        }
    }
}
