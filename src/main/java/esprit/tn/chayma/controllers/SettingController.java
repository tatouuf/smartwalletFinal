package esprit.tn.chayma.controllers;

import esprit.tn.chayma.services.SettingsService;
import esprit.tn.chayma.services.TranslationService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingController {
    @FXML
    private ComboBox<?> currencyCombo;
    @FXML
    private Slider fontSizeSlider;
    @FXML
    private Label previewLabel;
    @FXML
    private CheckBox animationsCheckbox;
    @FXML
    private Button saveButton;
    @FXML
    private RadioButton frenchRadio;
    @FXML
    private RadioButton englishRadio;
    @FXML
    private RadioButton arabicRadio;
    @FXML
    private RadioButton lightRadio;
    @FXML
    private RadioButton darkRadio;

    private SettingsService settingsService = SettingsService.getInstance();
    private TranslationService translationService = TranslationService.getInstance();

    @FXML
    public void initialize() {
        // Charger les paramètres sauvegardés
        String currentLang = settingsService.getLanguage();
        switch(currentLang) {
            case "en": englishRadio.setSelected(true); break;
            case "ar": arabicRadio.setSelected(true); break;
            default: frenchRadio.setSelected(true);
        }

        String currentTheme = settingsService.getTheme();
        if ("dark".equals(currentTheme)) {
            darkRadio.setSelected(true);
        } else {
            lightRadio.setSelected(true);
        }

        saveButton.setOnAction(e -> onSave());
    }

    @FXML
    public void onLanguageChange() {
        String lang = "fr";
        if (englishRadio.isSelected()) {
            lang = "en";
        } else if (arabicRadio.isSelected()) {
            lang = "ar";
        }
        settingsService.setLanguage(lang);
        translationService.setLanguage(lang);
        DialogUtil.info("Succès", "Langue changée avec succès / Language changed successfully / تم تغيير اللغة بنجاح");
    }

    @FXML
    public void onThemeChange() {
        String theme = lightRadio.isSelected() ? "light" : "dark";
        settingsService.setTheme(theme);
        applyTheme(theme);
        DialogUtil.info("Succès", "Thème changé / Theme changed / تم تغيير المظهر");
    }

    private void applyTheme(String theme) {
        String stylesheet = "dark".equals(theme) ? getDarkCSS() : getLightCSS();
        WalletLayoutController.instance.getScene().getStylesheets().clear();
        WalletLayoutController.instance.getScene().getStylesheets().add(stylesheet);
    }

    private String getDarkCSS() {
        return getClass().getResource("/css/dark-theme.css") != null
            ? getClass().getResource("/css/dark-theme.css").toExternalForm()
            : "";
    }

    private String getLightCSS() {
        return getClass().getResource("/css/light-theme.css") != null
            ? getClass().getResource("/css/light-theme.css").toExternalForm()
            : "";
    }

    private void onSave() {
        DialogUtil.info("Succès", "Paramètres enregistrés / Settings saved / تم حفظ الإعدادات");
    }
}
