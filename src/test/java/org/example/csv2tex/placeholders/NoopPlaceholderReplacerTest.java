package org.example.csv2tex.placeholders;

import org.junit.jupiter.api.Test;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NoopPlaceholderReplacerTest {
    private final NoopPlaceholderReplacer sut = new NoopPlaceholderReplacer();

    @Test
    public void loadTexTemplate() throws Exception {
        String texTemplate = "src/test/resources/shellout/page1.tex";
        String texFileContent = sut.loadTexTemplate(texTemplate);

        assertThat(texFileContent).isEqualTo("\\documentclass[11pt,a4paper]{article}\n" +
                "\n" +
                "\\begin{document}\n" +
                "page1\n" +
                "\\end{document}");
    }

    @Test
    public void replaceBaseData() throws Exception {
        SchoolReportData schoolReportData = generateSchoolReportData();
        String texTemplate = "src/test/resources/placeholders/replace.tex";
        String texFileContent = sut.loadTexTemplate(texTemplate);
        String baseDataContent = sut.replaceBaseData(texFileContent, schoolReportData);

        assertThat(baseDataContent).isEqualTo("\\documentclass[11pt,a4paper]{article}\n" +
                "\n" +
                "\\begin{document}\n" +
                "Michael\n" +
                "Pöhle\n" +
                "29.12.1985\n" +
                "5c\n" +
                "2021/2022\n" +
                "Halbjahr\n" +
                "\n" +
                "#tables\n" +
                "\n" +
                "\\end{document}");
    }

    private SchoolReportData generateSchoolReportData() {
        SchoolReportData schoolReportData = new SchoolReportData();
        schoolReportData.birthDay = "29.12.1985";
        schoolReportData.schoolClass = "5c";
        schoolReportData.schoolYear = "2021/2022";
        schoolReportData.givenName = "Michael";
        schoolReportData.surName = "Pöhle";
        schoolReportData.partOfYear = "Halbjahr";
        schoolReportData.schoolCompetencies.add(generateSchoolCompetencyData1());
        schoolReportData.schoolCompetencies.add(generateSchoolCompetencyData2());
        schoolReportData.schoolCompetencies.add(generateSchoolCompetencyData3());

        return schoolReportData;
    }

    private SchoolCompetencyData generateSchoolCompetencyData1() {
        SchoolCompetencyData schoolCompetencyData1 = new SchoolCompetencyData();

        schoolCompetencyData1.schoolSubject = "Mathematik";
        schoolCompetencyData1.schoolCompetency = "Rechnen";
        schoolCompetencyData1.schoolSubCompetency = "Addition";
        schoolCompetencyData1.description = "Kann addieren.";
        schoolCompetencyData1.grade = "1";
        schoolCompetencyData1.level = "1";

        return schoolCompetencyData1;
    }

    private SchoolCompetencyData generateSchoolCompetencyData2() {
        SchoolCompetencyData schoolCompetencyData2 = new SchoolCompetencyData();

        schoolCompetencyData2.schoolSubject = "Mathematik";
        schoolCompetencyData2.schoolCompetency = "Rechnen";
        schoolCompetencyData2.schoolSubCompetency = "Subtraktion";
        schoolCompetencyData2.description = "Kann subtrahieren.";
        schoolCompetencyData2.grade = "2";
        schoolCompetencyData2.level = "1";

        return schoolCompetencyData2;
    }

    private SchoolCompetencyData generateSchoolCompetencyData3() {
        SchoolCompetencyData schoolCompetencyData3 = new SchoolCompetencyData();

        schoolCompetencyData3.schoolSubject = "Fremdsprache";
        schoolCompetencyData3.schoolCompetency = "Französisch";
        schoolCompetencyData3.schoolSubCompetency = "";
        schoolCompetencyData3.description = "";
        schoolCompetencyData3.grade = "1";
        schoolCompetencyData3.level = "3";

        return schoolCompetencyData3;
    }
}
