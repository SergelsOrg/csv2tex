package org.example.csv2tex.shellout;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShellCommandsUtil {

    private final File workingDirectory;

    public ShellCommandsUtil() {
        this.workingDirectory = null;
    }

    public ShellCommandsUtil(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Usually, control flow by Exception throw is bad style, so we won't throw an Exception here right away
     * (we'll do that closer to the UI layer).
     *
     * @return if all is well, an empty list. Otherwise, a list of error conditions representing
     * the condition that one required command does not exist.
     */
    public List<ErrorMessage> ensureCommandsExist() {
        return Stream.of(ensureMvExists(), ensurePdfUniteExists(), ensureTexLiveExists())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    // TODO discuss: we could likely replace usages of mv with calls to File::renameTo
    private Optional<ErrorMessage> ensureMvExists() {
        if (doesCommandExitSuccessfully("mv", "--help")) {
            return Optional.empty();
        } else {
            return Optional.of(ErrorMessage.MV_NOT_INSTALLED);
        }
    }

    private Optional<ErrorMessage> ensurePdfUniteExists() {
        if (doesCommandExitSuccessfully("pdfunite", "--help")) {
            return Optional.empty();
        } else {
            return Optional.of(ErrorMessage.PDF_UNITE_NOT_INSTALLED);
        }
    }

    private Optional<ErrorMessage> ensureTexLiveExists() {
        if (doesCommandExitSuccessfully("texi2pdf", "--help")) {
            return Optional.empty();
        } else {
            return Optional.of(ErrorMessage.TEX_LIVE_NOT_INSTALLED);
        }
    }

    public ShellResult runPdfUnite(String outputFile, List<String> filesToMerge) {
        List<String> shellCommand = new ArrayList(filesToMerge);
        shellCommand.add(0, "pdfunite");
        shellCommand.add(outputFile);
        return runShellCommand(shellCommand.toArray(new String[]{}));
    }

    public ShellResult runTexi2Pdf(String inputFile) {
        return runShellCommand("texi2pdf", inputFile);
    }

    public boolean doesCommandExitSuccessfully(String... commandAndArguments) {
        ShellResult result = runShellCommand(commandAndArguments);
        return result.successfulExit && result.exitCode.orElse(-1) == 0;
    }

    public ShellResult runShellCommand(String... commandAndArguments) {
        ProcessBuilder processBuilder = new ProcessBuilder().command(commandAndArguments)
                .directory(workingDirectory);

        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException ignored) {
            // process will still be 'null'
            return new ShellResult(Optional.empty(), Optional.empty(), Optional.empty(), false);
        }

        try {
            boolean terminatedCorrectly = process.waitFor(1, TimeUnit.SECONDS);
            return new ShellResult(getContentFromStream(process.getInputStream()),
                    getContentFromStream(process.getErrorStream()),
                    getExitValue(process), terminatedCorrectly);
        } catch (InterruptedException ignored) {
            return new ShellResult(getContentFromStream(process.getInputStream()),
                    getContentFromStream(process.getErrorStream()),
                    getExitValue(process), false);
        }
    }

    private Optional<String> getContentFromStream(InputStream inputStream) {
        try {
            String output = String.join("", IOUtils.readLines(inputStream, StandardCharsets.UTF_8));
            return Optional.of(output);
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private Optional<Integer> getExitValue(Process process) {
        try {
            return Optional.of(process.exitValue());
        } catch (IllegalThreadStateException ignored) {
            return Optional.empty();
        }
    }

    public static class ShellResult {

        // not great style to have optional fields, but I prefer this over nullable fields

        public Optional<String> stdout;
        public Optional<String> stderr;
        public Optional<Integer> exitCode;
        public boolean successfulExit;

        public ShellResult(Optional<String> stdout, Optional<String> stderr, Optional<Integer> exitCode, boolean successfulExit) {
            this.stdout = stdout;
            this.stderr = stderr;
            this.exitCode = exitCode;
            this.successfulExit = successfulExit;
        }

        @Override
        public String toString() {
            return "ShellResult{" +
                    "stdout=" + stdout +
                    ", stderr=" + stderr +
                    ", exitCode=" + exitCode +
                    ", successfulExit=" + successfulExit +
                    '}';
        }
    }
}
