package esprit.tn.chayma.services;

import java.io.*;
import java.util.Properties;

public class SettingsService {
    private static final String SETTINGS_FILE = System.getProperty("user.home") + "/.smartwallet/settings.properties";
    private static Properties settings = new Properties();
    private static SettingsService instance;

    public static SettingsService getInstance() {
        if (instance == null) {
            instance = new SettingsService();
            instance.loadSettings();
        }
        return instance;
    }

    private SettingsService() {}

    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                settings.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Valeurs par défaut
            settings.setProperty("language", "fr");
            settings.setProperty("theme", "light");
            saveSettings();
        }
    }

    public void saveSettings() {
        try {
            File dir = new File(System.getProperty("user.home") + "/.smartwallet");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
                settings.store(fos, "SmartWallet Settings");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLanguage() {
        return settings.getProperty("language", "fr");
    }

    public void setLanguage(String lang) {
        settings.setProperty("language", lang);
        saveSettings();
    }

    public String getTheme() {
        return settings.getProperty("theme", "light");
    }

    public void setTheme(String theme) {
        settings.setProperty("theme", theme);
        saveSettings();
    }
}

