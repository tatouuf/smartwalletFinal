package com.example.smartwallet.controller.javafx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SettingsJavaFXController {

    @FXML
    private ComboBox<String> currencyCombo;
    @FXML
    private ComboBox<String> themeCombo;
    @FXML
    private Slider fontSizeSlider;
    @FXML
    private CheckBox animationsCheckbox;
    @FXML
    private Button saveButton;
    @FXML
    private Label previewLabel;

    @FXML
    public void initialize() {
        // Initialiser les options
        currencyCombo.setItems(FXCollections.observableArrayList("TND (Dinar Tunisien)", "EUR (Euro)", "USD (Dollar)"));
        currencyCombo.setValue("TND (Dinar Tunisien)");

        themeCombo.setItems(FXCollections.observableArrayList("Clair (Défaut)", "Sombre", "Contraste Élevé"));
        themeCombo.setValue("Clair (Défaut)");

        // Gestion du slider de taille de texte
        fontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double size = newVal.doubleValue();
            previewLabel.setStyle("-fx-font-size: " + size + "px;");
        });

        saveButton.setOnAction(e -> savePreferences());
    }

    private void savePreferences() {
        String currency = currencyCombo.getValue();
        String theme = themeCombo.getValue();
        boolean animations = animationsCheckbox.isSelected();
        double fontSize = fontSizeSlider.getValue();

        // Ici, on pourrait sauvegarder dans un fichier properties ou une base de données
        // Pour l'instant, on simule la sauvegarde
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Préférences enregistrées");
        alert.setHeaderText("Vos préférences ont été mises à jour");
        alert.setContentText(String.format(
            "Devise : %s\nThème : %s\nAnimations : %s\nTaille texte : %.0f px",
            currency, theme, (animations ? "Activées" : "Désactivées"), fontSize
        ));
        alert.showAndWait();
        
        // Appliquer le thème (simulation)
        if (theme.contains("Sombre")) {
            // Logique pour changer le CSS global
            System.out.println("Application du thème sombre...");
        }
    }
}
