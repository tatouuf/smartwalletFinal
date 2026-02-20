package com.example.smartwallet.controller.javafx;

import com.example.smartwallet.config.PrimaryStageInitializer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WelcomeController {

    @FXML
    private Button startButton;

    @FXML
    public void initialize() {
        startButton.setOnAction(e -> {
            // Appeler la méthode statique pour charger l'interface principale
            PrimaryStageInitializer.switchToMainScreen();
        });
    }
}
