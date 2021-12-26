package org.example.csv2tex.placeholders;

import org.example.csv2tex.data.SchoolReportData;

/**
 * Replace placeholders in a given tex file.
 */
public interface PlaceholderReplacer {

    String replacePlaceholdersInTexFile(String texTemplate, SchoolReportData schoolReportData);
}
