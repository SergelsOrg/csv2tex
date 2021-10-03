package org.example.csv2tex;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.SoftAssertions;
import org.example.csv2tex.data.SchoolReportData;
import org.example.csv2tex.data.SchoolReportDataParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that CSV data gets parsed correctly.
 * <p>
 * Test design:
 * <ul>
 * <li>Ideally, completing the implementation (resp, breaking the implementation) of a new bit of the code results in
 * one specific test case to succeed (resp fail) that will show one specific aspect is working (resp. broken) now.
 * </li>
 * <li>Thus, the test case is implemented in a way that has some test data sets test a specific aspect.</li>
 * </ul>
 */
public class SchoolReportDataParserTest {


    @ParameterizedTest
    @MethodSource("provideCsvFileAndExpectedResult")
    public void parseCsvFileToDataList(String description, File csvInput, int numberOfSubjects, List<SchoolReportData> expected) {
        SchoolReportDataParser sut = new SchoolReportDataParser(numberOfSubjects);

        List<SchoolReportData> actual = sut.parseCsvFileToDataList(csvInput);

        assertEqualSchoolReportDataList(actual, expected);
    }


    private void assertEqualSchoolReportDataList(List<SchoolReportData> actual, List<SchoolReportData> expected) {
        assertThat(actual).hasSize(expected.size());
        int numberOfEntries = expected.size();
        SoftAssertions.assertSoftly(softly -> {
            for (int i = 0; i < numberOfEntries; i++) {
                SchoolReportData expectedEntry = expected.get(i);
                SchoolReportData actualEntry = actual.get(i);
                assertEqualSchoolReportData(softly, actualEntry, expectedEntry);
            }
        });
    }

    private void assertEqualSchoolReportData(SoftAssertions softly, SchoolReportData actualEntry, SchoolReportData expectedEntry) {
        softly.assertThat(actualEntry.givenName)
                .describedAs("given name should match expectation")
                .isEqualTo(expectedEntry.givenName);
        softly.assertThat(actualEntry.surname)
                .describedAs("surname should match expectation")
                .isEqualTo(expectedEntry.surname);
        softly.assertThat(actualEntry.birthday)
                .describedAs("birthday should match expectation")
                .isEqualTo(expectedEntry.birthday);
        assertEqualSubjectData(softly, actualEntry.subjectToGrade, expectedEntry.subjectToGrade);
        assertEqualBooleanData(softly, actualEntry.booleanInformation, expectedEntry.booleanInformation);
    }

    private void assertEqualSubjectData(SoftAssertions softly, List<Pair<String, String>> actualSubjectToGrade, List<Pair<String, String>> expectedSubjectToGrade) {
        softly.assertThat(actualSubjectToGrade).usingRecursiveComparison()
                .describedAs("subject-grade mapping should match")
                .isEqualTo(expectedSubjectToGrade);
    }

    private void assertEqualBooleanData(SoftAssertions softly, List<Boolean> actualBooleanInformation, List<Boolean> expectedBooleanInformation) {
        softly.assertThat(actualBooleanInformation).usingRecursiveComparison()
                .describedAs("boolean list should match")
                .isEqualTo(expectedBooleanInformation);
    }


    private static Stream<Arguments> provideCsvFileAndExpectedResult() {
        return Stream.of(
                        getEmptyCsvFileTestArguments(),
                        getOnlyPupilDataCsvFileTestArguments(),
                        getWithBooleansCsvFileTestArguments(),
                        getWithBooleansAllCapsCsvFileTestArguments(),
                        getWithBooleansCapitalizedCsvFileTestArguments(),
                        getWithBooleansCrossOrEmptyCsvFileTestArguments(),
                        getWithBooleansGermanCsvFileTestArguments(),
                        getWithSubjectsFileTestArguments(),
                        getCompleteCsvFileTestArguments()
                )
                .flatMap(identity());
    }

    private static Stream<Arguments> getEmptyCsvFileTestArguments() {
        return Stream.of(Arguments.of(
                "should parse an empty file without failing",
                getCsvFileFromClasspath("csv/schoolReportDataEmpty.csv"), 13, emptyList()));
    }

