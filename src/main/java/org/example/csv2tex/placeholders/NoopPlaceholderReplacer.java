package org.example.csv2tex.placeholders;


import org.example.csv2tex.data.SchoolReportData;

/**
 * Dummy implementation that does no transformation.
 * <p>
 * We have it in place until we have the real implementation.
 */
public class NoopPlaceholderReplacer implements PlaceholderReplacer {

    @Override
    public String replacePlaceholdersInTexFile(String texTemplate, SchoolReportData schoolReportData) {
        return texTemplate;
    }
}
