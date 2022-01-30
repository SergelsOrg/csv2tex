package org.example.csv2tex.placeholders;

import org.example.csv2tex.data.SchoolReportData;

/**
 * Replace placeholders in a given-as-String tex file.
 */
public interface PlaceholderReplacer {

    String replacePlaceholdersInTexTemplate(String texTemplateAsString, SchoolReportData schoolReportData);
}
