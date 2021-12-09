package org.example.csv2tex.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.example.csv2tex.data.SchoolReportData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class CsvToSchoolReportDataParser {

    public List<SchoolReportData> parseCsvFileToReportDataList(File csvFile) throws IOException {
        List<CSVRecord> recordList = parseToRecords(csvFile);

        List<SchoolReportData> result = new ArrayList<>();
        for (CSVRecord rawData : recordList) {
            SchoolReportData singleStudentData = createDataFromRecord(rawData);
            result.add(singleStudentData);
        }
        return result;
    }

    private List<CSVRecord> parseToRecords(File csvFile) throws IOException {
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
        recordList.remove(0);
        return recordList;
    }

    private SchoolReportData createDataFromRecord(CSVRecord rawData) {
        SchoolReportData singleStudentData = new SchoolReportData();
        singleStudentData.schoolClass = rawData.get(0);
        singleStudentData.schoolYear = rawData.get(1);
        singleStudentData.partOfYear = rawData.get(2);
        singleStudentData.givenName = rawData.get(3);
        singleStudentData.surName = rawData.get(4);
        singleStudentData.birthDay = rawData.get(5);
        return singleStudentData;
    }
}
