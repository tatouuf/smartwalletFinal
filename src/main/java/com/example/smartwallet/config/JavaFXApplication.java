package com.example.smartwallet.config;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application JavaFX avec Spring Boot
 */
public class JavaFXApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private static String[] args;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Créer et afficher la fenêtre principale
        PrimaryStageInitializer stageInitializer = springContext.getBean(PrimaryStageInitializer.class);
        stageInitializer.initStage(primaryStage);

        primaryStage.setTitle("SmartWallet - Gestion Budgétaire");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        System.exit(0);
    }

    public static void launch(String[] args) {
        JavaFXApplication.args = args;
        Application.launch(JavaFXApplication.class, args);
    }

    public static void setSpringContext(ConfigurableApplicationContext context) {
        springContext = context;
    }
}
