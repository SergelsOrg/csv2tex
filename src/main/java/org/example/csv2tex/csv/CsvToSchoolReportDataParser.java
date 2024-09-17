package org.example.csv2tex.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.example.csv2tex.csv.CsvParsingUtil.isLevelSettingColumn;
import static org.example.csv2tex.csv.CsvParsingUtil.splitCompetencyColumnHeader;

public class CsvToSchoolReportDataParser {

    public static final int NUMBER_OF_FIXED_CSV_COLUMNS = 11;

    public List<SchoolReportData> parseCsvFileToReportDataList(File csvFile) throws IOException {
        Pair<List<String>, List<List<String>>> pair = parseToRows(csvFile);
        List<String> headers = pair.getLeft();
        List<List<String>> rowList = pair.getRight();

        new CsvValidator().ensureCorrectFormat(headers, rowList);

        List<SchoolReportData> result = new ArrayList<>();
        for (List<String> rawRowData : rowList) {
            SchoolReportData singleStudentData = createReportDataFromRow(headers, rawRowData);
            result.add(singleStudentData);
        }
        return result;
    }

    /**
     * @return pair of (header row titles, data rows)
     */
    private Pair<List<String>, List<List<String>>> parseToRows(File csvFile) throws IOException {
        CSVFormat parser = CSVFormat.DEFAULT.builder()
                .setTrim(true)
                .build();

        List<List<String>> stringListLines = new ArrayList<>();
        try (Reader in = new FileReader(csvFile)) {
            Iterable<CSVRecord> csvLines = parser.parse(in);

            for (CSVRecord line : csvLines) {

                stringListLines.add(toStringList(line));
            }
        }
        stripEmptyColumnsRight(stringListLines);
        // skip but save first row - header
        List<String> headerRow = stringListLines.remove(0);
        return Pair.of(headerRow, stringListLines);
    }

    private List<String> toStringList(CSVRecord csvLine) {
        List<String> stringLine = newArrayList();
        for (String cell : csvLine) {
            stringLine.add(cell);
        }
        return stringLine;
    }


    private void stripEmptyColumnsRight(List<List<String>> stringListLines) {
        int lastColumnIndex = stringListLines.get(0).size() - 1;

        outer:
        while (lastColumnIndex >= 0) {
            for (List<String> row : stringListLines) {
                if (!isLastCellEmpty(row)) {
                    break outer;
                }
            }
            stringListLines.forEach(this::removeLastCell);
            lastColumnIndex--;
        }
    }

    private boolean isLastCellEmpty(List<String> row) {
        return StringUtils.isEmpty(row.get(row.size() - 1));
    }

    private void removeLastCell(List<String> row) {
        row.remove(row.size() - 1);
    }

    private SchoolReportData createReportDataFromRow(List<String> headers, List<String> rawRowData) {
        SchoolReportData singleStudentData = createBaseData(rawRowData);
        addCompetencyData(singleStudentData, headers, rawRowData);
        return singleStudentData;
    }

    private SchoolReportData createBaseData(List<String> rawData) {
        SchoolReportData singleStudentData = new SchoolReportData();
        singleStudentData.surName = rawData.get(0);
        singleStudentData.givenName = rawData.get(1);
        singleStudentData.schoolClass = rawData.get(2);
        singleStudentData.schoolYear = rawData.get(3);
        singleStudentData.partOfYear = rawData.get(4);
        singleStudentData.birthDay = rawData.get(5);
        singleStudentData.absenceDaysTotal = CsvParsingUtil.checkEmptySetDefaultZero(rawData.get(6));
        singleStudentData.absenceDaysUnauthorized = CsvParsingUtil.checkEmptySetDefaultZero(rawData.get(7));
        singleStudentData.absenceHoursTotal = CsvParsingUtil.checkEmptySetDefaultZero(rawData.get(8));
        singleStudentData.absenceHoursUnauthorized = CsvParsingUtil.checkEmptySetDefaultZero(rawData.get(9));
        singleStudentData.certificateText = rawData.get(10);
        return singleStudentData;
    }

    private void addCompetencyData(SchoolReportData singleStudentData, List<String> headers, List<String> rawData) {
        String currentLevel = "";
        for (int i = NUMBER_OF_FIXED_CSV_COLUMNS; i < rawData.size(); i++) {
            String columnHeader = headers.get(i);
            String cellValue = rawData.get(i);

            if (isLevelSettingColumn(columnHeader)) {
                // ASSUMPTION: One "level" column sets the level for the following columns
                currentLevel = cellValue;
                continue;
            }
            singleStudentData.schoolCompetencies.add(addCompetencyData(columnHeader, currentLevel, cellValue));
        }
    }

    private SchoolCompetencyData addCompetencyData(String columnHeader, String currentLevel, String cellValue) {
        SchoolCompetencyData competencyData = new SchoolCompetencyData();
        String[] columnHeaderRows = splitCompetencyColumnHeader(columnHeader);
        // ASSUMPTION: subject, competency
        competencyData.schoolSubject = columnHeaderRows[0];
        competencyData.schoolCompetency = columnHeaderRows[1];
        if (columnHeaderRows.length == 3) {
            // ASSUMPTION: subject, competency, description
            competencyData.description = columnHeaderRows[2];
        } else if (columnHeaderRows.length == 4) {
            // ASSUMPTION: subject, competency, subcompetency, description
            // If you don't have a subcompetency, but a multi-line description: Leave an empty line!
            competencyData.schoolSubCompetency = columnHeaderRows[2];
            competencyData.description = columnHeaderRows[3];
        }
        competencyData.level = currentLevel;
        competencyData.grade = cellValue;
        return competencyData;
    }

}
