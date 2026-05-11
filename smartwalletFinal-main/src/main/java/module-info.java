module com.example.smartwallet {
    // ================= JAVA FX =================
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;
    requires javafx.media;
    requires com.google.gson;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.slf4j;

    // ================= CORE =================
    requires java.sql;
    requires java.prefs;
    requires jakarta.mail;  // ✅ AJOUTER CETTE LIGNE
    requires java.net.http;
    requires java.logging;
    requires java.desktop;
    requires jdk.httpserver;
    requires jdk.jsobject;
    requires com.google.zxing;
    requires com.google.zxing.javase;

    // ================= LIBRARIES =================
    requires com.github.librepdf.openpdf;
    requires org.apache.poi.ooxml;
    requires org.controlsfx.controls;
    requires org.json;
    requires stripe.java;
    requires org.locationtech.jts;
    requires jersey.client;
    requires jersey.media.json.jackson;
    requires jbcrypt;
    requires mysql.connector.j;
    requires kernel;
    requires layout;

    // ================= TWILIO =================
    requires twilio;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires java.xml;
    requires javafx.swing;

    // ================= OPEN PACKAGES FOR FXML =================
    opens tests to javafx.fxml;
    opens Controllers to javafx.fxml, javafx.base;
    opens controller to javafx.fxml;
    opens controller.acceuilservice to javafx.fxml;
    opens controller.mainalc to javafx.fxml;
    opens controller.assurance to javafx.fxml;
    opens controller.credit to javafx.fxml;
    opens controller.service to javafx.fxml;
    opens com.example.smartwallet.controllers to javafx.fxml;
    opens com.example.smartwallet.entities to javafx.base;
    opens com.example.smartwallet.utils to javafx.fxml;  // ✅ AJOUTER POUR EMAIL SERVICE

    // ================= OPEN PACKAGES FOR REFLECTION =================
    opens entities to javafx.base;
    opens utils to javafx.base;  // ✅ AJOUTER POUR EMAIL SERVICE

    // ================= EXPORTS =================
    exports tests;
    exports api;
    exports services;
    exports utils;
    exports entities;
    exports services.sms;
    exports services.service;
    exports com.example.smartwallet.utils;  // ✅ AJOUTER POUR EXPORTER LE PACKAGE

    // ================= ESPRIT PACKAGES =================
    opens esprit.tn.souha_pi.controllers to javafx.fxml, javafx.base;
    opens esprit.tn.souha_pi.controllers.wallet to javafx.fxml, javafx.base;
    opens esprit.tn.souha_pi.controllers.loan to javafx.fxml, javafx.base;
    opens esprit.tn.souha_pi.entities to javafx.base;

    // Budget & Expenses (Chaima)
    opens esprit.tn.chayma.controllers to javafx.fxml, javafx.base;
    opens esprit.tn.chayma.entities to javafx.base;
}