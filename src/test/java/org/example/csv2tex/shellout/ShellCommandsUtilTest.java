package org.example.csv2tex.shellout;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.csv2tex.shellout.ErrorMessage.PDF_UNITE_NOT_INSTALLED;
import static org.example.csv2tex.shellout.ErrorMessage.TEX_LIVE_NOT_INSTALLED;


class ShellCommandsUtilTest {

    @TempDir
    private File tempDir;

    private ShellCommandsUtil shellCommands;
    private ShellCommandsUtil shellCommandsInTmpFolder;

    @BeforeEach
    public void setUp() {
        shellCommands = new ShellCommandsUtil();
        shellCommandsInTmpFolder = new ShellCommandsUtil(tempDir);
    }

    @Test
    public void ensureCommandsExist() {
        assertThat(shellCommands.ensureCommandsExist())
                .describedAs("should not return any error codes on a system where all utilities are installed")
                .isEmpty();
    }

    @Test
    @Tag("toolsNotInstalled")
    public void ensureCommandsExist_withMissingCommands() {
        assertThat(shellCommands.ensureCommandsExist())
                .describedAs("should return some error codes on a system where some utilities are missing")
                .containsExactly(PDF_UNITE_NOT_INSTALLED, TEX_LIVE_NOT_INSTALLED);
    }

    @Test
    public void commandExitsSuccessfully_withSuccessfulCommand() {
        assertThat(shellCommands.doesCommandExitSuccessfully("date")).isTrue();
    }

    @Test
    public void runShellCommand_inDefaultDirectory_hasSamePwdAsJava() {
        // arrange
        String javaWorkingDirectory = new File(".").getAbsolutePath();

        // act
        ShellCommandsUtil.ShellResult normalPwdResult = shellCommands.runShellCommand("pwd");

        // assert
        assertThat(normalPwdResult.successfulExit).isTrue();
        assertThat(normalPwdResult.exitCode)
                .isPresent()
                .hasValue(0);
        assertThat(normalPwdResult.stdout).isPresent();
        String shellDefaultWorkingDirectory = normalPwdResult.stdout.get();

        assertThat(shellDefaultWorkingDirectory).isNotEmpty();
        assertThat(javaWorkingDirectory)
                .isIn(shellDefaultWorkingDirectory, shellDefaultWorkingDirectory + "/.");
    }

    @Test
    public void runShellCommand_inTmpDirectory_hasDifferentPwdThanJava() {
        // arrange
        String javaWorkingDirectory = new File(".").getAbsolutePath();

        // act
        ShellCommandsUtil.ShellResult tempDirectoryPwdResult = shellCommandsInTmpFolder.runShellCommand("pwd");

        // assert
        assertThat(tempDirectoryPwdResult.successfulExit).isTrue();
        assertThat(tempDirectoryPwdResult.exitCode)
                .isPresent()
                .hasValue(0);
        assertThat(tempDirectoryPwdResult.stdout).isPresent();
        String tmpWorkingDirectory = tempDirectoryPwdResult.stdout.get();
        assertThat(tmpWorkingDirectory).isNotEmpty();
        assertThat(javaWorkingDirectory)
                .isNotIn(tmpWorkingDirectory, tmpWorkingDirectory + "/.");
    }

    @Test
    public void commandExitsSuccessfully_withNonexistentCommand() {
        assertThat(shellCommands.doesCommandExitSuccessfully("nonexistentcommand")).isFalse();
    }

    @Test
    public void commandExitsSuccessfully_withFaultyParameters() {
        assertThat(shellCommands.doesCommandExitSuccessfully("date", "--unsupportedParameter")).isFalse();
    }

