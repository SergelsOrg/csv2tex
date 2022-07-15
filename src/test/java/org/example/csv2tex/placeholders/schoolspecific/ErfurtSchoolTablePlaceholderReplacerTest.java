package org.example.csv2tex.placeholders.schoolspecific;

import org.example.csv2tex.data.SchoolReportData;
import org.example.csv2tex.placeholders.AbstractReplacerTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErfurtSchoolTablePlaceholderReplacerTest extends AbstractReplacerTest {
    private final ErfurtSchoolTablePlaceholderReplacer sut = new ErfurtSchoolTablePlaceholderReplacer();

    @Test
    public void makeGrade() {
        SchoolReportData schoolReportData = generateSchoolReportData();
        String grade = sut.makeGrade(schoolReportData.schoolCompetencies.get(0).grade);
        String grade1 = sut.makeGrade("1");
        String grade2 = sut.makeGrade("2");
        String grade3 = sut.makeGrade("3");
        String grade4 = sut.makeGrade("4");
        String gradeNotGiven = sut.makeGrade("nb");
        String gradeComesWithSecondHalfYear = sut.makeGrade("hj");
        String gradeFalse = sut.makeGrade("7");

        assertThat(grade).isEqualTo("\\gradeOne\\hline");
        assertThat(grade1).isEqualTo("\\gradeOne\\hline");
        assertThat(grade2).isEqualTo("\\gradeTwo\\hline");
        assertThat(grade3).isEqualTo("\\gradeThree\\hline");
        assertThat(grade4).isEqualTo("\\gradeFour\\hline");
        assertThat(gradeNotGiven).isEqualTo("\\gradeNotGiven\\hline");
        assertThat(gradeComesWithSecondHalfYear).isEqualTo("\\gradeComesWithSecondHalfYear\\hline");
        assertThat(gradeFalse).isEqualTo("\\gradeDefault\\hline");
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

        assertThat(level).isEqualTo("\\levelOne");
        assertThat(level1).isEqualTo("\\levelOne");
        assertThat(level2).isEqualTo("\\levelTwo");
        assertThat(level3).isEqualTo("\\levelThree");
        assertThat(level7).isEqualTo("\\levelSeven");
        assertThat(level8).isEqualTo("\\levelEight");
        assertThat(level9).isEqualTo("\\levelNine");
        assertThat(levelNon).isEqualTo("\\noLevel");
        assertThat(levelFalse).isEqualTo("\\noLevel");
    }
}
