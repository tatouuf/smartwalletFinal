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

    // Ouvre les packages contenant les FXML controllers
    opens controller.service to javafx.fxml, javafx.web; // <-- ajouter javafx.web ici
    opens controller.assurance to javafx.fxml;
    opens controller.credit to javafx.fxml;
    opens controller.mainalc to javafx.fxml;
    opens controller.acceuilservice to javafx.fxml;
    // Ouvre le package contenant les entités
    opens entities.service to javafx.base;

    // Ouvre le package contenant la classe principale au module javafx.graphics
    opens tests.services to javafx.fxml, javafx.graphics;
}
