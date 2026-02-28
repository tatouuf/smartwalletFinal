<<<<<<< HEAD
module com.example.smartwallet {
    // ================= JAVA FX =================
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;
    requires javafx.media;
    requires com.google.gson;
    requires org.apache.httpcomponents.client5.httpclient5;  // Ajouter
    requires org.apache.httpcomponents.core5.httpcore5;      // Ajouter
    requires org.slf4j;                                       // Ajouter (souvent
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

    requires org.controlsfx.controls;
    requires org.json;
    requires stripe.java;
    requires org.locationtech.jts;
    requires jersey.client;
    requires jersey.media.json.jackson;
    requires jbcrypt;
    requires mysql.connector.j;

    // ================= OPEN PACKAGES FOR FXML =================
    opens tests to javafx.fxml;
    opens Controllers to javafx.fxml;
    opens controller to javafx.fxml;
    opens controller.acceuilservice to javafx.fxml;
    opens controller.mainalc to javafx.fxml;
    opens controller.assurance to javafx.fxml;
    opens controller.credit to javafx.fxml;
    opens controller.service to javafx.fxml;

    // ================= OPEN PACKAGES FOR REFLECTION =================
    opens entities to javafx.base;

    // ================= EXPORTS =================
    exports tests;
    exports api;
    exports services;
    exports utils;
    exports entities;

    // ================= ESPRIT PACKAGES - VÉRIFIEZ QUE CES DOSSIERS EXISTENT =================
    // Commentez ou supprimez les packages qui n'existent pas

    // Si le dossier esprit/tn/souha_pi/controllers/admin existe
    opens esprit.tn.souha_pi.controllers to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.wallet to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.loan to javafx.fxml;
    opens esprit.tn.souha_pi.entities to javafx.base;
=======
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
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
}