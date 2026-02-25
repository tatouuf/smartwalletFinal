module com.example.smartwallet {

    // ================= JAVA FX =================
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;
    requires javafx.media;

    // ================= CORE =================
    requires java.sql;
    requires java.prefs;
    requires java.mail;
    requires java.net.http;
    requires java.logging;
    requires java.desktop;
    requires jdk.httpserver;
    requires jdk.jsobject;

    // ================= LIBRARIES =================
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires org.json;
    requires stripe.java;
    requires org.locationtech.jts;
    requires jersey.client;
    requires jersey.media.json.jackson;

    // FXGL
    requires com.almasb.fxgl.all;
    requires api;
    requires service;

    // ⚠️ IMPORTANT : NE PAS utiliser requires api/service ici
    // (ça casse la lecture des modules JavaFX)

    // ================= OPEN FOR FXML =================
    opens tests to javafx.fxml, javafx.graphics;
   // opens esprit.tn.souha_pi.controllers.user to javafx.fxml;
    opens Controllers to javafx.fxml, javafx.base;
    opens controller to javafx.fxml;

    opens esprit.tn.souha_pi.controllers to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.wallet to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.loan to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.admin to javafx.fxml;
    opens controller.acceuilservice to javafx.fxml;
    opens controller.mainalc to javafx.fxml;
     opens controller.assurance to javafx.fxml;
    opens controller.credit to javafx.fxml;
    opens controller.service to javafx.fxml;

    // Entities (TableView reflection)
    opens entities to javafx.base;
    opens esprit.tn.souha_pi.entities to javafx.base;



    exports tests;
}