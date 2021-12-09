package org.example.csv2tex.csv;

import org.example.csv2tex.data.SchoolCompetencyData;
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

    @Test
    public void parseCsvFileToReportDataList_parsesSingleCompetencyData() throws Exception {
        File file = getCsvFileFromClasspath("csv/student_data_example_one_competency.csv");

        List<SchoolReportData> actual = sut.parseCsvFileToReportDataList(file);

        assertThat(actual).hasSize(3);
        SchoolReportData student1 = actual.get(0);
        assertStudent1BaseData(student1);
        assertThat(student1.schoolCompetencies).hasSize(1);
        SchoolCompetencyData student1Competency = student1.schoolCompetencies.get(0);
        assertCompetency1Data(student1Competency);
        assertStudent1FirstCompetency(student1Competency);

        SchoolReportData student2 = actual.get(1);
        assertStudent2BaseData(student2);
        assertThat(student2.schoolCompetencies).hasSize(1);
        SchoolCompetencyData student2Competency = student2.schoolCompetencies.get(0);
        assertCompetency1Data(student2Competency);
        assertStudent2FirstCompetency(student2Competency);

        SchoolReportData student3 = actual.get(2);
        assertStudent3BaseData(student3);
        assertThat(student3.schoolCompetencies).hasSize(1);
        SchoolCompetencyData student3Competency = student3.schoolCompetencies.get(0);
        assertCompetency1Data(student3Competency);
        assertStudent3FirstCompetency(student3Competency);
    }

    @Test
    public void parseCsvFileToReportDataList_parsesShortSingleCompetencyData() throws Exception {
        File file = getCsvFileFromClasspath("csv/student_data_example_one_competency2.csv");

        List<SchoolReportData> actual = sut.parseCsvFileToReportDataList(file);

        assertThat(actual).hasSize(3);
        SchoolReportData student1 = actual.get(0);
        assertStudent1BaseData(student1);
        assertThat(student1.schoolCompetencies).hasSize(1);
        SchoolCompetencyData student1Competency = student1.schoolCompetencies.get(0);
        assertAlternateCompetency1Data(student1Competency);
        assertStudent1FirstCompetency(student1Competency);

        SchoolReportData student2 = actual.get(1);
        assertStudent2BaseData(student2);
        assertThat(student2.schoolCompetencies).hasSize(1);
        SchoolCompetencyData student2Competency = student2.schoolCompetencies.get(0);
        assertAlternateCompetency1Data(student2Competency);
        assertStudent2FirstCompetency(student2Competency);

        SchoolReportData student3 = actual.get(2);
        assertStudent3BaseData(student3);
        assertThat(student3.schoolCompetencies).hasSize(1);
        SchoolCompetencyData student3Competency = student3.schoolCompetencies.get(0);
        assertAlternateCompetency1Data(student3Competency);
        assertStudent3FirstCompetency(student3Competency);
    }

    @Test
    public void parseCsvFileToReportDataList_parsesFullExampleFile() throws Exception {
        File file = getCsvFileFromClasspath("csv/student_data_example_full.csv");

        List<SchoolReportData> actual = sut.parseCsvFileToReportDataList(file);

        assertThat(actual).hasSize(3);
        SchoolReportData student1 = actual.get(0);
        assertStudent1BaseData(student1);
        List<SchoolCompetencyData> student1Competencies = student1.schoolCompetencies;
        assertThat(student1Competencies).hasSize(9);
        assertStudent1Competencies(student1Competencies);

        SchoolReportData student2 = actual.get(1);
        assertStudent2BaseData(student2);
        List<SchoolCompetencyData> student2Competencies = student2.schoolCompetencies;
        assertThat(student2Competencies).hasSize(9);
        assertStudent2Competencies(student2Competencies);

        SchoolReportData student3 = actual.get(2);
        assertStudent3BaseData(student3);
        List<SchoolCompetencyData> student3Competencies = student3.schoolCompetencies;
        assertThat(student3Competencies).hasSize(9);
        assertStudent3Competencies(student3Competencies);
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

    private void assertStudent1Competencies(List<SchoolCompetencyData> studentCompetencies) {
        assertCompetency1Data(studentCompetencies.get(0));
        assertCompetency2Data(studentCompetencies.get(1));
        assertCompetency3Data(studentCompetencies.get(2));
        assertCompetency4Data(studentCompetencies.get(3));
        assertCompetency5Data(studentCompetencies.get(4));
        assertCompetency6Data(studentCompetencies.get(5));
        assertCompetency7Data(studentCompetencies.get(6));
        assertCompetency8Data(studentCompetencies.get(7));
        assertCompetency9Data(studentCompetencies.get(8));

        assertThat(studentCompetencies.get(0).level).isEqualTo("1");
        assertThat(studentCompetencies.get(1).level).isEqualTo("1");
        assertThat(studentCompetencies.get(2).level).isEqualTo("1");
        assertThat(studentCompetencies.get(3).level).isEqualTo("1");
        assertThat(studentCompetencies.get(4).level).isEqualTo("3");
        assertThat(studentCompetencies.get(5).level).isEqualTo("3");
        assertThat(studentCompetencies.get(6).level).isEqualTo("3");
        assertThat(studentCompetencies.get(7).level).isEqualTo("3");
        assertThat(studentCompetencies.get(8).level).isEqualTo("3");

        assertThat(studentCompetencies.get(0).grade).isEqualTo("1");
        assertThat(studentCompetencies.get(1).grade).isEqualTo("2");
        assertThat(studentCompetencies.get(2).grade).isEqualTo("1");
        assertThat(studentCompetencies.get(3).grade).isEqualTo("2");
        assertThat(studentCompetencies.get(4).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(5).grade).isEqualTo("3");
        assertThat(studentCompetencies.get(6).grade).isEqualTo("3");
        assertThat(studentCompetencies.get(7).grade).isEqualTo("2");
        assertThat(studentCompetencies.get(8).grade).isEqualTo("2");
    }

    private void assertStudent2Competencies(List<SchoolCompetencyData> studentCompetencies) {
        assertCompetency1Data(studentCompetencies.get(0));
        assertCompetency2Data(studentCompetencies.get(1));
        assertCompetency3Data(studentCompetencies.get(2));
        assertCompetency4Data(studentCompetencies.get(3));
        assertCompetency5Data(studentCompetencies.get(4));
        assertCompetency6Data(studentCompetencies.get(5));
        assertCompetency7Data(studentCompetencies.get(6));
        assertCompetency8Data(studentCompetencies.get(7));
        assertCompetency9Data(studentCompetencies.get(8));

        assertThat(studentCompetencies.get(0).level).isEqualTo("2");
        assertThat(studentCompetencies.get(1).level).isEqualTo("2");
        assertThat(studentCompetencies.get(2).level).isEqualTo("2");
        assertThat(studentCompetencies.get(3).level).isEqualTo("2");
        assertThat(studentCompetencies.get(4).level).isEqualTo("2");
        assertThat(studentCompetencies.get(5).level).isEqualTo("2");
        assertThat(studentCompetencies.get(6).level).isEqualTo("2");
        assertThat(studentCompetencies.get(7).level).isEqualTo("2");
        assertThat(studentCompetencies.get(8).level).isEqualTo("2");

        assertThat(studentCompetencies.get(0).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(1).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(2).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(3).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(4).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(5).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(6).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(7).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(8).grade).isEqualTo("4");
    }

    private void assertStudent3Competencies(List<SchoolCompetencyData> studentCompetencies) {
        assertCompetency1Data(studentCompetencies.get(0));
        assertCompetency2Data(studentCompetencies.get(1));
        assertCompetency3Data(studentCompetencies.get(2));
        assertCompetency4Data(studentCompetencies.get(3));
        assertCompetency5Data(studentCompetencies.get(4));
        assertCompetency6Data(studentCompetencies.get(5));
        assertCompetency7Data(studentCompetencies.get(6));
        assertCompetency8Data(studentCompetencies.get(7));
        assertCompetency9Data(studentCompetencies.get(8));

        assertThat(studentCompetencies.get(0).level).isEqualTo("3");
        assertThat(studentCompetencies.get(1).level).isEqualTo("3");
        assertThat(studentCompetencies.get(2).level).isEqualTo("3");
        assertThat(studentCompetencies.get(3).level).isEqualTo("3");
        assertThat(studentCompetencies.get(4).level).isEqualTo("1");
        assertThat(studentCompetencies.get(5).level).isEqualTo("1");
        assertThat(studentCompetencies.get(6).level).isEqualTo("1");
        assertThat(studentCompetencies.get(7).level).isEqualTo("1");
        assertThat(studentCompetencies.get(8).level).isEqualTo("1");

        assertThat(studentCompetencies.get(0).grade).isEqualTo("1");
        assertThat(studentCompetencies.get(1).grade).isEqualTo("2");
        assertThat(studentCompetencies.get(2).grade).isEqualTo("1");
        assertThat(studentCompetencies.get(3).grade).isEqualTo("2");
        assertThat(studentCompetencies.get(4).grade).isEqualTo("4");
        assertThat(studentCompetencies.get(5).grade).isEqualTo("3");
        assertThat(studentCompetencies.get(6).grade).isEqualTo("3");
        assertThat(studentCompetencies.get(7).grade).isEqualTo("2");
        assertThat(studentCompetencies.get(8).grade).isEqualTo("2");
    }

    private void assertStudent1FirstCompetency(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.level).isEqualTo("1");
        assertThat(studentCompetency.grade).isEqualTo("1");
    }

    private void assertStudent2FirstCompetency(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.level).isEqualTo("2");
        assertThat(studentCompetency.grade).isEqualTo("4");
    }

    private void assertStudent3FirstCompetency(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.level).isEqualTo("3");
        assertThat(studentCompetency.grade).isEqualTo("1");
    }

    private void assertCompetency1Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Deutsch");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Texte rezipieren");
        assertThat(studentCompetency.schoolSubCompetency).isEqualTo("Lese- und Hörverstehen");
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann Informationen aus Texten entnehmen und wiedergeben. Ich kann Inhalt und Aussage von Texten wiedergeben. Ich kenne die Gattungsmerkmale von Märchen und Sagen.");
    }

    private void assertCompetency2Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Deutsch");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Texte produzieren");
        assertThat(studentCompetency.schoolSubCompetency).isEqualTo("Sprechen");
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann mich an Gesprächen und Diskussionen beteiligen. Ich besitze Sicherheit im einfachen Berichten und im Beschreiben. Ich kann freie Redebeiträge (Vorträge), ggf. mit Stichwortzettel, leisten. Ich kann Texte / Textpassagen fließend und gestaltend vorlesen (sinnbetont vortragen).");
    }

    private void assertCompetency3Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Deutsch");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Texte produzieren");
        assertThat(studentCompetency.schoolSubCompetency).isEqualTo("Schreiben");
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann Texte mit Hilfe von Vorgaben planen und schreiben (z. B. Märchen, Tierbeschreibungen, Brief, Erzählung, Tagebucheintrag etc.).");
    }

    private void assertCompetency4Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Deutsch");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Über Sprache, Sprachverwendung und Sprachenlernen reflektieren");
        assertThat(studentCompetency.schoolSubCompetency).isEqualTo("Wortebene");
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann einen Grundbestand an Rechtschreibregeln sicher anwenden. Ich kann die Wortarten benennen. Ich kann Rechtschreibstrategien erkennen und sicher anwenden.");
    }

    private void assertCompetency5Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Mathe");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Rechenoperationen");
        assertThat(studentCompetency.schoolSubCompetency).isEmpty();
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann die Grundrechenoperationen im Bereich der natürlichen im Kopf und schriftlich ausführen und an Beispielen den Zusammenhang zwischen Rechenoperationen und deren Umkehroperationen erläutern. Ich kann Teiler und Vielfache natürlicher Zahlen bestimmen. Ich kann ein Verfahren zur Bestimmung von Primzahlen anwenden.");
    }

    private void assertCompetency6Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Mathe");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Mathematik mit Alltagsbezug");
        assertThat(studentCompetency.schoolSubCompetency).isEmpty();
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann Größen der Zeit, der Länge, der Masse, des Geldes, vergleichen, ordnen und umrechnen. Ich kann einfache Probleme aus dem Alltag lösen, in denen mehrere Rechenoperationen miteinander zu verknüpfen sind und negative Zahlen vorkommen (z. B. Temperaturänderungen).");
    }

    private void assertCompetency7Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Mathe");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Rationale Zahlen");
        assertThat(studentCompetency.schoolSubCompetency).isEmpty();
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann natürliche und gebrochene Zahlen in verschiedenen Situationen lesen, im mündlichen und schriftlichen Sprachgebrauch sicher und sachgemäß verwenden. Ich kann Bruchteile zeichnerisch darstellen, aus geometrischen Darstellungen ablesen, gebrochene Zahlen der Situation angemessen darstellen. Ich kann natürliche Zahlen und einfache gemeine Brüche aus Alltagssituationen ordnen und vergleichen.");
    }

    private void assertCompetency8Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Mathe");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Geometrie");
        assertThat(studentCompetency.schoolSubCompetency).isEmpty();
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann die Begriffe Strecke, Strahl, Gerade unterscheiden. Ich kann die Lagebeziehung von Geraden beschreiben.");
    }

    private void assertCompetency9Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Mathe");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Texte rezipieren");
        assertThat(studentCompetency.schoolSubCompetency).isEmpty();
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Hör-/Hör-Sehverstehen Ich kann vertraute Wörter und einfache Sätze verstehen, die sich um mich selbst, meine eigene Familie oder auf konkrete Dinge um mich herum beziehen.");
    }

    private void assertAlternateCompetency1Data(SchoolCompetencyData studentCompetency) {
        assertThat(studentCompetency.schoolSubject).isEqualTo("Mathe");
        assertThat(studentCompetency.schoolCompetency).isEqualTo("Rechenoperationen");
        assertThat(studentCompetency.schoolSubCompetency).isEmpty();
        assertThat(studentCompetency.schoolSubCompetencyDescription).isEqualTo("Ich kann die Grundrechenoperationen im Bereich der natürlichen im Kopf und schriftlich ausführen und an Beispielen den Zusammenhang zwischen Rechenoperationen und deren Umkehroperationen erläutern. Ich kann Teiler und Vielfache natürlicher Zahlen bestimmen. Ich kann ein Verfahren zur Bestimmung von Primzahlen anwenden.");
    }

    private static File getCsvFileFromClasspath(String pathToResource) {
        URL resource = CsvToSchoolReportDataParserTest.class.getClassLoader().getResource(pathToResource);
        assertThat(resource)
                .describedAs("file not found in classpath: " + pathToResource)
                .isNotNull();
        return new File(resource.getFile());
    }
}