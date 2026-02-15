module esprit.tn.souha_pi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens esprit.tn.souha_pi.controllers to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.loan to javafx.fxml;
    opens esprit.tn.souha_pi.entities ;
    opens esprit.tn.souha_pi.services;
    opens esprit.tn.souha_pi.utils;

    opens esprit.tn.souha_pi to javafx.fxml;
    exports esprit.tn.souha_pi;
}