package esprit.tn.chayma.controllers;

import esprit.tn.chayma.services.SettingsService;
import esprit.tn.chayma.services.TranslationService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
    private Button btnRetourDashboard;  // ✅ AJOUTÉ

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

    @FXML
    private void retourDashboard() {
        try {
            // Chemin absolu depuis la racine des ressources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetourDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartWallet Admin Dashboard");
            stage.centerOnScreen();
            System.out.println("✅ Retour au DashboardAdmin effectué");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au Dashboard !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyTheme(String theme) {
        System.out.println("Apply theme: " + theme + " (stub)");
    }

    private void onSave() {
        DialogUtil.info("Succès", "Paramètres enregistrés / Settings saved / تم حفظ الإعدادات");
    }
}