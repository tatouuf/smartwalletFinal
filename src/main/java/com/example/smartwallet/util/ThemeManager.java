package com.example.smartwallet.util;

import javafx.scene.Scene;

public class ThemeManager {
    // Chemin relatif vers le fichier CSS dans les ressources
    private static final String DARK_THEME_PATH = "/com/example/smartwallet/dark-theme.css";
    private static boolean isDarkMode = false;

    public static void applyTheme(Scene scene) {
        if (scene == null) return;
        
        String cssUrl = ThemeManager.class.getResource(DARK_THEME_PATH).toExternalForm();
        
        if (isDarkMode) {
            if (!scene.getStylesheets().contains(cssUrl)) {
                scene.getStylesheets().add(cssUrl);
            }
        } else {
            scene.getStylesheets().remove(cssUrl);
        }
    }

    public static void setDarkMode(boolean enable) {
        isDarkMode = enable;
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
}
