package org.example.csv2tex.placeholders;

import org.example.csv2tex.placeholders.schoolspecific.ErfurtSchoolTablePlaceholderReplacer;

public class TablePlaceholderReplacerFactory {

    // if the application will be extended to other schools, the mechanism to choose the concrete placeholder replacer
    // will be implemented here (e.g. based on system properties)
    public static TablePlaceholderReplacer getTablePlaceholderReplacer() {
        return new ErfurtSchoolTablePlaceholderReplacer();
    }
}
