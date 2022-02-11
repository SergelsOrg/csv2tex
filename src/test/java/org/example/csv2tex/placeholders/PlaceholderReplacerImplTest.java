package org.example.csv2tex.placeholders;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class PlaceholderReplacerImplTest extends AbstractReplacerTest {
    private final PlaceholderReplacerImpl sut = new PlaceholderReplacerImpl();

    @Test
    public void replaceBaseData() throws Exception {
        SchoolReportData schoolReportData = generateSchoolReportData();
        File texFile = new File("src/test/resources/placeholders/replace.tex");
        String texTemplate = FileUtils.readFileToString(texFile, StandardCharsets.UTF_8);
        String baseDataContent = sut.replaceBaseData(texTemplate, schoolReportData);

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
        File texFile = new File("src/test/resources/placeholders/replace.tex");
        String texTemplate = FileUtils.readFileToString(texFile, StandardCharsets.UTF_8);
        String tableEntries = sut.replacePlaceholdersInTexTemplate(texTemplate, schoolReportData);

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
                "\\competencyTableMajorSubject{Mathematik}{\\competencyMajorSubject{Rechnen\\\\\n" +
                "Addition\\\\\n" +
                "Kann addieren.}{\\gradeOne}\n" +
                "\\competencyMajorSubject{Rechnen\\\\\n" +
                "Subtraktion\\\\\n" +
                "Kann subtrahieren.}{\\gradeTwo}\n" +
                "}{rot}\n" +
                "\\competencytable{Fremdsprache}{\\competencyMinorSubject{Französisch\\\\\n" +
                "sc\\\\\n" +
                "des}{\\gradeOne}{grün}\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\\end{document}");
    }

    @Test
    public void replacePlaceholdersInTexFile_withoutCompetencies_doesNotFail() throws Exception {
        SchoolReportData schoolReportDataWithoutCompetencies = schoolReportDataWithoutCompetencies();

        assertThatCode(() -> sut.replacePlaceholdersInTexTemplate("", schoolReportDataWithoutCompetencies))
                .doesNotThrowAnyException();
    }

    private SchoolReportData schoolReportDataWithoutCompetencies() {
        SchoolReportData schoolReportData = generateSchoolReportData();
        schoolReportData.schoolCompetencies.clear();
        return schoolReportData;
    }

}
