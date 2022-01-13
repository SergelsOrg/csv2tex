package org.example.csv2tex.placeholders;


import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Dummy implementation that does no transformation.
 * <p>
 * We have it in place until we have the real implementation.
 */
public class NoopPlaceholderReplacer implements PlaceholderReplacer {
    public String replacePlaceholdersInTexFile(String texTemplate, SchoolReportData schoolReportData) {
        String texFileTemplate = loadTexTemplate(texTemplate);

        String texFileContent = replaceBaseData(texFileTemplate, schoolReportData);

        String partOfYear = schoolReportData.partOfYear;;
        String currentSubject = null;
        StringBuilder tables = new StringBuilder();
        List<SchoolCompetencyData> competencyList = null;

        for (SchoolCompetencyData schoolCompetencyData : schoolReportData.schoolCompetencies) {
            if (currentSubject.equals(null)) {
                competencyList.add(schoolCompetencyData);
            } else if (currentSubject != schoolCompetencyData.schoolSubject) {
                currentSubject = schoolCompetencyData.schoolSubject;

                tables.append(makeTableEntry(competencyList, partOfYear));
                competencyList.removeAll(competencyList);
                competencyList.add(schoolCompetencyData);
            } else {
                competencyList.add(schoolCompetencyData);
            }
        }
        texFileContent.replace("#Tables", tables);
        return texFileContent;
    }

    public String makeTableEntry(List<SchoolCompetencyData> competencyList, String partOfYear) {
        StringBuilder subjectTable = new StringBuilder();
        SchoolCompetencyData firstSchoolcompetencyData = competencyList.get(0);
        String competencytableMSCmd = "\\competencytableMS{#SUBJECT}{#COMPETENCIES}{#LEVEL}";
        String competencytableSSCmd = "\\competencytable{#SUBJECT}{#COMPETENCIES}";

        if (partOfYear.equals("Endjahr") ||
                firstSchoolcompetencyData.schoolSubject.equals("Mathematik") ||
                firstSchoolcompetencyData.schoolSubject.equals("Deutsch") ||
                firstSchoolcompetencyData.schoolSubject.equals("Englisch")) {
            subjectTable.append(
                    competencytableMSCmd
                            .replace("#SUBJECT", firstSchoolcompetencyData.schoolSubject)
                            .replace("#LEVEL", firstSchoolcompetencyData.level)
                            .replace("#COMPETENCIES", makeCompetencyEntriesMS(competencyList)));
        } else {
            subjectTable.append(
                    competencytableSSCmd
                            .replace("#SUBJECT", firstSchoolcompetencyData.schoolSubject)
                            .replace("#COMPETENCIES", makeCompetencyEntriesSS(competencyList)));
        }
        return subjectTable.toString();
    }

    public String makeCompetencyEntriesSS(List<SchoolCompetencyData> competencyList) {
        String competencySSCmd = "\\competencySS{#COMPETENCY}{#GRADE}{#LEVEL}";
        StringBuilder competenciesTable = new StringBuilder();

        for (SchoolCompetencyData schoolCompetencyData : competencyList) {
            competenciesTable.append(competencySSCmd.replace("#COMPETENCY", schoolCompetencyData.schoolCompetency)
                    .replace("#GRADE", makeGrade(schoolCompetencyData.grade))
                    .replace("#LEVEL", schoolCompetencyData.level));
        }
        return competenciesTable.toString();
    }

    public String makeCompetencyEntriesMS(List<SchoolCompetencyData> competencyList) {
        String competencyMSCmd = "\\competencyMS{#COMPETENCY}{#GRADE}";
        StringBuilder competenciesTable = new StringBuilder();

        for (SchoolCompetencyData schoolCompetencyData : competencyList) {
            competenciesTable.append(competencyMSCmd.replace("#COMPETENCY", schoolCompetencyData.schoolCompetency)
                    .replace("#GRADE", makeGrade(schoolCompetencyData.grade)));
        }
        return competenciesTable.toString();
    }

    public String makeGrade(String grade) {
        switch (grade) {
            case "1": return "\\gradeOne";
            case "2": return "\\gradeTwo";
            case "3": return "\\gradeThree";
            case "4": return "\\gradeFour";
            default: return "\\gradeNon";
        }
    }

    public String loadTexTemplate(String texTemplate) {
        Path path = Paths.get(texTemplate);
        String texFileContent = null;
        try {
            texFileContent = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return texFileContent;
    }

    public String replaceBaseData(String texFileContent, SchoolReportData schoolReportData) {
        texFileContent.replace("#givenName", schoolReportData.givenName);
        texFileContent.replace("#surName", schoolReportData.surName);
        texFileContent.replace("#birthDay", schoolReportData.birthDay);
        texFileContent.replace("#schoolClass", schoolReportData.schoolClass);
        texFileContent.replace("#schoolYear", schoolReportData.schoolYear);
        texFileContent.replace("#partOfYear", schoolReportData.partOfYear);

        return texFileContent;
    }
}
