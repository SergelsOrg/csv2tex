package org.example.csv2tex.data;

import java.io.File;
import java.util.List;

public class SchoolReportDataParser {

    private int numberOfSubjects;

    /**
     * Parses data from a given CSV file into {@link SchoolReportData} structures.
     * <p></p>
     * It assumes the following columns in the given CSV file, and thus only needs the number of subjects as input parameter:
     * <ul>
     *     <li>last name</li>
     *     <li>given name</li>
     *     <li>date of birth</li>
     *     <li>a number of column pairs: subject, followed by grade</li>
     *     <li>remaining columns are assumed to represent boolean values</li>
     * </ul>
     * (Other approaches would be just as easily possible)
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
