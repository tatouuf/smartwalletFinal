package esprit.tn.chayma.services;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationService {
    private static TranslationService instance;
    private ResourceBundle bundle;
    private String currentLanguage;

    public static TranslationService getInstance() {
        if (instance == null) {
            instance = new TranslationService();
        }
        return instance;
    }

    private TranslationService() {
        try {
            String lang = SettingsService.getInstance().getLanguage();
            setLanguage(lang);
        } catch (Exception e) {
            System.err.println("TranslationService: Error initializing, using default French");
            setLanguage("fr");
        }
    }

    public void setLanguage(String lang) {
        currentLanguage = lang;
        String baseName = "i18n/messages";

        try {
            // Essayer de charger le bundle selon la langue
            if (lang.equals("ar")) {
                try {
                    // Essayer avec le pays SA
                    bundle = ResourceBundle.getBundle(baseName, new Locale("ar", "SA"));
                    System.out.println("✅ TranslationService: Loaded ar_SA bundle");
                } catch (Exception e) {
                    // Fallback sans pays
                    bundle = ResourceBundle.getBundle(baseName, new Locale("ar"));
                    System.out.println("✅ TranslationService: Loaded ar bundle (fallback)");
                }
            } else if (lang.equals("en")) {
                try {
                    // Essayer avec le pays US
                    bundle = ResourceBundle.getBundle(baseName, new Locale("en", "US"));
                    System.out.println("✅ TranslationService: Loaded en_US bundle");
                } catch (Exception e) {
                    // Fallback sans pays
                    bundle = ResourceBundle.getBundle(baseName, new Locale("en"));
                    System.out.println("✅ TranslationService: Loaded en bundle (fallback)");
                }
            } else {
                try {
                    // Essayer avec le pays FR
                    bundle = ResourceBundle.getBundle(baseName, new Locale("fr", "FR"));
                    System.out.println("✅ TranslationService: Loaded fr_FR bundle");
                } catch (Exception e) {
                    // Fallback sans pays
                    bundle = ResourceBundle.getBundle(baseName, new Locale("fr"));
                    System.out.println("✅ TranslationService: Loaded fr bundle (fallback)");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ TranslationService: Cannot load bundle for " + lang + ": " + e.getMessage());
            // Créer un bundle vide comme dernier recours
            bundle = new ResourceBundle() {
                @Override
                protected Object handleGetObject(String key) {
                    return key;
                }

                @Override
                public Enumeration<String> getKeys() {
                    return java.util.Collections.emptyEnumeration();
                }
            };
            System.out.println("⚠️ TranslationService: Using empty bundle fallback");
        }
    }

    public String get(String key) {
        try {
            if (bundle != null && bundle.containsKey(key)) {
                return bundle.getString(key);
            }
            return key;
        } catch (Exception e) {
            return key;
        }
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}