package org.example.csv2tex.placeholders;


import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.util.ArrayList;
import java.util.List;


public class PlaceholderReplacerImpl implements PlaceholderReplacer {
    public String replacePlaceholdersInTexTemplate(String texTemplateAsString, SchoolReportData schoolReportData) {

        String texFileContent = replaceBaseData(texTemplateAsString, schoolReportData);

        String partOfYear = schoolReportData.partOfYear;
        String currentSubject = schoolReportData.schoolCompetencies.get(0).schoolSubject;
        StringBuilder tables = new StringBuilder();
        List<SchoolCompetencyData> competencyList = new ArrayList<>();

        for (SchoolCompetencyData schoolCompetencyData : schoolReportData.schoolCompetencies) {
            if (!currentSubject.equals(schoolCompetencyData.schoolSubject)) {
                currentSubject = schoolCompetencyData.schoolSubject;

                tables.append(makeTableEntry(competencyList, partOfYear));
                //FIXME
                competencyList.removeAll(competencyList);
                competencyList.add(schoolCompetencyData);
            } else {
                competencyList.add(schoolCompetencyData);
            }
        }
        tables.append(makeTableEntry(competencyList, partOfYear));
        texFileContent = texFileContent.replace("#tables", tables);
        return texFileContent;
    }

    public String makeTableEntry(List<SchoolCompetencyData> competencyList, String partOfYear) {
        StringBuilder subjectTable = new StringBuilder();
        SchoolCompetencyData firstSchoolcompetencyData = competencyList.get(0);
        String competencyTableMajorSubjectCmd = "\\competencyTableMajorSubject{#SUBJECT}{#COMPETENCIES}{#LEVEL}\n";
        String competencyTableMinorSubjectCmd = "\\competencytable{#SUBJECT}{#COMPETENCIES}\n";

        if (partOfYear.equals("Endjahr") ||
                firstSchoolcompetencyData.schoolSubject.equals("Mathematik") ||
                firstSchoolcompetencyData.schoolSubject.equals("Deutsch") ||
                firstSchoolcompetencyData.schoolSubject.equals("Englisch")) {

            competencyTableMajorSubjectCmd = competencyTableMajorSubjectCmd
                    .replace("#SUBJECT", firstSchoolcompetencyData.schoolSubject)
                    .replace("#LEVEL", makeLevel(firstSchoolcompetencyData.level))
                    .replace("#COMPETENCIES", makeCompetencyEntriesMS(competencyList));
            subjectTable.append(competencyTableMajorSubjectCmd);
        } else {
            competencyTableMinorSubjectCmd = competencyTableMinorSubjectCmd
                    .replace("#SUBJECT", firstSchoolcompetencyData.schoolSubject)
                    .replace("#COMPETENCIES", makeCompetencyEntriesSS(competencyList));
            subjectTable.append(competencyTableMinorSubjectCmd);

        }
        return subjectTable.toString();
    }

    public String makeCompetencyEntriesMS(List<SchoolCompetencyData> competencyList) {
        String competencyMSCmd = "\\competencyMS{#COMPETENCY}{#GRADE}\n";
        StringBuilder competenciesTable = new StringBuilder();

        for (SchoolCompetencyData schoolCompetencyData : competencyList) {
            StringBuilder competency = new StringBuilder();
            competency.append(schoolCompetencyData.schoolCompetency);
            if (!schoolCompetencyData.schoolSubCompetency.isEmpty()) {
                competency.append("\\\\\n").append(schoolCompetencyData.schoolSubCompetency);
            }
            if (!schoolCompetencyData.description.isEmpty()) {
                competency.append("\\\\\n").append(schoolCompetencyData.description);
            }
            String competencyReplaced = competencyMSCmd
                    .replace("#COMPETENCY", competency)
                    .replace("#GRADE", makeGrade(schoolCompetencyData.grade));
            competenciesTable.append(competencyReplaced);
        }
        return competenciesTable.toString();
    }

    public String makeCompetencyEntriesSS(List<SchoolCompetencyData> competencyList) {
        String competencySSCmd = "\\competencySS{#COMPETENCY}{#GRADE}{#LEVEL}\n";
        StringBuilder competenciesTable = new StringBuilder();

        for (SchoolCompetencyData schoolCompetencyData : competencyList) {
            StringBuilder competency = new StringBuilder();
            competency.append(schoolCompetencyData.schoolCompetency);
            if (!schoolCompetencyData.schoolSubCompetency.isEmpty()) {
                competency.append("\\\\\n").append(schoolCompetencyData.schoolSubCompetency);
            }
            if (!schoolCompetencyData.description.isEmpty()) {
                competency.append("\\\\\n").append(schoolCompetencyData.description);
            }
            String competencyReplaced = competencySSCmd
                    .replace("#COMPETENCY", competency)
                    .replace("#GRADE", makeGrade(schoolCompetencyData.grade))
                    .replace("#LEVEL", makeLevel(schoolCompetencyData.level));
            competenciesTable.append(competencyReplaced);
        }
        return competenciesTable.toString();
    }

    public String makeGrade(String grade) {
        switch (grade) {
            case "1": return "\\gradeOne";
            case "2": return "\\gradeTwo";
            case "3": return "\\gradeThree";
            case "4": return "\\gradeFour";
            case "hj": return "\\gradeHalfYear";
            case "nb": return "\\gradeNotGiven";
            default: return "";
        }
    }

    public String makeLevel(String level) {
        switch (level) {
            case "1": return "rot";
            case "2": return "blau";
            case "3": return "grün";
            case "7": return "\\levelSeven";
            case "8": return "\\levelEight";
            case "9": return "\\levelNine";
            default: return "";
        }
    }

    public String replaceBaseData(String texFileContent, SchoolReportData schoolReportData) {
        texFileContent = texFileContent
                .replace("#givenName", schoolReportData.givenName)
                .replace("#surName", schoolReportData.surName)
                .replace("#birthDay", schoolReportData.birthDay)
                .replace("#schoolClass", schoolReportData.schoolClass)
                .replace("#schoolYear", schoolReportData.schoolYear)
                .replace("#partOfYear", schoolReportData.partOfYear);
        return texFileContent;
    }
}
