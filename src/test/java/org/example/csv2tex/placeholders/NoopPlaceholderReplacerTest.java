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
        String texNoTemplate = "src/test/resources/shellout/nopage.tex";
        String texFileContent = sut.loadTexTemplate(texTemplate);

        assertThat(sut.loadTexTemplate(texNoTemplate)).isNullOrEmpty();
        assertThat(texFileContent).isEqualTo("\\documentclass[11pt,a4paper]{article}\n" +
                "\n" +
                "\\begin{document}\n" +
                "page1\n" +
                "\\end{document}");
    }

    @Test
    public void makeGrade() {
        SchoolReportData schoolReportData = generateSchoolReportData();
        String grade = sut.makeGrade(schoolReportData.schoolCompetencies.get(0).grade);
        String grade1 = sut.makeGrade("1");
        String grade2 = sut.makeGrade("2");
        String grade3 = sut.makeGrade("3");
        String grade4 = sut.makeGrade("4");
        String gradeNon = sut.makeGrade("nb");
        String gradeHj = sut.makeGrade("hj");
        String gradeFalse = sut.makeGrade("7");

        assertThat(grade).isEqualTo("\\gradeOne");
        assertThat(grade1).isEqualTo("\\gradeOne");
        assertThat(grade2).isEqualTo("\\gradeTwo");
        assertThat(grade3).isEqualTo("\\gradeThree");
        assertThat(grade4).isEqualTo("\\gradeFour");
        assertThat(gradeNon).isEqualTo("\\gradeNon");
        assertThat(gradeHj).isEqualTo("\\gradeHj");
        assertThat(gradeFalse).isEqualTo("");
    }

    @Test
    public void makeLevel() {
        SchoolReportData schoolReportData = generateSchoolReportData();
        String level = sut.makeLevel(schoolReportData.schoolCompetencies.get(0).level);
        String level1 = sut.makeLevel("1");
        String level2 = sut.makeLevel("2");
        String level3 = sut.makeLevel("3");
        String level7 = sut.makeLevel("7");
        String level8 = sut.makeLevel("8");
        String level9 = sut.makeLevel("9");
        String levelNon = sut.makeLevel("");
        String levelFalse = sut.makeLevel("10");

        assertThat(level).isEqualTo("rot");
        assertThat(level1).isEqualTo("rot");
        assertThat(level2).isEqualTo("blau");
        assertThat(level3).isEqualTo("grün");
        assertThat(level7).isEqualTo("\\levelSeven");
        assertThat(level8).isEqualTo("\\levelEight");
        assertThat(level9).isEqualTo("\\levelNine");
        assertThat(levelNon).isEqualTo("");
        assertThat(levelFalse).isEqualTo("");
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

    @Test
    public void replacePlaceholdersInTexFile() throws Exception {
        SchoolReportData schoolReportData = generateSchoolReportData();
        String texTemplate = "src/test/resources/placeholders/replace.tex";
        String tableEntries = sut.replacePlaceholdersInTexFile(texTemplate, schoolReportData);

        assertThat(tableEntries).isEqualTo("\\documentclass[11pt,a4paper]{article}\n" +
                "\n" +
                "\\begin{document}\n" +
                "Michael\n" +
                "Pöhle\n" +
                "29.12.1985\n" +
                "5c\n" +
                "2021/2022\n" +
                "Halbjahr\n" +
                "\n" +
                "\\competencytableMS{Mathematik}{\\competencyMS{Rechnen\\\\\n" +
                "Addition\\\\\n" +
                "Kann addieren.}{\\gradeOne}\n" +
                "\\competencyMS{Rechnen\\\\\n" +
                "Subtraktion\\\\\n" +
                "Kann subtrahieren.}{\\gradeTwo}\n" +
                "}{rot}\n" +
                "\\competencytable{Fremdsprache}{\\competencySS{Französisch\\\\\n" +
                "sc\\\\\n" +
                "des}{\\gradeOne}{grün}\n" +
                "}\n" +
                "\n" +
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
        schoolCompetencyData3.schoolSubCompetency = "sc";
        schoolCompetencyData3.description = "des";
        schoolCompetencyData3.grade = "1";
        schoolCompetencyData3.level = "3";

        return schoolCompetencyData3;
    }
}
