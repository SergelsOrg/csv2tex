package org.example.csv2tex.placeholders.schoolspecific;

import com.google.common.annotations.VisibleForTesting;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;
import org.example.csv2tex.placeholders.TablePlaceholderReplacer;

import java.util.ArrayList;
import java.util.List;

import static java.util.regex.Matcher.quoteReplacement;

public class ErfurtSchoolTablePlaceholderReplacer implements TablePlaceholderReplacer {

    private static final String COMMAND_PLACEHOLDER_SUBJECT = "#SUBJECT";
    private static final String COMMAND_PLACEHOLDER_COMPETENCIES = "#COMPETENCIES";
    private static final String COMMAND_PLACEHOLDER_COMPETENCY = "#COMPETENCY";
    private static final String COMMAND_PLACEHOLDER_LEVEL = "#LEVEL";
    private static final String COMMAND_PLACEHOLDER_GRADE = "#GRADE";
    private static final String COMMAND_PLACEHOLDER_TEXT = "#TEXT";

    private static final String COMMAND_CALL_COMPETENCY_TABLE =
            "\\competencyTable{" +
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
    private static final String COMMAND_CALL_BOLD =
            "\\textbf{" +
                    COMMAND_PLACEHOLDER_TEXT +
                    "}\n";
    private static final String COMMAND_CALL_UNDERLINE =
            "\\uline{" +
                    COMMAND_PLACEHOLDER_TEXT +
                    "}\n";
    private static final String TEX_TABLE_LINE_BREAK = "\\tableLineBreak\n";
    private static final String TEX_TABLE_LINE_BREAK_REPLACEMENT = quoteReplacement(TEX_TABLE_LINE_BREAK);
    private static final String SPECIAL_GRADE_VALUE_GRADE_NOT_GIVEN = "nb";
    private static final String SPECIAL_GRADE_VALUE_GRADE_IN_SECOND_HALF_YEAR = "hj";

    private static final String LEVEL_RED = "\\levelThree";
    private static final String LEVEL_BLUE = "\\levelTwo";
    private static final String LEVEL_GREEN = "\\levelOne";
    private static final String LEVEL_None = "\\noLevel";

    @Override
    public String replaceTableData(SchoolReportData schoolReportData, String texFileContent) {
        String partOfYear = schoolReportData.partOfYear;
        StringBuilder tables = new StringBuilder();

        if (schoolReportData.schoolCompetencies.isEmpty()) {
            return texFileContent;
        }

        String currentSubject = schoolReportData.schoolCompetencies.get(0).schoolSubject;
        String currentLevel = schoolReportData.schoolCompetencies.get(0).level;
        List<SchoolCompetencyData> currentSubjectCompetencyList = new ArrayList<>();

        for (SchoolCompetencyData schoolCompetencyData : schoolReportData.schoolCompetencies) {
            if (shouldStartNewSection(schoolReportData, currentSubject, currentLevel, schoolCompetencyData)) {
                renderCompletedCompetencyListOfCurrentSubject(partOfYear, tables, currentSubjectCompetencyList);
                currentSubject = schoolCompetencyData.schoolSubject;
                currentLevel = schoolCompetencyData.level;
                currentSubjectCompetencyList.clear();
            }
            currentSubjectCompetencyList.add(schoolCompetencyData);
        }
        renderCompletedCompetencyListOfCurrentSubject(partOfYear, tables, currentSubjectCompetencyList);
        texFileContent = texFileContent.replace(TEX_TEMPLATE_PLACEHOLDER_TABLES, tables);
        return texFileContent;
    }

    private boolean shouldStartNewSection(SchoolReportData schoolReportData, String currentSubject, String currentLevel, SchoolCompetencyData schoolCompetencyData) {
        return isStartOfNewSubject(currentSubject, schoolCompetencyData) ||
                (hasTableBreakInSubject(currentLevel, schoolCompetencyData) && isEndYear(schoolReportData));
    }

    private boolean isStartOfNewSubject(String currentSubject, SchoolCompetencyData schoolCompetencyData) {
        return !currentSubject.equals(schoolCompetencyData.schoolSubject);
    }

    private boolean hasTableBreakInSubject(String currentLevel, SchoolCompetencyData schoolCompetencyData) {
        return !currentLevel.equals(schoolCompetencyData.level);
    }

    private boolean isEndYear(SchoolReportData schoolReportData) {
        return !schoolReportData.partOfYear.equals("Halbjahr");
    }

    private void renderCompletedCompetencyListOfCurrentSubject(String partOfYear, StringBuilder tables, List<SchoolCompetencyData> currentSubjectCompetencyList) {
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

    private boolean shouldRenderAsMajorSubject(SchoolCompetencyData firstSchoolCompetencyData, String partOfYear) {
        return partOfYear.equals("Schuljahr") ||
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
            appendBold(competency, schoolCompetencyData.schoolCompetency);
            if (!schoolCompetencyData.schoolSubCompetency.isEmpty()) {
                competency.append(TEX_TABLE_LINE_BREAK);
                appendUnderlined(competency, schoolCompetencyData.schoolSubCompetency);
            }
            if (!schoolCompetencyData.description.isEmpty()) {
                String temp = schoolCompetencyData.description.replaceAll("\r\n|\r|\n", TEX_TABLE_LINE_BREAK_REPLACEMENT);
                competency.append(TEX_TABLE_LINE_BREAK).append(temp);
            }
            String competencyReplaced = COMMAND_CALL_COMPETENCY_MAJOR_SUBJECT
                    .replace(COMMAND_PLACEHOLDER_COMPETENCY, competency)
                    .replace(COMMAND_PLACEHOLDER_GRADE, makeGrade(schoolCompetencyData.grade));
            competenciesTable.append(competencyReplaced);
        }
        return competenciesTable.toString();
    }

    private void appendUnderlined(StringBuilder text, String valueToAppend) {
        String temp = COMMAND_CALL_UNDERLINE.replace(COMMAND_PLACEHOLDER_TEXT, valueToAppend);
        text.append(temp);
    }

    private void appendBold(StringBuilder text, String valueToAppend) {
        String temp = COMMAND_CALL_BOLD.replace(COMMAND_PLACEHOLDER_TEXT, valueToAppend);
        text.append(temp);
    }

    @VisibleForTesting
    String makeCompetencyEntriesSS(List<SchoolCompetencyData> competencyList) {
        StringBuilder competenciesTable = new StringBuilder();

        for (SchoolCompetencyData schoolCompetencyData : competencyList) {
            StringBuilder competency = new StringBuilder();
            competency.append(schoolCompetencyData.schoolCompetency);
            if (!schoolCompetencyData.schoolSubCompetency.isEmpty()) {
                competency.append(TEX_TABLE_LINE_BREAK).append(schoolCompetencyData.schoolSubCompetency);
            }
            if (!schoolCompetencyData.description.isEmpty()) {
                competency.append(TEX_TABLE_LINE_BREAK)
                        .append(schoolCompetencyData.description
                                .replaceAll("\r\n|\r|\n", TEX_TABLE_LINE_BREAK_REPLACEMENT));
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
                return "\\gradeDefault";
        }
    }

    @VisibleForTesting
    String makeLevel(String level) {
        switch (level) {
            case "1":
                return LEVEL_RED;
            case "2":
                return LEVEL_BLUE;
            case "3":
                return LEVEL_GREEN;
            case "7":
                return "\\levelSeven";
            case "8":
                return "\\levelEight";
            case "9":
                return "\\levelNine";
            default:
                return LEVEL_None;
        }
    }
}
