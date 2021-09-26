package org.example.csv2tex.data;

import java.io.File;
import java.util.List;

public class SchoolReportDataParser {

    private int numberOfSubjects;

    /**
     * Parses data from a given CSV file into {@link SchoolReportData} structures.
     * <p></p>
     * It assumes the following columns in the given CSV file, and thus only needs the number of subjects as input parameter.
     * Other approaches would be just as easily possible.
     *
     * @param numberOfSubjects number of subjects contained in the report data
     */
    public SchoolReportDataParser(int numberOfSubjects) {
        this.numberOfSubjects = numberOfSubjects;
    }

    public List<SchoolReportData> parseCsvFileToDataList(File csvFile) {
        // FIXME DonMischo
        throw new RuntimeException("implement me");
    }
}
