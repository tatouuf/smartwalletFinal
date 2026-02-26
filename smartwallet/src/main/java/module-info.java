module com.example.smartwallet {

    // =============================
    // JavaFX
    // =============================
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;

    // =============================
    // Java standard
    // =============================
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires jdk.jsobject;

    // =============================
    // Bibliothèques externes
    // =============================
    requires org.locationtech.jts;
    requires org.json;

    // =========================================================
    // ✅ EXPORTS (TRÈS IMPORTANT pour lancer MyApp)
    // =========================================================
    exports controller.service;
    exports tests.services; // si MyApp est ici (à garder si utilisé)

    // =========================================================
    // ✅ OPENS pour FXML
    // =========================================================
    opens controller.service to javafx.fxml, javafx.web;
    opens controller.assurance to javafx.fxml;
    opens controller.credit to javafx.fxml;
    opens controller.mainalc to javafx.fxml;
    opens controller.acceuilservice to javafx.fxml;

    // =========================================================
    // ✅ OPENS pour les entités (TableView, etc.)
    // =========================================================
    opens entities.service to javafx.base;
}