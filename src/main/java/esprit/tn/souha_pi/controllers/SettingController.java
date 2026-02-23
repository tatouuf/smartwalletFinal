package esprit.tn.souha_pi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class SettingController {

    @FXML
    private ComboBox<?> currencyCombo;

    @FXML
    private ComboBox<?> themeCombo;

    @FXML
    private Slider fontSizeSlider;

    @FXML
    private Label previewLabel;

    @FXML
    private CheckBox animationsCheckbox;

    @FXML
    private Button saveButton;

    @FXML
    public void initialize() {
        // TODO: Initialiser la vue des paramètres
    }
}