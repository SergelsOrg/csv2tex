package org.example.csv2tex.csv;

public class CsvParsingUtil {

    public static boolean isLevelSettingColumn(String columnHeader) {
        return "niveau".equalsIgnoreCase(columnHeader) || "level".equalsIgnoreCase(columnHeader);
    }

    public static String[] splitCompetencyColumnHeader(String columnHeader) {
        // limit 4: will keep all lines after the 4th as part of the 4th split
        return columnHeader.split("(\r\n|\r|\n)", 4);
    }
}
