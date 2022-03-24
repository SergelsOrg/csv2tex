package org.example.csv2tex.csv;

import org.example.csv2tex.exception.InvalidCsvException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.example.csv2tex.csv.CsvParsingUtil.isLevelSettingColumn;
import static org.example.csv2tex.csv.CsvParsingUtil.splitCompetencyColumnHeader;
import static org.example.csv2tex.exception.InvalidCsvCause.*;

public class CsvValidator {

    public void ensureCorrectFormat(List<String> headers, List<List<String>> rowList) {
        ensureBaseColumns(headers);
        ensureLevelColumn(headers);
        ensureRowLengths(headers, rowList);
        ensureCompleteCompetencyHeaders(headers);
    }


    private void ensureBaseColumns(List<String> headers) {
        if (headers.size() < 10) {
            throw new InvalidCsvException(TOO_FEW_COLUMNS);
        }
    }

    private void ensureLevelColumn(List<String> headers) {
        int baseDataLength = 10;
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

    private void ensureRowLengths(List<String> headers, List<List<String>> rowList) {
        if (rowList.isEmpty()) {
            return;
        }

        int headerRowLength = headers.size();
        Map<Integer, Integer> rowLengths = countRowLengths(rowList);

        boolean lengthChecksPassed = checkForSystematicFaults(headerRowLength, rowLengths);
        if (lengthChecksPassed) {
            return;
        }
        checkFaultsWithHeuristics(headerRowLength, rowLengths, rowList);
    }

    // checks cases where all content rows differ from header row

    private Map<Integer, Integer> countRowLengths(List<List<String>> rowList) {
        Map<Integer, Integer> rowLengths = new HashMap<>();
        rowList.forEach(row -> {
            int rowLength = row.size();
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
    private void checkFaultsWithHeuristics(float headerRowLength, Map<Integer, Integer> rowLengths, List<List<String>> rowList) {
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
        float ratioWrongLengthRows = (contentRowsLongerThanHeader + contentRowsShorterThanHeader) / rowList.size();
        if (ratioWrongLengthRows > 0.20f || contentRowsShorterThanHeader + contentRowsLongerThanHeader > 9) {
            if (contentRowsLongerThanHeader > contentRowsShorterThanHeader) {
                throw new InvalidCsvException(HEADER_SHORTER_THAN_CONTENT, getListOfLongerRows(headerRowLength, rowList));
            } else {
                throw new InvalidCsvException(HEADER_LONGER_THAN_CONTENT, getListOfShorterRows(headerRowLength, rowList));
            }
        } else { // assume content error
            if (contentRowsLongerThanHeader > contentRowsShorterThanHeader) {
                throw new InvalidCsvException(CONTENT_ROW_LONGER_THAN_HEADER, getListOfLongerRows(headerRowLength, rowList));
            } else {
                throw new InvalidCsvException(CONTENT_ROW_SHORTER_THAN_HEADER, getListOfShorterRows(headerRowLength, rowList));
            }
        }
    }

    private String getListOfLongerRows(float headerRowLength, List<List<String>> rowList) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < rowList.size(); i++) {
            List<String> row = rowList.get(i);
            if (isLongerThanHeader(headerRowLength, row)) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(i + 1);
            }
        }
        return sb.toString();
    }

    private boolean isLongerThanHeader(float headerRowLength, List<String> row) {
        return row.size() > headerRowLength;
    }

    private String getListOfShorterRows(float headerRowLength, List<List<String>> rowList) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < rowList.size(); i++) {
            List<String> row = rowList.get(i);
            if (isShorterThanHeader(headerRowLength, row)) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(i + 1);
            }
        }
        return sb.toString();
    }

    private boolean isShorterThanHeader(float headerRowLength, List<String> row) {
        return row.size() < headerRowLength;
    }


    private void ensureCompleteCompetencyHeaders(List<String> headers) {
        // check every column after the base data columns
        List<Integer> invalidHeaderColumnsOneBased = new ArrayList<>();
        for (int i = 10; i < headers.size(); i++) {
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
