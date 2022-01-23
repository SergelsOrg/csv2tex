package org.example.csv2tex.placeholders;


import com.google.common.annotations.VisibleForTesting;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.util.ArrayList;
import java.util.List;


public class PlaceholderReplacerImpl implements PlaceholderReplacer {


    private static final String COMMAND_PLACEHOLDER_SUBJECT = "#SUBJECT";
    private static final String COMMAND_PLACEHOLDER_COMPETENCIES = "#COMPETENCIES";
    private static final String COMMAND_PLACEHOLDER_COMPETENCY = "#COMPETENCY";
    private static final String COMMAND_PLACEHOLDER_LEVEL = "#LEVEL";
    private static final String COMMAND_PLACEHOLDER_GRADE = "#GRADE";

    private static final String COMMAND_CALL_COMPETENCY_TABLE =
            "\\competencytable{" +
                    COMMAND_PLACEHOLDER_SUBJECT +
                    "}{" +
                    COMMAND_PLACEHOLDER_COMPETENCIES +
                    "}\n";
    private static final String COMMAND_CALL_COMPETENCY_TABLE_MAJOR_SUBJECT =
            "\\competencyTableMajorSubject{" +
                    COMMAND_PLACEHOLDER_SUBJECT +
                    "}{" +
                    COMMAND_PLACEHOLDER_COMPETENCIES +
                    "}{" +
                    COMMAND_PLACEHOLDER_LEVEL +
                    "}\n";
    private static final String COMMAND_CALL_COMPETENCY_MAJOR_SUBJECT =
            "\\competencyMajorSubject{" +
                    COMMAND_PLACEHOLDER_COMPETENCY +
                    "}{" +
                    COMMAND_PLACEHOLDER_GRADE +
                    "}\n";
    private static final String COMMAND_CALL_COMPETENCY_MINOR_SUBJECT =
            "\\competencyMinorSubject{" +
                    COMMAND_PLACEHOLDER_COMPETENCY +
                    "}{" +
                    COMMAND_PLACEHOLDER_GRADE +
                    "}{" +
                    COMMAND_PLACEHOLDER_LEVEL +
                    "}\n";


    private static final String TEX_TEMPLATE_PLACEHOLDER_GIVEN_NAME = "#givenName";
    private static final String TEX_TEMPLATE_PLACEHOLDER_SURNAME = "#surName";
    private static final String TEX_TEMPLATE_PLACEHOLDER_BIRTHDAY = "#birthDay";
    private static final String TEX_TEMPLATE_PLACEHOLDER_SCHOOL_CLASS = "#schoolClass";
    private static final String TEX_TEMPLATE_PLACEHOLDER_SCHOOL_YEAR = "#schoolYear";
    private static final String TEX_TEMPLATE_PLACEHOLDER_PART_OF_YEAR = "#partOfYear";
    private static final String TEX_TEMPLATE_PLACEHOLDER_TABLES = "#tables";

    private static final String TEX_NEWLINE = "\\\\\n";
    private static final String SPECIAL_GRADE_VALUE_GRADE_NOT_GIVEN = "nb";
    private static final String SPECIAL_GRADE_VALUE_GRADE_IN_SECOND_HALF_YEAR = "hj";

    private static final String GERMAN_LEVEL_RED = "rot";
    private static final String GERMAN_LEVEL_BLUE = "blau";
    private static final String GERMAN_LEVEL_GREEN = "grün";

    @Override
    public String replacePlaceholdersInTexTemplate(String texTemplateAsString, SchoolReportData schoolReportData) {
        String texFileContent = replaceBaseData(texTemplateAsString, schoolReportData);
        texFileContent = replaceTableData(schoolReportData, texFileContent);
        return texFileContent;
    }

