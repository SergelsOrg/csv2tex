package org.example.csv2tex.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.csv2tex.csv.CsvToSchoolReportDataParserTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class PdfTestingLearningTest {

    @Test
    public void pdfBoxTextStripper_extractsAllTextFromPdf() throws IOException {
        File fileFromClasspath = getFileFromClasspath("pdf/extractionTesting.pdf");
        PDDocument document = PDDocument.load(fileFromClasspath);
        PDFTextStripper extractor = new PDFTextStripper();

        String actualText = extractor.getText(document);

        assertThat(actualText).contains("This is some text on page 1.");
        assertThat(actualText).contains("This is some header text on page 1.");
        assertThat(actualText).contains("This is some footer text on page 1.");
        assertThat(actualText).contains("This is some text on page 2.");
        assertThat(actualText).contains("This is some header text on page 2.");
        assertThat(actualText).contains("This is some footer text on page 2.");
    }

    @Test
    public void pdfBoxTextStripper_withPageLimit_extractsOnlyGivenPages() throws IOException {
        File fileFromClasspath = getFileFromClasspath("pdf/extractionTesting.pdf");
        PDDocument document = PDDocument.load(fileFromClasspath);
        PDFTextStripper extractor = new PDFTextStripper();
        extractor.setStartPage(2);
        extractor.setEndPage(2);

        String actualText = extractor.getText(document);

        assertThat(actualText).doesNotContain("This is some text on page 1.");
        assertThat(actualText).doesNotContain("This is some header text on page 1.");
        assertThat(actualText).doesNotContain("This is some footer text on page 1.");
        assertThat(actualText).contains("This is some text on page 2.");
        assertThat(actualText).contains("This is some header text on page 2.");
        assertThat(actualText).contains("This is some footer text on page 2.");
    }

    @Test
    public void iTextPdfExtractor_extractsAllTextFromPdf() throws IOException {
        PdfReader reader = new PdfReader(getFileFromClasspath("pdf/extractionTesting.pdf"));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        assertThat(pageCount).isEqualTo(2);

        StringBuilder content = new StringBuilder();
        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            content.append(PdfTextExtractor.getTextFromPage(page));
        }

        String actualText = content.toString();
        assertThat(actualText).contains("This is some text on page 1.");
        assertThat(actualText).contains("This is some header text on page 1.");
        assertThat(actualText).contains("This is some footer text on page 1.");
        assertThat(actualText).contains("This is some text on page 2.");
        assertThat(actualText).contains("This is some header text on page 2.");
        assertThat(actualText).contains("This is some footer text on page 2.");
    }

    @Test
    public void iTextPdfExtractor_withPageLimit_extractsOnlyGivenPages() throws IOException {
        PdfReader reader = new PdfReader(getFileFromClasspath("pdf/extractionTesting.pdf"));
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        assertThat(pageCount).isEqualTo(2);
        PdfPage page = document.getPage(2);

        String actualText = PdfTextExtractor.getTextFromPage(page);

        assertThat(actualText).doesNotContain("This is some text on page 1.");
        assertThat(actualText).doesNotContain("This is some header text on page 1.");
        assertThat(actualText).doesNotContain("This is some footer text on page 1.");
        assertThat(actualText).contains("This is some text on page 2.");
        assertThat(actualText).contains("This is some header text on page 2.");
        assertThat(actualText).contains("This is some footer text on page 2.");
    }

    private static File getFileFromClasspath(String pathToResource) {
        URL resource = CsvToSchoolReportDataParserTest.class.getClassLoader().getResource(pathToResource);
        assertThat(resource)
                .describedAs("file not found in classpath: " + pathToResource)
                .isNotNull();
        return new File(resource.getFile());
    }
}
