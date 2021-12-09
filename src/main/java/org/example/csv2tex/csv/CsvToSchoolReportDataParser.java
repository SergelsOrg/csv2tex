package org.example.csv2tex.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvToSchoolReportDataParser {

    public List<SchoolReportData> parseCsvFileToReportDataList(File csvFile) throws IOException {
        Pair<List<String>, List<CSVRecord>> pair = parseToRecords(csvFile);
        List<String> headers = pair.getLeft();
        List<CSVRecord> recordList = pair.getRight();

        List<SchoolReportData> result = new ArrayList<>();
        for (CSVRecord rawData : recordList) {
            SchoolReportData singleStudentData = createDataFromRecord(headers, rawData);
            result.add(singleStudentData);
        }
        return result;
    }

    private Pair<List<String>, List<CSVRecord>> parseToRecords(File csvFile) throws IOException {
        CSVFormat parser = CSVFormat.DEFAULT.builder()
                .setTrim(true)
                // .setDelimiter("\"")
                .build();

        List<CSVRecord> recordList = new ArrayList<>();
        try (Reader in = new FileReader(csvFile)) {
            Iterable<CSVRecord> records = parser.parse(in);
            records.forEach(recordList::add);
        }
        // skip first record - header (cannot configure this out
        // with .setSkipHeaderRecord() because we're not defining a fixed header enum)
        List<String> headerRow = recordList.remove(0).toList();
        return Pair.of(headerRow, recordList);
    }

    private SchoolReportData createDataFromRecord(List<String> headers, CSVRecord rawData) {
        SchoolReportData singleStudentData = new SchoolReportData();
        singleStudentData.schoolClass = rawData.get(0);
        singleStudentData.schoolYear = rawData.get(1);
        singleStudentData.partOfYear = rawData.get(2);
        singleStudentData.givenName = rawData.get(3);
        singleStudentData.surName = rawData.get(4);
        singleStudentData.birthDay = rawData.get(5);

        String currentLevel = "";
        for (int i = 6; i < rawData.size(); i++) {
            String columnHeader = headers.get(i);
            String cellValue = rawData.get(i);

            if (isLevelSettingColumn(columnHeader)) {
                // ASSUMPTION: One "level" column sets the level for the following columns
                currentLevel = cellValue;
                continue;
            }
            singleStudentData.schoolCompetencies.add(createCompetencyData(columnHeader, cellValue, currentLevel));
        }
        return singleStudentData;
    }

    private boolean isLevelSettingColumn(String columnHeader) {
        return "niveau".equalsIgnoreCase(columnHeader) || "level".equalsIgnoreCase(columnHeader);
    }

    private SchoolCompetencyData createCompetencyData(String columnHeader, String cellValue, String currentLevel) {
        SchoolCompetencyData competencyData = new SchoolCompetencyData();
        String[] columnHeaderRows = columnHeader.split("[\r\n]+", 4);
        if (columnHeaderRows.length < 3) {
            // TODO invalid data?
        } else if (columnHeaderRows.length == 3) {
            // ASSUMPTION: subject, competency, description
            competencyData.schoolSubject = columnHeaderRows[0];
            competencyData.schoolCompetency = columnHeaderRows[1];
            competencyData.schoolSubCompetencyDescription = columnHeaderRows[2];
        } else if (columnHeaderRows.length == 4) {
            // ASSUMPTION: subject, competency, subcompetency, description
            competencyData.schoolSubject = columnHeaderRows[0];
            competencyData.schoolCompetency = columnHeaderRows[1];
            competencyData.schoolSubCompetency = columnHeaderRows[2];
            competencyData.schoolSubCompetencyDescription = columnHeaderRows[3];
        }
        competencyData.level = currentLevel;
        competencyData.grade = cellValue;
        return competencyData;
    }
}
