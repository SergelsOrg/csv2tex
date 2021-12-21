package org.example.csv2tex.rendering;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.csv2tex.exception.InvalidCsvException;
import org.example.csv2tex.exception.RenderingException;
import org.example.csv2tex.exception.RenderingExceptionCause;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SchoolReportsRendererTest {

    private static String FILE_PATH_PREFIX = "src/test/resources/rendering/";

    private static final File NONEXISTENT_TEX_FILE = new File("nonexistent.tex");
    private static final File NO_PLACEHOLDERS_TEX_FILE = new File(FILE_PATH_PREFIX + "no_placeholders.tex");

    private static final File NONEXISTENT_CSV_FILE = new File("nonexistent.csv");
    private static final File EMPTY_CSV_FILE = new File(FILE_PATH_PREFIX + "student_data_example_empty.csv");
    private static final File INVALID_CSV_FILE = new File(FILE_PATH_PREFIX + "student_data_faulty_one_line_competency.csv");
    private static final File VALID_CSV_FILE = new File(FILE_PATH_PREFIX + "student_data_example_full.csv");
    private static final File VALID_CSV_FILE_ONE_STUDENT = new File(FILE_PATH_PREFIX + "student_data_example_full_one_student.csv");

    private final SchoolReportsRenderer sut = new SchoolReportsRenderer();

    @Test
    public void renderSchoolReportsForGivenFiles_withInvalidCsv_throwsException() {
        assertThatThrownBy(() -> sut.renderSchoolReportsForGivenFiles(INVALID_CSV_FILE, NO_PLACEHOLDERS_TEX_FILE))
                .isInstanceOf(InvalidCsvException.class);
    }

    @Test
    public void renderSchoolReportsForGivenFiles_withNoStudents_throwsException() {
        assertThatThrownBy(() -> sut.renderSchoolReportsForGivenFiles(EMPTY_CSV_FILE, NO_PLACEHOLDERS_TEX_FILE))
                .isInstanceOf(RenderingException.class)
                .extracting(e -> ((RenderingException) e).getErrorCode())
                .isEqualTo(RenderingExceptionCause.NO_DATA);
    }

    @Test
    public void renderSchoolReportsForGivenFiles_withNonExistentCsv_throwsException() {
        assertThatThrownBy(() -> sut.renderSchoolReportsForGivenFiles(NONEXISTENT_CSV_FILE, NO_PLACEHOLDERS_TEX_FILE))
                .describedAs("As the UI prevents the user from selecting a nonexistent file, we expect a generic exception")
                .isInstanceOf(RenderingException.class)
                .hasMessageContaining("No such file")
                .extracting(e -> ((RenderingException) e).getErrorCode())
                .isEqualTo(RenderingExceptionCause.UNEXPECTED)
        ;
    }

    @Test
    public void renderSchoolReportsForGivenFiles_withNonExistentTex_throwsException() {
        assertThatThrownBy(() -> sut.renderSchoolReportsForGivenFiles(NONEXISTENT_TEX_FILE, NO_PLACEHOLDERS_TEX_FILE))
                .describedAs("As the UI prevents the user from selecting a nonexistent file, we expect a generic exception")
                .isInstanceOf(RenderingException.class)
                .hasMessageContaining("No such file")
                .extracting(e -> ((RenderingException) e).getErrorCode())
                .isEqualTo(RenderingExceptionCause.UNEXPECTED)
        ;
    }

    @Test
    public void renderSchoolReportsForGivenFiles_forOneStudent_rendersOutputPdf() throws IOException {
        Path outputFilePath = sut.renderSchoolReportsForGivenFiles(VALID_CSV_FILE_ONE_STUDENT, NO_PLACEHOLDERS_TEX_FILE);

        assertThat(outputFilePath).exists();
        PDDocument doc = PDDocument.load(outputFilePath.toFile());
        assertThat(doc.getNumberOfPages()).isEqualTo(1);
        String pdfText = new PDFTextStripper().getText(doc);
        assertThat(pdfText).isEqualTo("No placeholders here.\n1\n");
    }

    @Test
    public void renderSchoolReportsForGivenFiles_forMultipleStudents_rendersOutputPdf() throws IOException {
        Path outputFilePath = sut.renderSchoolReportsForGivenFiles(VALID_CSV_FILE, NO_PLACEHOLDERS_TEX_FILE);

        assertThat(outputFilePath).exists();
        PDDocument doc = PDDocument.load(outputFilePath.toFile());
        assertThat(doc.getNumberOfPages()).isEqualTo(3);
        String pdfText = new PDFTextStripper().getText(doc);
        assertThat(pdfText).isEqualTo("No placeholders here.\n1\n" +
                "No placeholders here.\n1\n" +
                "No placeholders here.\n1\n"
        );
    }
}