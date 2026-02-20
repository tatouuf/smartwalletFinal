package com.example.smartwallet.controller.javafx;

import com.example.smartwallet.util.ThemeManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

        themeCombo.setItems(FXCollections.observableArrayList("Clair (Défaut)", "Sombre"));
        
        // Définir la valeur actuelle du thème
        if (ThemeManager.isDarkMode()) {
            themeCombo.setValue("Sombre");
        } else {
            themeCombo.setValue("Clair (Défaut)");
        }

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

        // Gestion du Thème Sombre
        boolean enableDarkMode = "Sombre".equals(theme);
        ThemeManager.setDarkMode(enableDarkMode);
        
        // Appliquer immédiatement à la scène actuelle
        if (saveButton.getScene() != null) {
            ThemeManager.applyTheme(saveButton.getScene());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Préférences enregistrées");
        alert.setHeaderText("Vos préférences ont été mises à jour");
        alert.setContentText(String.format(
            "Devise : %s\nThème : %s\nAnimations : %s\nTaille texte : %.0f px",
            currency, theme, (animations ? "Activées" : "Désactivées"), fontSize
        ));
        alert.showAndWait();
    }
}