    @VisibleForTesting
    String replaceBaseData(String texFileContent, SchoolReportData schoolReportData) {
        texFileContent = texFileContent
                .replace(TEX_TEMPLATE_PLACEHOLDER_GIVEN_NAME, schoolReportData.givenName)
                .replace(TEX_TEMPLATE_PLACEHOLDER_SURNAME, schoolReportData.surName)
                .replace(TEX_TEMPLATE_PLACEHOLDER_BIRTHDAY, schoolReportData.birthDay)
                .replace(TEX_TEMPLATE_PLACEHOLDER_SCHOOL_CLASS, schoolReportData.schoolClass)
                .replace(TEX_TEMPLATE_PLACEHOLDER_SCHOOL_YEAR, schoolReportData.schoolYear)
                .replace(TEX_TEMPLATE_PLACEHOLDER_PART_OF_YEAR, schoolReportData.partOfYear);
        return texFileContent;
    }

    private String replaceTableData(SchoolReportData schoolReportData, String texFileContent) {
        String partOfYear = schoolReportData.partOfYear;
        StringBuilder tables = new StringBuilder();

        if (schoolReportData.schoolCompetencies.isEmpty()) {
            return texFileContent;
        }

        String currentSubject = schoolReportData.schoolCompetencies.get(0).schoolSubject;
        List<SchoolCompetencyData> currentSubjectCompetencyList = new ArrayList<>();

        for (SchoolCompetencyData schoolCompetencyData : schoolReportData.schoolCompetencies) {
            if (isStartOfNewSubject(currentSubject, schoolCompetencyData)) {
                renderCompletedCompetencyListOfCurrentsubject(partOfYear, tables, currentSubjectCompetencyList);
                currentSubject = schoolCompetencyData.schoolSubject;
                currentSubjectCompetencyList.clear();
            }
            currentSubjectCompetencyList.add(schoolCompetencyData);
        }
        renderCompletedCompetencyListOfCurrentsubject(partOfYear, tables, currentSubjectCompetencyList);
        texFileContent = texFileContent.replace(TEX_TEMPLATE_PLACEHOLDER_TABLES, tables);
        return texFileContent;
    }

    private boolean isStartOfNewSubject(String currentSubject, SchoolCompetencyData schoolCompetencyData) {
        return !currentSubject.equals(schoolCompetencyData.schoolSubject);
    }

    private void renderCompletedCompetencyListOfCurrentsubject(String partOfYear, StringBuilder tables, List<SchoolCompetencyData> currentSubjectCompetencyList) {
        tables.append(makeTableEntry(currentSubjectCompetencyList, partOfYear));
    }

    @VisibleForTesting
    String makeTableEntry(List<SchoolCompetencyData> competencyList, String partOfYear) {
        StringBuilder subjectTable = new StringBuilder();
        SchoolCompetencyData firstSchoolCompetencyData = competencyList.get(0);

        if (shouldRenderAsMajorSubject(firstSchoolCompetencyData, partOfYear)) {

            String competencyTableMajorSubjectCmd = COMMAND_CALL_COMPETENCY_TABLE_MAJOR_SUBJECT
                    .replace(COMMAND_PLACEHOLDER_SUBJECT, firstSchoolCompetencyData.schoolSubject)
                    .replace(COMMAND_PLACEHOLDER_LEVEL, makeLevel(firstSchoolCompetencyData.level))
                    .replace(COMMAND_PLACEHOLDER_COMPETENCIES, makeCompetencyEntriesMS(competencyList));
            subjectTable.append(competencyTableMajorSubjectCmd);
        } else {
            String competencyTableMinorSubjectCmd = COMMAND_CALL_COMPETENCY_TABLE
                    .replace(COMMAND_PLACEHOLDER_SUBJECT, firstSchoolCompetencyData.schoolSubject)
                    .replace(COMMAND_PLACEHOLDER_COMPETENCIES, makeCompetencyEntriesSS(competencyList));
            subjectTable.append(competencyTableMinorSubjectCmd);

        }
        return subjectTable.toString();
    }