    private static Stream<Arguments> getOnlyPupilDataCsvFileTestArguments() {
        SchoolReportData row1Data = new SchoolReportData();
        row1Data.surname = "Last";
        row1Data.givenName = "Robert";
        row1Data.birthday = "1.4.2007";

        SchoolReportData row2Data = new SchoolReportData();
        row2Data.surname = "Doe";
        row2Data.givenName = "John";
        row2Data.birthday = "31.8.2007";

        SchoolReportData row3Data = new SchoolReportData();
        row3Data.surname = "Public";
        row3Data.givenName = "John Q.";
        row3Data.birthday = "1.9.2007";

        SchoolReportData row4Data = new SchoolReportData();
        row4Data.surname = "Ital";
        row4Data.givenName = "Odo";
        row4Data.birthday = "31.12.1987";

        return Stream.of(Arguments.of(
                "should parse basic metadata",
                getCsvFileFromClasspath("csv/schoolReportDataOnlyPupilData.csv"), 0, asList(row1Data, row2Data, row3Data, row4Data)));
    }

    private static Stream<Arguments> getWithBooleansCsvFileTestArguments() {
        return Stream.of(Arguments.of(
                "should parse boolean content",
                getCsvFileFromClasspath("csv/schoolReportDataWithBooleans.csv"), 0, expectedParsedBooleans()));
    }

    private static Stream<Arguments> getWithBooleansAllCapsCsvFileTestArguments() {
        return Stream.of(Arguments.of(
                "should parse boolean content provided in all caps",
                getCsvFileFromClasspath("csv/schoolReportDataWithBooleansAllCaps.csv"), 0, expectedParsedBooleans()));
    }

    private static Stream<Arguments> getWithBooleansCapitalizedCsvFileTestArguments() {
        return Stream.of(Arguments.of(
                "should parse boolean content provided with first letter capitalized",
                getCsvFileFromClasspath("csv/schoolReportDataWithBooleansCapitalized.csv"), 0, expectedParsedBooleans()));
    }

    private static Stream<Arguments> getWithBooleansCrossOrEmptyCsvFileTestArguments() {
        return Stream.of(Arguments.of(
                "should parse boolean content provided as 'X' or ' '",
                getCsvFileFromClasspath("csv/schoolReportDataWithBooleansCrossOrEmpty.csv"), 0, expectedParsedBooleans()));
    }

    private static Stream<Arguments> getWithBooleansGermanCsvFileTestArguments() {
        return Stream.of(
                Arguments.of(
                        "should parse boolean content provided in German as 'ja' or 'nein' in various cases",
                        getCsvFileFromClasspath("csv/schoolReportDataWithBooleansGerman1.csv"),
                        0,
                        expectedParsedBooleans()),
                Arguments.of(
                        "should parse boolean content provided in German as 'wahr' or 'falsch' in various cases",
                        getCsvFileFromClasspath("csv/schoolReportDataWithBooleansGerman2.csv"),
                        0,
                        expectedParsedBooleans())
        );
    }

    private static Object expectedParsedBooleans() {
        SchoolReportData row1Data = new SchoolReportData();
        row1Data.surname = "Last";
        row1Data.givenName = "Robert";
        row1Data.birthday = "1.4.2007";
        row1Data.booleanInformation.addAll(asList(true, true));

        SchoolReportData row2Data = new SchoolReportData();
        row2Data.surname = "Doe";
        row2Data.givenName = "John";
        row2Data.birthday = "31.8.2007";
        row2Data.booleanInformation.addAll(asList(false, false));

        SchoolReportData row3Data = new SchoolReportData();
        row3Data.surname = "Public";
        row3Data.givenName = "John Q.";
        row3Data.birthday = "1.9.2007";
        row3Data.booleanInformation.addAll(asList(true, false));

        SchoolReportData row4Data = new SchoolReportData();
        row4Data.surname = "Ital";
        row4Data.givenName = "Odo";
        row4Data.birthday = "31.12.1987";
        row4Data.booleanInformation.addAll(asList(false, true));

        return asList(row1Data, row2Data, row3Data, row4Data);
    }

