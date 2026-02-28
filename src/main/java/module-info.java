module esprit.tn.souha_pi {
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
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.slf4j;
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

    // ================= ESPRIT PACKAGES =================
    opens esprit.tn.souha_pi to javafx.fxml;
    opens esprit.tn.souha_pi.controllers to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.wallet to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.loan to javafx.fxml;
    opens esprit.tn.souha_pi.controllers.admin to javafx.fxml;
    opens esprit.tn.souha_pi.entities to javafx.base;
    opens esprit.tn.souha_pi.services to javafx.fxml;
    opens esprit.tn.souha_pi.utils to javafx.fxml;

    // ================= OPEN PACKAGES FOR REFLECTION =================
    opens entities to javafx.base;

    // ================= EXPORTS =================
    exports tests;
    exports api;
    exports services;
    exports utils;
    exports entities;
    exports esprit.tn.souha_pi;
}