package org.example.csv2tex.data;

import java.util.ArrayList;
import java.util.List;

/**
 * School report data for one student's school report
 */
public class SchoolReportData {

    /**
     * E.g. "5c"
     */
    public String schoolClass;

    /**
     * E.g. 2021/2022
     */
    public String schoolYear;

    /**
     * E.g., if your school does half terms, it could be 1 or 2
     * <p>
     * In the current version, will be expected as "Schuljahr" for end of year
     */
    public String partOfYear;

    public String givenName;

    public String surName;

    public String birthDay;

    public String absenceDaysTotal;

    public String absenceDaysUnauthorized;

    public String absenceHoursTotal;

    public String absenceHoursUnauthorized;

    public String certificateText = "";

    public List<SchoolCompetencyData> schoolCompetencies = new ArrayList<>();

}
