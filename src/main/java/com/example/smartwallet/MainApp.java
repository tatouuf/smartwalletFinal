package com.example.smartwallet;

import com.example.smartwallet.config.JavaFXApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Classe principale de démarrage de l'application SmartWallet
 * Lance l'application Spring Boot avec interface JavaFX
 */
@SpringBootApplication
public class MainApp {

    public static void main(String[] args) {
        // Créer le contexte Spring en arrière-plan
        ConfigurableApplicationContext context = SpringApplication.run(MainApp.class, args);

        // Passer le contexte à JavaFXApplication et lancer l'interface graphique
        JavaFXApplication.setSpringContext(context);
        JavaFXApplication.launch(args);
    }
}


