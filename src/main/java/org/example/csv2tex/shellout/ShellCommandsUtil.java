package org.example.csv2tex.shellout;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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


    public boolean doesCommandExitSuccessfully(String... commandAndArguments) {
        ProcessBuilder processBuilder = new ProcessBuilder().command(commandAndArguments)
                .directory(workingDirectory);

        try {
            Process process = processBuilder.start();
            boolean terminatedCorrectly = process.waitFor(1, TimeUnit.SECONDS);
            return terminatedCorrectly && process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public ShellResult runShellCommand(String... commandAndArguments) {
        ProcessBuilder processBuilder = new ProcessBuilder().command(commandAndArguments)
                .directory(workingDirectory);

        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            return new ShellResult(getContentFromStream(process.getInputStream()),
                    getContentFromStream(process.getErrorStream()),
                    getExitValue(process), false);
        }

        try {
            boolean terminatedCorrectly = process.waitFor(1, TimeUnit.SECONDS);
            return new ShellResult(getContentFromStream(process.getInputStream()),
                    getContentFromStream(process.getErrorStream()),
                    getExitValue(process), terminatedCorrectly);
        } catch (InterruptedException e) {
            return new ShellResult(getContentFromStream(process.getInputStream()),
                    getContentFromStream(process.getErrorStream()),
                    getExitValue(process), false);
        }
    }

    private Optional<String> getContentFromStream(InputStream inputStream) {
        try {
            String output = IOUtils.readLines(inputStream, StandardCharsets.UTF_8).stream()
                    .collect(Collectors.joining(""));
            return Optional.of(output);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    private Optional<Integer> getExitValue(Process process) {
        try {
            return Optional.of(process.exitValue());
        } catch (IllegalThreadStateException e) {
            return Optional.empty();
        }
    }

    public static class ShellResult {

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

    }
}