    @Test
    public void texi2pdfExitsSuccessfully() {
        long timeSecs = System.currentTimeMillis() / 1000L;
        String texFile1 = new File("src/test/resources/shellout/page1.tex").getAbsolutePath();
        String texFile2 = new File("src/test/resources/shellout/page2.tex").getAbsolutePath();
        File expectedOutFile1 = new File(tempDir, "page1.pdf");
        File expectedOutFile2 = new File(tempDir, "page2.pdf");

        ShellCommandsUtil.ShellResult texi2pdf1 = shellCommandsInTmpFolder.runShellCommand("texi2pdf", texFile1);
        ShellCommandsUtil.ShellResult texi2pdf2 = shellCommandsInTmpFolder.runShellCommand("texi2pdf", texFile2);

        assertThat(texi2pdf1.successfulExit).isTrue();
        assertThat(texi2pdf1.exitCode).isPresent();
        assertThat(texi2pdf1.exitCode.get())
                .describedAs(texi2pdf1.toString())
                .isEqualTo(0);
        assertThat(expectedOutFile1).exists();
        assertThat(expectedOutFile1)
                .describedAs("file not found in classpath: page1.pdf")
                .isNotNull();
        assertThat(expectedOutFile1.lastModified() / 1000L).isGreaterThanOrEqualTo(timeSecs);

        assertThat(texi2pdf2.successfulExit).isTrue();
        assertThat(texi2pdf2.exitCode).isPresent();
        assertThat(texi2pdf2.exitCode.get())
                .describedAs(texi2pdf2.toString())
                .isEqualTo(0);
        assertThat(expectedOutFile2).exists();
        assertThat(expectedOutFile2)
                .describedAs("file not found in classpath: page2.pdf")
                .isNotNull();
        assertThat(expectedOutFile2.lastModified() / 1000L).isGreaterThanOrEqualTo(timeSecs);
    }

    @Test
    public void texi2pdfRunsCorrectly() {
        long timeSecs = System.currentTimeMillis() / 1000L;
        String texFile = new File("src/test/resources/shellout/page1.tex").getAbsolutePath();

        ShellCommandsUtil.ShellResult result = shellCommandsInTmpFolder.runTexi2Pdf(texFile);

        assertThat(result.successfulExit).isTrue();
        assertThat(result.exitCode).isPresent();
        assertThat(result.exitCode.get())
                .describedAs(result.toString())
                .isEqualTo(0);
        File outFile1 = new File(tempDir, "page1.pdf");
        assertThat(outFile1).exists();
        assertThat(outFile1.lastModified() / 1000L).isGreaterThanOrEqualTo(timeSecs);
        assertThat(outFile1)
                .describedAs("file not found in classpath: page1.pdf")
                .isNotNull();
    }

    @Test
    public void pdfUniteExitsSuccessfully() throws IOException {
        String pdf1 = new File("src/test/resources/shellout/page1.pdf").getAbsolutePath();
        String pdf2 = new File("src/test/resources/shellout/page2.pdf").getAbsolutePath();
        String outputFile = Files.createTempFile("output", "pdf").toAbsolutePath().toString();

        boolean successfulExit = shellCommandsInTmpFolder.doesCommandExitSuccessfully("pdfunite",
                pdf1, pdf2, outputFile);

        assertThat(successfulExit).isTrue();
        File outFile = new File(outputFile);
        assertThat(outFile)
                .describedAs("file not found in classpath: pages.pdf")
                .isNotNull();
        String[] splitSting = extractTextLinesFromPdf(outputFile);
        assertThat(splitSting[0]).isEqualTo("page1");
        assertThat(splitSting[2]).isEqualTo("page2");
    }

    @Test
    public void runPdfUnite_mergesTwoGivenPdfs() throws IOException {
        String outputPath = Files.createTempFile("output", "pdf").toAbsolutePath().toString();
        String pdf1 = new File("src/test/resources/shellout/page1.pdf").getAbsolutePath();
        String pdf2 = new File("src/test/resources/shellout/page2.pdf").getAbsolutePath();
        List<String> filesToMerge = Arrays.asList(pdf1, pdf2);

        ShellCommandsUtil.ShellResult result = shellCommandsInTmpFolder.runPdfUnite(outputPath, filesToMerge);

        String[] actualText = extractTextLinesFromPdf(outputPath);
        assertThat(actualText[0]).isEqualTo("page1");
        assertThat(actualText[1]).isEqualTo("1");
        assertThat(actualText[2]).isEqualTo("page2");
        assertThat(actualText[3]).isEqualTo("1");
        assertThat(result.exitCode).isNotEmpty();
        assertThat(result.exitCode.get()).isEqualTo(0);
        assertThat(result.successfulExit).isTrue();
    }

    private String[] extractTextLinesFromPdf(String outputFile) throws IOException {
        PDDocument doc = PDDocument.load(new File(outputFile));
        String strip = new PDFTextStripper().getText(doc);
        String[] splitSting = strip.split("[\r\n]+");
        return splitSting;
    }
}