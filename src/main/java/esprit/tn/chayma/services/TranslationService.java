package esprit.tn.chayma.services;

import java.util.ResourceBundle;
import java.util.Locale;

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
        String lang = SettingsService.getInstance().getLanguage();
        setLanguage(lang);
    }

    public void setLanguage(String lang) {
        currentLanguage = lang;
        Locale locale;
        switch (lang) {
            case "ar":
                locale = new Locale("ar", "SA");
                break;
            case "en":
                locale = new Locale("en", "US");
                break;
            default:
                locale = new Locale("fr", "FR");
        }
        try {
            bundle = ResourceBundle.getBundle("i18n.messages", locale);
        } catch (Exception e) {
            e.printStackTrace();
            bundle = ResourceBundle.getBundle("i18n.messages", new Locale("fr", "FR"));
        }
    }

    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}

