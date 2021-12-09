package org.example.csv2tex.data;

public class SchoolCompetencyData {

    /**
     * E.g., with 3 levels integrated in one school, could be "1", "2" or "3"
     * (but we won't restrict this to a number, this could change).
     */
    public String level;

    public String schoolSubject;

    public String schoolCompetency;

    public String schoolSubCompetency = "";

    public String schoolSubCompetencyDescription;

    public String grade;
}
