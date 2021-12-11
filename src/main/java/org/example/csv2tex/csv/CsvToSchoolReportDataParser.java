package org.example.csv2tex.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;
import org.example.csv2tex.exception.InvalidCsvException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.csv2tex.exception.InvalidCsvCause.*;

public class CsvToSchoolReportDataParser {

    public List<SchoolReportData> parseCsvFileToReportDataList(File csvFile) throws IOException {
        Pair<List<String>, List<CSVRecord>> pair = parseToRecords(csvFile);
        List<String> headers = pair.getLeft();
        List<CSVRecord> recordList = pair.getRight();

        ensureCorrectFormat(headers, recordList);

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

    private void ensureCorrectFormat(List<String> headers, List<CSVRecord> recordList) {
        ensureLevelColumn(headers);
        ensureRowLengths(headers, recordList);
    }

    private void ensureLevelColumn(List<String> headers) {
        int baseDataLength = 6;
        boolean levelColumnFound = false;
        for (String columnName : headers) {
            if (isLevelSettingColumn(columnName)) {
                levelColumnFound = true;
                break;
            }
        }
        if (headers.size() > baseDataLength && !levelColumnFound) {
            throw new InvalidCsvException(HEADER_NO_LEVEL_DEFINED);
        }
    }

    private void ensureRowLengths(List<String> headers, List<CSVRecord> recordList) {
        if(recordList.isEmpty()) {
            return;
        }
        int headerRowLength = headers.size();
        Map<Integer, Integer> rowLengths = new HashMap<>();
        recordList.forEach(record -> {
            int rowLength = record.size();
            Integer existingValue = rowLengths.getOrDefault(rowLength, 0);
            rowLengths.put(rowLength, existingValue + 1);
        });

        if (rowLengths.size() == 1 && headerRowLength == rowLengths.keySet().iterator().next()) {
            // all rows have the same number of columns
            return;
        }

        throw new InvalidCsvException(HEADER_SHORTER_THAN_CONTENT);
    }

    private SchoolReportData createReportDataFromRecord(List<String> headers, CSVRecord rawRowData) {
        SchoolReportData singleStudentData = createBaseData(rawRowData);
        addCompetencyData(singleStudentData, headers, rawRowData);
        return singleStudentData;
    }

    private SchoolReportData createBaseData(CSVRecord rawData) {
        SchoolReportData singleStudentData = new SchoolReportData();
        singleStudentData.schoolClass = rawData.get(0);
        singleStudentData.schoolYear = rawData.get(1);
        singleStudentData.partOfYear = rawData.get(2);
        singleStudentData.givenName = rawData.get(3);
        singleStudentData.surName = rawData.get(4);
        singleStudentData.birthDay = rawData.get(5);
        return singleStudentData;
    }

    private void addCompetencyData(SchoolReportData singleStudentData, List<String> headers, CSVRecord rawData) {
        String currentLevel = "";
        for (int i = 6; i < rawData.size(); i++) {
            String columnHeader = headers.get(i);
            String cellValue = rawData.get(i);

            if (isLevelSettingColumn(columnHeader)) {
                // ASSUMPTION: One "level" column sets the level for the following columns
                currentLevel = cellValue;
                continue;
            }
            singleStudentData.schoolCompetencies.add(addCompetencyData(columnHeader, cellValue, currentLevel));
        }
    }

    private boolean isLevelSettingColumn(String columnHeader) {
        return "niveau".equalsIgnoreCase(columnHeader) || "level".equalsIgnoreCase(columnHeader);
    }

    private SchoolCompetencyData addCompetencyData(String columnHeader, String cellValue, String currentLevel) {
        SchoolCompetencyData competencyData = new SchoolCompetencyData();
        String[] columnHeaderRows = columnHeader.split("[\r\n]+", 4);
        if (columnHeaderRows.length < 3) {
            // TODO invalid data?
        } else if (columnHeaderRows.length == 3) {
            // ASSUMPTION: subject, competency, description
            competencyData.schoolSubject = columnHeaderRows[0];
            competencyData.schoolCompetency = columnHeaderRows[1];
            competencyData.description = columnHeaderRows[2];
        } else if (columnHeaderRows.length == 4) {
            // ASSUMPTION: subject, competency, subcompetency, description
            competencyData.schoolSubject = columnHeaderRows[0];
            competencyData.schoolCompetency = columnHeaderRows[1];
            competencyData.schoolSubCompetency = columnHeaderRows[2];
            competencyData.description = columnHeaderRows[3];
        }
        competencyData.level = currentLevel;
        competencyData.grade = cellValue;
        return competencyData;
    }
}
