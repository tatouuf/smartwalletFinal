module com.example.smartwallet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jsobject;
    requires javafx.web;
    requires java.desktop;
    requires org.locationtech.jts;
    requires java.net.http;
    requires org.json;

    // Ouvre les packages contenant les FXML controllers et entités
    opens com.example.smartwallet to javafx.fxml, javafx.graphics;
    opens tests.services to javafx.fxml, javafx.graphics;
}