    // This is a HACK - Michael needed to introduce it in hard-coded form for now
    private boolean shouldRenderAsMajorSubject(SchoolCompetencyData firstSchoolCompetencyData, String partOfYear) {
        return partOfYear.equals("Endjahr") ||
                partOfYear.equalsIgnoreCase("End of year") ||
                firstSchoolCompetencyData.schoolSubject.equals("Mathematik") ||
                firstSchoolCompetencyData.schoolSubject.equalsIgnoreCase("Mathematics") ||
                firstSchoolCompetencyData.schoolSubject.equals("Deutsch") ||
                firstSchoolCompetencyData.schoolSubject.equals("German") ||
                firstSchoolCompetencyData.schoolSubject.equals("Englisch") ||
                firstSchoolCompetencyData.schoolSubject.equals("English");
    }

    @VisibleForTesting
    String makeCompetencyEntriesMS(List<SchoolCompetencyData> competencyList) {
        StringBuilder competenciesTable = new StringBuilder();

        for (SchoolCompetencyData schoolCompetencyData : competencyList) {
            StringBuilder competency = new StringBuilder();
            competency.append(schoolCompetencyData.schoolCompetency);
            if (!schoolCompetencyData.schoolSubCompetency.isEmpty()) {
                competency.append(TEX_NEWLINE).append(schoolCompetencyData.schoolSubCompetency);
            }
            if (!schoolCompetencyData.description.isEmpty()) {
                competency.append(TEX_NEWLINE).append(schoolCompetencyData.description);
            }
            String competencyReplaced = COMMAND_CALL_COMPETENCY_MAJOR_SUBJECT
                    .replace(COMMAND_PLACEHOLDER_COMPETENCY, competency)
                    .replace(COMMAND_PLACEHOLDER_GRADE, makeGrade(schoolCompetencyData.grade));
            competenciesTable.append(competencyReplaced);
        }
        return competenciesTable.toString();
    }

    @VisibleForTesting
    String makeCompetencyEntriesSS(List<SchoolCompetencyData> competencyList) {
        StringBuilder competenciesTable = new StringBuilder();

        for (SchoolCompetencyData schoolCompetencyData : competencyList) {
            StringBuilder competency = new StringBuilder();
            competency.append(schoolCompetencyData.schoolCompetency);
            if (!schoolCompetencyData.schoolSubCompetency.isEmpty()) {
                competency.append(TEX_NEWLINE).append(schoolCompetencyData.schoolSubCompetency);
            }
            if (!schoolCompetencyData.description.isEmpty()) {
                competency.append(TEX_NEWLINE).append(schoolCompetencyData.description);
            }
            String competencyReplaced = COMMAND_CALL_COMPETENCY_MINOR_SUBJECT
                    .replace(COMMAND_PLACEHOLDER_COMPETENCY, competency)
                    .replace(COMMAND_PLACEHOLDER_GRADE, makeGrade(schoolCompetencyData.grade))
                    .replace(COMMAND_PLACEHOLDER_LEVEL, makeLevel(schoolCompetencyData.level));
            competenciesTable.append(competencyReplaced);
        }
        return competenciesTable.toString();
    }

    @VisibleForTesting
    String makeGrade(String grade) {
        switch (grade) {
            case "1":
                return "\\gradeOne";
            case "2":
                return "\\gradeTwo";
            case "3":
                return "\\gradeThree";
            case "4":
                return "\\gradeFour";
            case SPECIAL_GRADE_VALUE_GRADE_IN_SECOND_HALF_YEAR:
                return "\\gradeComesWithSecondHalfYear";
            case SPECIAL_GRADE_VALUE_GRADE_NOT_GIVEN:
                return "\\gradeNotGiven";
            default:
                return "";
        }
    }

    @VisibleForTesting
    String makeLevel(String level) {
        switch (level) {
            case "1":
                return GERMAN_LEVEL_RED;
            case "2":
                return GERMAN_LEVEL_BLUE;
            case "3":
                return GERMAN_LEVEL_GREEN;
            case "7":
                return "\\levelSeven";
            case "8":
                return "\\levelEight";
            case "9":
                return "\\levelNine";
            default:
                return "";
        }
    }
}
