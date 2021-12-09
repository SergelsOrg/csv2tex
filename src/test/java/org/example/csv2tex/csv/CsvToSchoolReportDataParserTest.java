package org.example.csv2tex.csv;

import org.example.csv2tex.data.SchoolReportData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class CsvToSchoolReportDataParserTest {
    private CsvToSchoolReportDataParser sut = new CsvToSchoolReportDataParser();

    @Test
    public void parseCsvFileToReportDataList_parsesEmptyFile() throws Exception {
        File file = getCsvFileFromClasspath("csv/student_data_example_empty.csv");

        List<SchoolReportData> actual = sut.parseCsvFileToReportDataList(file);

        assertThat(actual).isEmpty();
    }

    @Test
    public void parseCsvFileToReportDataList_parsesBaseData() throws Exception {
        File file = getCsvFileFromClasspath("csv/student_data_example_base_data_only.csv");

        List<SchoolReportData> actual = sut.parseCsvFileToReportDataList(file);

        assertThat(actual).hasSize(3);
        SchoolReportData student1 = actual.get(0);
        assertStudent1BaseData(student1);
        assertThat(student1.schoolCompetencies).isEmpty();
        SchoolReportData student2 = actual.get(1);
        assertStudent2BaseData(student2);
        assertThat(student2.schoolCompetencies).isEmpty();
        SchoolReportData student3 = actual.get(2);
        assertStudent3BaseData(student3);
        assertThat(student3.schoolCompetencies).isEmpty();
    }

    private void assertStudent1BaseData(SchoolReportData schoolReportData) {
        // Klasse,Schuljahr,Halbjahr,Vorname,Name,Geburtstag
        // 5a,2019/2020,1,Karl,Otto,27.12.85
        assertThat(schoolReportData.birthDay).isEqualTo("27.12.85");
        assertThat(schoolReportData.givenName).isEqualTo("Karl");
        assertThat(schoolReportData.schoolClass).isEqualTo("5a");
        assertThat(schoolReportData.schoolYear).isEqualTo("2019/2020");
        assertThat(schoolReportData.partOfYear).isEqualTo("1");
        assertThat(schoolReportData.surName).isEqualTo("Otto");
    }

    private void assertStudent2BaseData(SchoolReportData schoolReportData) {
        // Klasse,Schuljahr,Halbjahr,Vorname,Name,Geburtstag
        // 5b,2020/2021,2,Ottokar,Domma,28.12.85
        assertThat(schoolReportData.birthDay).isEqualTo("28.12.85");
        assertThat(schoolReportData.givenName).isEqualTo("Ottokar");
        assertThat(schoolReportData.schoolClass).isEqualTo("5b");
        assertThat(schoolReportData.schoolYear).isEqualTo("2020/2021");
        assertThat(schoolReportData.partOfYear).isEqualTo("2");
        assertThat(schoolReportData.surName).isEqualTo("Domma");
    }

    private void assertStudent3BaseData(SchoolReportData schoolReportData) {
        // Klasse,Schuljahr,Halbjahr,Vorname,Name,Geburtstag
        // 6c,2021/2022,1,Klara,Schein,29.12.85
        assertThat(schoolReportData.birthDay).isEqualTo("29.1.85");
        assertThat(schoolReportData.givenName).isEqualTo("Klara");
        assertThat(schoolReportData.schoolClass).isEqualTo("6c");
        assertThat(schoolReportData.schoolYear).isEqualTo("2021/2022");
        assertThat(schoolReportData.partOfYear).isEqualTo("1");
        assertThat(schoolReportData.surName).isEqualTo("Schein");
    }

    private static File getCsvFileFromClasspath(String pathToResource) {
        URL resource = CsvToSchoolReportDataParserTest.class.getClassLoader().getResource(pathToResource);
        assertThat(resource)
                .describedAs("file not found in classpath: " + pathToResource)
                .isNotNull();
        return new File(resource.getFile());
    }
}
