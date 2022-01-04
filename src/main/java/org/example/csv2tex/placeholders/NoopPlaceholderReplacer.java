package org.example.csv2tex.placeholders;


import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Dummy implementation that does no transformation.
 * <p>
 * We have it in place until we have the real implementation.
 */
public class NoopPlaceholderReplacer implements PlaceholderReplacer {


//    @Override
    public String replacePlaceholdersInTexFile(String texTemplate, SchoolReportData schoolReportData) {
        String texFileTemplate = loadTexTemplate(texTemplate);

        String texFileContent = replaceBaseData(texFileTemplate, schoolReportData);

        String currentSubject = null;
        String subjectTableEntry = null;
        int i = 0;
        for (SchoolCompetencyData schoolCompetencyData : schoolReportData.schoolCompetencies) {
            i++;
            if (currentSubject == null) {
                currentSubject = schoolCompetencyData.schoolSubject;

                subjectTableEntry.concat(makeTableHeader(schoolCompetencyData.schoolSubject));
                subjectTableEntry.concat(makeTableEntry(schoolCompetencyData));
            } else if (currentSubject != schoolCompetencyData.schoolSubject) {
                currentSubject = schoolCompetencyData.schoolSubject;

                subjectTableEntry.concat(makeTableEntry(schoolCompetencyData));
                String stringToReplace = "#" + schoolCompetencyData.schoolSubject;
                texFileContent.replace(stringToReplace, subjectTableEntry);
            } else if (i == schoolReportData.schoolCompetencies.size()) {
                subjectTableEntry.concat(makeTableEntry(schoolCompetencyData));
                String stringToReplace = "#" + schoolCompetencyData.schoolSubject;
                texFileContent.replace(stringToReplace, subjectTableEntry);
            } else {
                subjectTableEntry.concat(makeTableEntry(schoolCompetencyData));
            }
        }
        return texFileContent;
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
    private String makeTableHeader(String schoolSubject) {
        StringBuilder tableHead = new StringBuilder();
        tableHead.append("\\hline\\multicolumn{5}{|H|}{").append(schoolSubject).append("}\\\\\\hline\\hline");
        return tableHead.toString();
    }

    private String makeTableEntry(SchoolCompetencyData schoolCompetency) {
        StringBuilder tableLine = new StringBuilder();
        if (schoolCompetency.schoolCompetency == "Werkstatt") {
            tableLine.append(schoolCompetency.schoolCompetency).append(makeGrade(schoolCompetency.grade));
        } else if (schoolCompetency.schoolSubCompetency.isEmpty()) {
            tableLine.append(schoolCompetency.schoolCompetency).append("& & & & \\");
            tableLine.append(schoolCompetency.description).append(makeGrade(schoolCompetency.grade));
        } else {
            tableLine.append(schoolCompetency.schoolCompetency).append("& & & & \\");
            tableLine.append(schoolCompetency.schoolSubCompetency).append("& & & & \\");
            tableLine.append(schoolCompetency.description).append(makeGrade(schoolCompetency.grade));
        }
        return tableLine.toString();
    }

    private String makeGrade(String grade) {
        String tableGradeString = null;
        switch (grade) {
            case "1":
                tableGradeString = "& \\textbf{X} & & & \\\\\n\\hline\n";
                break;
            case "2":
                tableGradeString = "& & \\textbf{X} & & \\\\\n\\hline\n";
                break;
            case "3":
                tableGradeString = "& & & \\textbf{X} & \\\\\n\\hline\n";
                break;
            case "4":
                tableGradeString = "& & & & \\textbf{X} \\\\\n\\hline\n";
                break;
            default:
                tableGradeString = "& & & & \\\\\n\\hline\n";
                break;
        }
        return tableGradeString;
    }
}
