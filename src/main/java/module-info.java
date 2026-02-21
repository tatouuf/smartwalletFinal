module esprit.tn.souha_pi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.mail;
    requires java.net.http;  // ← AJOUTEZ CETTE LIGNE
    requires com.google.gson; // Pour Gson

    // Ouvrir tous les packages
    opens esprit.tn.souha_pi to javafx.fxml;
    opens esprit.tn.souha_pi.controllers to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.loan to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.wallet to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.admin to javafx.fxml;
    opens esprit.tn.souha_pi.entities to javafx.base;
    opens esprit.tn.souha_pi.services to javafx.fxml;
    opens esprit.tn.souha_pi.utils to javafx.fxml;

    exports esprit.tn.souha_pi;
}