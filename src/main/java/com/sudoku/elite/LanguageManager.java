package com.sudoku.elite;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

/**
 * Singleton Manager to handle Application Internationalization (I18n).
 */
public class LanguageManager {
    private static LanguageManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;

    private LanguageManager() {
        // Default to Spanish as requested, or system default if preferred
        setLanguage("es"); 
    }

    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLanguage(String languageCode) {
        currentLocale = new Locale(languageCode);
        bundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public String getString(String key, Object... args) {
        String pattern = getString(key);
        return MessageFormat.format(pattern, args);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
}