    private static Stream<Arguments> getWithSubjectsFileTestArguments() {
        SchoolReportData row1Data = new SchoolReportData();
        row1Data.surname = "Last";
        row1Data.givenName = "Robert";
        row1Data.birthday = "1.4.2007";
        row1Data.subjectToGrade.add(entry("Mathematics", "1"));
        row1Data.subjectToGrade.add(entry("Phys Ed", "3"));
        row1Data.subjectToGrade.add(entry("Arts and Crafts", "4"));

        SchoolReportData row2Data = new SchoolReportData();
        row2Data.surname = "Doe";
        row2Data.givenName = "John";
        row2Data.birthday = "31.8.2007";
        row2Data.subjectToGrade.add(entry("German", "2"));
        row2Data.subjectToGrade.add(entry("French", "6"));
        row2Data.subjectToGrade.add(entry("Arts and Crafts", "1"));

        SchoolReportData row3Data = new SchoolReportData();
        row3Data.surname = "Public";
        row3Data.givenName = "John Q.";
        row3Data.birthday = "1.9.2007";
        row3Data.subjectToGrade.add(entry("Computer Science", "2"));
        row3Data.subjectToGrade.add(entry("Geography", "3"));
        row3Data.subjectToGrade.add(entry("Religion", "1"));

        SchoolReportData row4Data = new SchoolReportData();
        row4Data.surname = "Ital";
        row4Data.givenName = "Odo";
        row4Data.birthday = "31.12.1987";
        row4Data.subjectToGrade.add(entry("Computer Science", "2"));
        row4Data.subjectToGrade.add(entry("Temporal mechanics", "4"));
        row4Data.subjectToGrade.add(entry("Cardassian Politics", "3"));

        return Stream.of(Arguments.of(
                "should parse subject data",
                getCsvFileFromClasspath("csv/schoolReportDataWithSubjects.csv"),
                3,
                asList(row1Data, row2Data, row3Data, row4Data)));
    }


    private static Stream<Arguments> getCompleteCsvFileTestArguments() {
        //        Last, Robert, 1.4.2007, Mathematics, 1, Phys Ed, 3, Arts and Crafts, 4, true, true
        SchoolReportData row1Data = new SchoolReportData();
        row1Data.surname = "Last";
        row1Data.givenName = "Robert";
        row1Data.birthday = "1.4.2007";
        row1Data.subjectToGrade.add(entry("Mathematics", "1"));
        row1Data.subjectToGrade.add(entry("Phys Ed", "3"));
        row1Data.subjectToGrade.add(entry("Arts and Crafts", "4"));
        row1Data.booleanInformation.addAll(asList(true, true));

        //        Doe, John, 31.8.2007, German, 2, French, 6, Arts and Crafts, 1, false, false
        SchoolReportData row2Data = new SchoolReportData();
        row2Data.surname = "Doe";
        row2Data.givenName = "John";
        row2Data.birthday = "31.8.2007";
        row2Data.subjectToGrade.add(entry("German", "2"));
        row2Data.subjectToGrade.add(entry("French", "6"));
        row2Data.subjectToGrade.add(entry("Arts and Crafts", "1"));
        row2Data.booleanInformation.addAll(asList(false, false));

        //        Public, John Q., 1.9.2007, Computer Science, 2, Geography, 3, Religion, 1, true, false
        SchoolReportData row3Data = new SchoolReportData();
        row3Data.surname = "Public";
        row3Data.givenName = "John Q.";
        row3Data.birthday = "1.9.2007";
        row3Data.subjectToGrade.add(entry("Computer Science", "2"));
        row3Data.subjectToGrade.add(entry("Geography", "3"));
        row3Data.subjectToGrade.add(entry("Religion", "1"));
        row3Data.booleanInformation.addAll(asList(true, false));

        //        Ital, Odo, 31.12.1987, Computer Science, 2, Temporal Mechanics, 4, Cardassian Politics, 3, false, true
        SchoolReportData row4Data = new SchoolReportData();
        row4Data.surname = "Ital";
        row4Data.givenName = "Odo";
        row4Data.birthday = "31.12.1987";
        row4Data.subjectToGrade.add(entry("Computer Science", "2"));
        row4Data.subjectToGrade.add(entry("Temporal mechanics", "4"));
        row4Data.subjectToGrade.add(entry("Cardassian Politics", "3"));
        row4Data.booleanInformation.addAll(asList(false, true));

        return Stream.of(Arguments.of(
                "should parse a file with all content elements",
                getCsvFileFromClasspath("csv/schoolReportDataComplete.csv"),
                3,
                asList(row1Data, row2Data, row3Data, row4Data)));
    }


    private static File getCsvFileFromClasspath(String pathToResource) {
        URL resource = SchoolReportDataParserTest.class.getClassLoader().getResource(pathToResource);
        assertThat(resource)
                .describedAs("file not found in classpath: " + pathToResource)
                .isNotNull();
        return new File(resource.getFile());
    }


    private static Pair<String, String> entry(String key, String value) {
        return Pair.of(key, value);
    }

}
