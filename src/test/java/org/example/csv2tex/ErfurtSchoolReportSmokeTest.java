package org.example.csv2tex;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.description.LazyTextDescription;
import org.example.csv2tex.exception.RenderingException;
import org.example.csv2tex.rendering.SchoolReportsRenderer;
import org.example.csv2tex.shellout.ShellCommandsUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.csv2tex.exception.RenderingExceptionCause.SHELL_COMMAND_FAILED;

// smoke test: only tests that the operation does not fail
public class ErfurtSchoolReportSmokeTest {

    private static final Pattern TEX_PLACEHOLDER_PATTERN = Pattern.compile("\\b\\s+(#[A-Za-z0-9-]+)\\b");
    private static final String EXAMPLE_TEMPLATE_FILE_PATH_PREFIX = "sample templates/half_year/";

    @Test
    public void testThatRenderingOfRealReportSucceeds() throws Exception {
        SchoolReportsRenderer renderer = new SchoolReportsRenderer();
        File csvFile = getFullCsvFile();
        File texFile = getTexFile();
        assertThat(csvFile).exists();
        assertThat(texFile).exists();

        Path result = renderer.renderSchoolReportsForGivenFiles(csvFile, texFile);

        doSanityCheck(result);
    }


    private File getTexFile() {
        return new File(EXAMPLE_TEMPLATE_FILE_PATH_PREFIX + "SchoolReportTemplate_HalfYear__ZeugnisTemplate_Halbjahr.tex");
    }

    private File getFullCsvFile() {
        return new File("src/test/resources/rendering/student_data_example_full.csv");
    }

    private void runShellCommandThrowing(Callable<ShellCommandsUtil.ShellResult> callable) {
        ShellCommandsUtil.ShellResult result;
        try {
            result = callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!result.successfulExit || result.exitCode.isEmpty() || result.exitCode.get() != 0) {
            throw new RenderingException(SHELL_COMMAND_FAILED, result.toString());
        }
    }

    // check that there are no things inside that look like placeholders
    private void doSanityCheck(Path result) throws Exception {
        File outputFile = result.toFile();
        assertThat(outputFile)
                .describedAs("file not found in classpath: pages.pdf")
                .isNotNull();
        String stringContent = extractTextFromPdf(outputFile);
        assertDoesNotContainPlaceholders(stringContent);
    }

    private String extractTextFromPdf(File outputFile) throws Exception {
        PDDocument doc = PDDocument.load(outputFile);
        return new PDFTextStripper().getText(doc);
    }

    private static void assertDoesNotContainPlaceholders(String stringContent) {
        Matcher regexMatcher = TEX_PLACEHOLDER_PATTERN.matcher(stringContent);
        assertThat(regexMatcher.find())
                .describedAs(new LazyTextDescription(() -> "output PDF should not contain any placeholders, but did: " + regexMatcher.group(1)))
                .isFalse();
    }

}
