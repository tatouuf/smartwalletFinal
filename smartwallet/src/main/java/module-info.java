module com.example.smartwallet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    opens com.example.smartwallet to javafx.fxml;
    opens com.example.smartwallet.controllers to javafx.fxml;

    exports com.example.smartwallet;
}
