package org.example.csv2tex.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data class representing one school report (report card).
 * <p></p>
 * One such dataset contains data for
 * <ul>
 * <li>one student</li>
 * <li>one term</li>
 * <li>all subjects + respective school grade</li>
 * <li>a number of boolean variables for parameters (e.g. "passed the term")</li>
 * </ul>
 */
public class SchoolReportData {

    // TODO:
    //  In general, names are a complex topic, a separation into surname and given name is not always possible
    //  like that.
    //  Please consider if a simple "name" field containing everything would be an option.
    public String surname = "";
    public String givenName = "";

    /**
     * Birthdate, represented as a String - we don't want to care about complex date formats etc
     */
    public String birthday = "";

    /**
     * Ordered listing of subjects and respective grades.
     * <p>
     * Again, represented as a String, as we don't want to take care of
     * different grade representations (A, B, C... vs 1, 2, 3, ... vs ?).
     */
    // TODO use a Pair class instead from some kind of util library instead of Map.Entry
    public final List<Map.Entry<String, String>> subjectToGrade = new ArrayList<>();

    /**
     * Boolean data that will be part of the report, e.g. "advances to the next grade" or "will stay down / repeat the grade"
     */
    public final List<Boolean> booleanInformation = new ArrayList<>();

}
