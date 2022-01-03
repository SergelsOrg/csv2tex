package org.example.csv2tex.globalstate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static com.google.common.base.Suppliers.memoize;

public class GlobalState {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalState.class);
    private static final Supplier<GlobalState> INSTANCE = memoize(GlobalState::new);

    // NB: If you change the default here, do also update the preselected radio button in the FXML file
    private Locale locale = Locale.ENGLISH;

    private GlobalState() {
        // must not be called directly, call getInstance() on the class instead
    }

    public static GlobalState getInstance() {
        return INSTANCE.get();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale currentLocale) {
        LOGGER.info("Language changed to " + currentLocale.getDisplayLanguage());
        this.locale = currentLocale;
    }

    public ResourceBundle getTranslations() {
        return ResourceBundle.getBundle("translations", locale);
    }
}
