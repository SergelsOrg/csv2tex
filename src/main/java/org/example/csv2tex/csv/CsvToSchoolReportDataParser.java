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

import static java.util.stream.Collectors.joining;
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
        ensureCompleteCompetencyHeaders(headers);
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
        if (recordList.isEmpty()) {
            return;
        }

        int headerRowLength = headers.size();
        Map<Integer, Integer> rowLengths = countRowLengths(recordList);

        boolean lengthChecksPassed = checkForSystematicFaults(headerRowLength, rowLengths);
        if (lengthChecksPassed) {
            return;
        }
        checkFaultsWithHeuristics(headerRowLength, rowLengths, recordList);
    }

    // checks cases where all content rows differ from header row

    private Map<Integer, Integer> countRowLengths(List<CSVRecord> recordList) {
        Map<Integer, Integer> rowLengths = new HashMap<>();
        recordList.forEach(record -> {
            int rowLength = record.size();
            Integer existingValue = rowLengths.getOrDefault(rowLength, 0);
            rowLengths.put(rowLength, existingValue + 1);
        });
        return rowLengths;
    }

    private boolean checkForSystematicFaults(int headerRowLength, Map<Integer, Integer> rowLengths) {
        if (rowLengths.size() == 1) {
            Integer contentRowsLength = rowLengths.keySet().iterator().next();
            if (headerRowLength == contentRowsLength) {
                // all rows have the same number of columns
                return true;
            } else if (headerRowLength > contentRowsLength) {
                throw new InvalidCsvException(HEADER_LONGER_THAN_ALL_CONTENT);
            } else if (headerRowLength < contentRowsLength) {
                throw new InvalidCsvException(HEADER_SHORTER_THAN_ALL_CONTENT);
            }
        }
        return false;
    }

    /**
     * Heuristic: determine whether to show "some data rows are wrong" or "the header row is wrong"
     */
    private void checkFaultsWithHeuristics(float headerRowLength, Map<Integer, Integer> rowLengths, List<CSVRecord> recordList) {
        float contentRowsShorterThanHeader = 0f;
        float contentRowsLongerThanHeader = 0f;
        for (Map.Entry<Integer, Integer> entry : rowLengths.entrySet()) {
            if (entry.getKey() > headerRowLength) {
                contentRowsLongerThanHeader += entry.getValue();
            } else if (entry.getKey() < headerRowLength) {
                contentRowsShorterThanHeader += entry.getValue();
            }
        }

        // more than 20% / at least 10 rows off --> assume header error
        float ratioWrongLengthRows = (contentRowsLongerThanHeader + contentRowsShorterThanHeader) / recordList.size();
        if (ratioWrongLengthRows > 0.20f || contentRowsShorterThanHeader + contentRowsLongerThanHeader > 9) {
            if (contentRowsLongerThanHeader > contentRowsShorterThanHeader) {
                throw new InvalidCsvException(HEADER_SHORTER_THAN_CONTENT, getListOfLongerRows(headerRowLength, recordList));
            } else {
                throw new InvalidCsvException(HEADER_LONGER_THAN_CONTENT, getListOfShorterRows(headerRowLength, recordList));
            }
        } else { // assume content error
            if (contentRowsLongerThanHeader > contentRowsShorterThanHeader) {
                throw new InvalidCsvException(CONTENT_ROW_LONGER_THAN_HEADER, getListOfLongerRows(headerRowLength, recordList));
            } else {
                throw new InvalidCsvException(CONTENT_ROW_SHORTER_THAN_HEADER, getListOfShorterRows(headerRowLength, recordList));
            }
        }
    }

    private String getListOfLongerRows(float headerRowLength, List<CSVRecord> recordList) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < recordList.size(); i++) {
            CSVRecord record = recordList.get(i);
            if (isLongerThanHeader(headerRowLength, record)) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(i + 1);
            }
        }
        return sb.toString();
    }

    private boolean isLongerThanHeader(float headerRowLength, CSVRecord record) {
        return record.size() > headerRowLength;
    }

    private String getListOfShorterRows(float headerRowLength, List<CSVRecord> recordList) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < recordList.size(); i++) {
            CSVRecord record = recordList.get(i);
            if (isShorterThanHeader(headerRowLength, record)) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(i + 1);
            }
        }
        return sb.toString();
    }

    private boolean isShorterThanHeader(float headerRowLength, CSVRecord record) {
        return record.size() < headerRowLength;
    }


    private void ensureCompleteCompetencyHeaders(List<String> headers) {
        // check every column after the base data columns
        List<Integer> invalidHeaderColumnsOneBased = new ArrayList<>();
        for (int i = 6; i < headers.size(); i++) {
            String header = headers.get(i);
            if (!isLevelSettingColumn(header)) {
                if (splitCompetencyColumnHeader(header).length < 3) {
                    invalidHeaderColumnsOneBased.add(i + 1);
                }
            }
        }
        if (!invalidHeaderColumnsOneBased.isEmpty()) {
            throw new InvalidCsvException(HEADER_COMPETENCY_INCOMPLETE_DEFINITION,
                    invalidHeaderColumnsOneBased.stream().map(Object::toString).collect(joining(",")));
        }
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
            singleStudentData.schoolCompetencies.add(addCompetencyData(columnHeader, currentLevel, cellValue));
        }
    }

    private boolean isLevelSettingColumn(String columnHeader) {
        return "niveau".equalsIgnoreCase(columnHeader) || "level".equalsIgnoreCase(columnHeader);
    }

    private SchoolCompetencyData addCompetencyData(String columnHeader, String currentLevel, String cellValue) {
        SchoolCompetencyData competencyData = new SchoolCompetencyData();
        String[] columnHeaderRows = splitCompetencyColumnHeader(columnHeader);
        if (columnHeaderRows.length == 3) {
            // ASSUMPTION: subject, competency, description
            competencyData.schoolSubject = columnHeaderRows[0];
            competencyData.schoolCompetency = columnHeaderRows[1];
            competencyData.description = columnHeaderRows[2];
        } else if (columnHeaderRows.length == 4) {
            // ASSUMPTION: subject, competency, subcompetency, description
            // If you don't have a subcompetency, but a multi-line description: Leave an empty line!
            competencyData.schoolSubject = columnHeaderRows[0];
            competencyData.schoolCompetency = columnHeaderRows[1];
            competencyData.schoolSubCompetency = columnHeaderRows[2];
            competencyData.description = columnHeaderRows[3];
        }
        competencyData.level = currentLevel;
        competencyData.grade = cellValue;
        return competencyData;
    }

    private String[] splitCompetencyColumnHeader(String columnHeader) {
        // limit 4: will keep all lines after the 4th as part of the 4th split
        return columnHeader.split("[\r\n]+", 4);
    }
}
