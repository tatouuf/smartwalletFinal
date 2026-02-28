package esprit.tn.souha_pi.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
<<<<<<< HEAD

public class MyDataBase {

    private static MyDataBase instance;

    // URL avec paramètres de reconnexion automatique
    private final String URL = "jdbc:mysql://localhost:3306/smartwalletdb"
            + "?useSSL=false"
            + "&serverTimezone=UTC"
            + "&autoReconnect=true"
            + "&failOverReadOnly=false"
            + "&maxReconnects=10"
            + "&allowPublicKeyRetrieval=true"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";

    private final String USER = "root";
    private final String PASSWORD = "";
    private Connection connection;

    private MyDataBase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connection établie avec succès");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Vérifier si la connexion est fermée ou invalide
            if (connection == null || connection.isClosed()) {
                System.out.println("⚠️ Connexion fermée, reconnexion...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Reconnexion établie");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification/reconnexion: " + e.getMessage());
            try {
                // Tentative de reconnexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Reconnexion après erreur réussie");
            } catch (SQLException ex) {
                System.err.println("❌ Échec de la reconnexion: " + ex.getMessage());
            }
        }
        return connection;
    }

    // Méthode utilitaire pour fermer la connexion (optionnel)
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture: " + e.getMessage());
        }
    }
=======
public class MyDataBase {
    private static MyDataBase instance;
    private final String URL = "jdbc:mysql://localhost:3306/smartwalletdb";
    private final String USER = "root";
    private final String PASSWORD = "";
    private  Connection connection;
    public MyDataBase(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established");
        } catch (SQLException e) {
            // throw new RuntimeException(e);
            System.err.println(e.getMessage());
        }
    }
    public static MyDataBase getInstance(){
        if(instance==null)
            instance = new MyDataBase();
        return instance;
    }
    public Connection getConnection() {
        return connection;
    }
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
}