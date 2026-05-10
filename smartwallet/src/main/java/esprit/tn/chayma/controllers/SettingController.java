package esprit.tn.chayma.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;          // ← Ligne ajoutée
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import java.util.prefs.Preferences;

public class SettingController {

    @FXML private BorderPane rootPane;
    @FXML private VBox lightThemeBox;
    @FXML private VBox darkThemeBox;
    @FXML private Label previewLabel;
    @FXML private Label previewFontLabel;
    @FXML private Slider fontSizeSlider;
    @FXML private CheckBox animationsCheckbox;
    @FXML private Button saveButton;

    private Preferences prefs;

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(SettingController.class);
        loadPreferences();

        // Sélection du thème par clic sur les cartes
        lightThemeBox.setOnMouseClicked(e -> applyTheme("light"));
        darkThemeBox.setOnMouseClicked(e -> applyTheme("dark"));

        // Aperçu taille police en temps réel
        fontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double size = newVal.doubleValue();
            previewFontLabel.setStyle("-fx-font-size: " + size + "px;");
            previewLabel.setStyle("-fx-font-size: " + size + "px;");
        });
    }

    private void applyTheme(String theme) {
        if ("dark".equals(theme)) {
            // Styles pour le thème sombre
            rootPane.setStyle("-fx-background-color: #0f172a;");

            // Appliquer aux cartes et éléments principaux via lookup (ou directement via CSS)
            for (Node node : rootPane.lookupAll(".card")) {
                node.setStyle("-fx-background-color: #1e293b; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 4);");
            }
            for (Node node : rootPane.lookupAll(".card-title")) {
                node.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 18px; -fx-font-weight: bold;");
            }
            for (Node node : rootPane.lookupAll(".separator")) {
                node.setStyle("-fx-background-color: #334155;");
            }
            for (Node node : rootPane.lookupAll(".preview-box")) {
                node.setStyle("-fx-padding: 12; -fx-background-color: #334155; -fx-background-radius: 10; -fx-border-color: #475569; -fx-border-radius: 10; -fx-border-width: 1; -fx-text-fill: #cbd5e1;");
            }
            rootPane.lookupAll(".footer").forEach(node -> node.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155;"));
            rootPane.lookupAll(".back-button").forEach(node -> node.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 8 18; -fx-font-weight: bold;"));
            rootPane.lookupAll(".save-button").forEach(node -> node.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 40; -fx-font-size: 14px;"));

            // Mise en surbrillance de la carte sélectionnée
            darkThemeBox.setStyle("-fx-background-color: #0f172a; -fx-border-color: #3b82f6; -fx-border-width: 2; -fx-background-radius: 16; -fx-border-radius: 16; -fx-padding: 15;");
            lightThemeBox.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-width: 1; -fx-background-radius: 16; -fx-border-radius: 16; -fx-padding: 15;");

            previewLabel.setText("Thème sombre activé. (Aperçu)");
        } else {
            // Styles pour le thème clair
            rootPane.setStyle("-fx-background-color: #f8fafc;");

            for (Node node : rootPane.lookupAll(".card")) {
                node.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 4);");
            }
            for (Node node : rootPane.lookupAll(".card-title")) {
                node.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 18px; -fx-font-weight: bold;");
            }
            for (Node node : rootPane.lookupAll(".separator")) {
                node.setStyle("-fx-background-color: #e2e8f0;");
            }
            for (Node node : rootPane.lookupAll(".preview-box")) {
                node.setStyle("-fx-padding: 12; -fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1; -fx-text-fill: black;");
            }
            rootPane.lookupAll(".footer").forEach(node -> node.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0;"));
            rootPane.lookupAll(".back-button").forEach(node -> node.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 8 18; -fx-font-weight: bold;"));
            rootPane.lookupAll(".save-button").forEach(node -> node.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 40; -fx-font-size: 14px;"));

            lightThemeBox.setStyle("-fx-background-color: #e2e8f0; -fx-border-color: #3b82f6; -fx-border-width: 2; -fx-background-radius: 16; -fx-border-radius: 16; -fx-padding: 15;");
            darkThemeBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #475569; -fx-border-width: 1; -fx-background-radius: 16; -fx-border-radius: 16; -fx-padding: 15;");

            previewLabel.setText("Thème clair activé. (Aperçu)");
        }
    }

    private void loadPreferences() {
        double fontSize = prefs.getDouble("fontSize", 14.0);
        boolean animations = prefs.getBoolean("animations", true);
        String theme = prefs.get("theme", "light");

        fontSizeSlider.setValue(fontSize);
        animationsCheckbox.setSelected(animations);
        applyTheme(theme);
    }

    @FXML
    public void savePreferences() {
        prefs.putDouble("fontSize", fontSizeSlider.getValue());
        prefs.putBoolean("animations", animationsCheckbox.isSelected());

        // Déterminer le thème actif
        String currentTheme = lightThemeBox.getStyle().contains("border-color: #3b82f6") ? "light" : "dark";
        prefs.put("theme", currentTheme);

        showInfo("Succès", "Préférences enregistrées !");
    }

    @FXML
    public void retourDashboard(ActionEvent event) {
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/views/Dashboard.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Erreur", "Impossible de revenir au tableau de bord.");
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}