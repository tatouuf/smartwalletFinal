module com.example.smartwallet {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens tests.services to javafx.graphics, javafx.fxml;
    opens controller.service to javafx.fxml;
    opens entities.service to javafx.base;
}
