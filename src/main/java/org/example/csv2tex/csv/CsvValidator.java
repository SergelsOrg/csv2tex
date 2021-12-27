package org.example.csv2tex.csv;

import org.apache.commons.csv.CSVRecord;
import org.example.csv2tex.exception.InvalidCsvException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.example.csv2tex.csv.CsvParsingUtil.isLevelSettingColumn;
import static org.example.csv2tex.csv.CsvParsingUtil.splitCompetencyColumnHeader;
import static org.example.csv2tex.exception.InvalidCsvCause.*;
import static org.example.csv2tex.exception.InvalidCsvCause.HEADER_COMPETENCY_INCOMPLETE_DEFINITION;

public class CsvValidator {

    public void ensureCorrectFormat(List<String> headers, List<CSVRecord> recordList) {
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
                if (splitCompetencyColumnHeader(header).length < 2) {
                    invalidHeaderColumnsOneBased.add(i + 1);
                }
            }
        }
        if (!invalidHeaderColumnsOneBased.isEmpty()) {
            throw new InvalidCsvException(HEADER_COMPETENCY_INCOMPLETE_DEFINITION,
                    invalidHeaderColumnsOneBased.stream().map(Object::toString).collect(joining(",")));
        }
    }

}
