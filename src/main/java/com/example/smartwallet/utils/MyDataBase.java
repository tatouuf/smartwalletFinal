package com.example.smartwallet.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
    private Connection connection;

    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/smartwalletdb2?useSSL=false&serverTimezone=UTC";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : ""; 

    private MyDataBase() {
        try {
            // assure le chargement du driver (utile selon environnement)
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion MySQL réussie !");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL introuvable. Vérifie mysql-connector-j dans pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Erreur connexion MySQL : " + e.getMessage());
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
        return connection;
    }
}
