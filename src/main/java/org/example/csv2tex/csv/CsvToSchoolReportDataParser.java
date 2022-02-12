package org.example.csv2tex.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static org.example.csv2tex.csv.CsvParsingUtil.isLevelSettingColumn;
import static org.example.csv2tex.csv.CsvParsingUtil.splitCompetencyColumnHeader;

public class CsvToSchoolReportDataParser {

    public List<SchoolReportData> parseCsvFileToReportDataList(File csvFile) throws IOException {
        Pair<List<String>, List<CSVRecord>> pair = parseToRecords(csvFile);
        List<String> headers = pair.getLeft();
        List<CSVRecord> recordList = pair.getRight();

        new CsvValidator().ensureCorrectFormat(headers, recordList);

        List<SchoolReportData> result = new ArrayList<>();
        for (CSVRecord rawData : recordList) {
            SchoolReportData singleStudentData = createReportDataFromRecord(headers, rawData);
            result.add(singleStudentData);
        }
        return result;
    }

    /**
     * @return pair of (header row titles, data rows)
     */
    private Pair<List<String>, List<CSVRecord>> parseToRecords(File csvFile) throws IOException {
        CSVFormat parser = CSVFormat.DEFAULT.builder()
                .setTrim(true)
                .build();

        List<CSVRecord> recordList = new ArrayList<>();
        try (Reader in = new FileReader(csvFile)) {
            Iterable<CSVRecord> records = parser.parse(in);
            records.forEach(recordList::add);
        }
        // skip but save first record - header
        List<String> headerRow = recordList.remove(0).toList();
        return Pair.of(headerRow, recordList);
    }

    private SchoolReportData createReportDataFromRecord(List<String> headers, CSVRecord rawRowData) {
        SchoolReportData singleStudentData = createBaseData(rawRowData);
        addCompetencyData(singleStudentData, headers, rawRowData);
        return singleStudentData;
    }

    private SchoolReportData createBaseData(CSVRecord rawData) {
        SchoolReportData singleStudentData = new SchoolReportData();
        singleStudentData.surName = rawData.get(0);
        singleStudentData.givenName = rawData.get(1);
        singleStudentData.schoolClass = rawData.get(2);
        singleStudentData.schoolYear = rawData.get(3);
        singleStudentData.partOfYear = rawData.get(4);
        singleStudentData.birthDay = rawData.get(5);
        singleStudentData.absenceDaysTotal = rawData.get(6);
        singleStudentData.absenceDaysUnauthorized = rawData.get(7);
        singleStudentData.absenceHoursTotal = rawData.get(8);
        singleStudentData.absenceHoursUnauthorized = rawData.get(9);
        return singleStudentData;
    }

    private void addCompetencyData(SchoolReportData singleStudentData, List<String> headers, CSVRecord rawData) {
        String currentLevel = "";
        for (int i = 10; i < rawData.size(); i++) {
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
