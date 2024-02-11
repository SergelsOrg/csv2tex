package org.example.csv2tex.rendering;

import org.apache.commons.io.FileUtils;
import org.example.csv2tex.csv.CsvToSchoolReportDataParser;
import org.example.csv2tex.data.SchoolReportData;
import org.example.csv2tex.exception.InvalidCsvException;
import org.example.csv2tex.exception.RenderingException;
import org.example.csv2tex.placeholders.PlaceholderReplacer;
import org.example.csv2tex.placeholders.PlaceholderReplacerImpl;
import org.example.csv2tex.shellout.ShellCommandsUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.example.csv2tex.exception.RenderingExceptionCause.NO_DATA;
import static org.example.csv2tex.exception.RenderingExceptionCause.SHELL_COMMAND_FAILED;

/**
 * Implements the rendering pipeline, from given CSV and TEX files to output PDF.
 */
public class SchoolReportsRenderer {

    private final CsvToSchoolReportDataParser parser;
    private final PlaceholderReplacer placeholderReplacer;


    public SchoolReportsRenderer() {
        placeholderReplacer = new PlaceholderReplacerImpl();
        parser = new CsvToSchoolReportDataParser();
    }

    /**
     * @param csvFile CSV file containing student report data
     * @param texFile tex template file with placeholders
     * @return path to PDF with all school reports
     * @throws RenderingException  if any exception or error occurs or there is no data, an exception will be thrown
     * @throws InvalidCsvException if the CSV is considered invalid, an exception will be thrown
     */
    public Path renderSchoolReportsForGivenFiles(File csvFile, File texFile) {
        try {
            List<SchoolReportData> studentDataList = parser.parseCsvFileToReportDataList(csvFile);
            if (studentDataList.isEmpty()) {
                throw new RenderingException(NO_DATA);
            }
            String texTemplate = FileUtils.readFileToString(texFile, StandardCharsets.UTF_8);

            // running directly in the output temporary directory, because texi2pdf does not allow specifying an output directory
            Path temporaryDirectory = Files.createTempDirectory(getClass().getSimpleName());
            ShellCommandsUtil shellCommandsInTempDir = new ShellCommandsUtil(temporaryDirectory.toFile());
            new MoveFilesForRenderingHelper().moveAllTexFilesInSameFolderToTemporaryDirectory(texFile.getParentFile(), temporaryDirectory);
            return renderSchoolReportsForGivenFiles(studentDataList, texTemplate, temporaryDirectory, shellCommandsInTempDir);
        } catch (RenderingException | InvalidCsvException e) {
            throw e;
        } catch (Exception e) {
            throw new RenderingException(e);
        }
    }

    private Path renderSchoolReportsForGivenFiles(List<SchoolReportData> studentDataList, String texTemplate, Path temporaryDirectory, ShellCommandsUtil shellCommandsInTempDir) throws IOException {
        List<String> renderedPdfs = renderStudentReports(studentDataList, texTemplate, temporaryDirectory, shellCommandsInTempDir);
        Path outputFile = mergePdfs(renderedPdfs, temporaryDirectory, shellCommandsInTempDir);
        return outputFile;
    }

    private List<String> renderStudentReports(List<SchoolReportData> studentDataList, String texTemplate, Path temporaryDirectory,
                                              ShellCommandsUtil shellCommandsInTempDir) throws IOException {
        List<String> renderedPdfs = new ArrayList<>();
        for (int fileNumber = 0; fileNumber < studentDataList.size(); fileNumber++) {
            renderSingleSchoolReportPdf(studentDataList, texTemplate, temporaryDirectory, shellCommandsInTempDir, renderedPdfs, fileNumber);
        }
        return renderedPdfs;
    }

    private void renderSingleSchoolReportPdf(List<SchoolReportData> studentDataList, String texTemplate, Path temporaryDirectory,
                                             ShellCommandsUtil shellCommandsInTempDir, List<String> renderedPdfs, int fileNumber) throws IOException {
        String texWithReplacedPlaceholders = placeholderReplacer.replacePlaceholdersInTexTemplate(texTemplate, studentDataList.get(fileNumber));
        Path temporaryTexFilePath = temporaryDirectory.resolve("schoolReport_" + fileNumber + ".tex").toAbsolutePath();
        Files.writeString(temporaryTexFilePath, texWithReplacedPlaceholders);
        runShellCommandThrowing(() -> shellCommandsInTempDir.runTexi2Pdf(temporaryTexFilePath.toString()));
        renderedPdfs.add("schoolReport_" + fileNumber + ".pdf");
    }

    private Path mergePdfs(List<String> renderedPdfs, Path temporaryDirectory, ShellCommandsUtil shellCommandsInTempDir) {
        Path outputFile = temporaryDirectory.resolve("schoolReports.pdf").toAbsolutePath();
        runShellCommandThrowing(() -> shellCommandsInTempDir.runPdfUnite(outputFile.toString(), renderedPdfs));
        return outputFile;
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

}
