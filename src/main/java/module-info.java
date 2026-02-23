module esprit.tn.chayma {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.mail;
    requires java.net.http;
    requires com.google.gson;

    opens esprit.tn.chayma to javafx.fxml;
    opens esprit.tn.chayma.controllers to javafx.fxml;

    exports esprit.tn.chayma;
}