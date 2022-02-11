package org.example.csv2tex.placeholders;

import org.example.csv2tex.data.SchoolReportData;

/**
 * Replace the table placeholder in a given-as-String tex file.
 */
public interface TablePlaceholderReplacer {
    String TEX_TEMPLATE_PLACEHOLDER_TABLES = "#tables";

    String replaceTableData(SchoolReportData schoolReportData, String texFileContent);
}
