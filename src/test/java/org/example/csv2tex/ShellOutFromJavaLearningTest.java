package org.example.csv2tex;

import org.apache.commons.io.IOUtils;
import org.assertj.core.data.Offset;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static org.assertj.core.api.Assertions.*;

/**
 * Just to try out how calling the shell works from Java
 */
public class ShellOutFromJavaLearningTest {

    @BeforeAll
    public static void verifyDateCommandIsInstalledExists() throws Exception {
        Process process = new ProcessBuilder().command("date", "--version").start();
        boolean terminatedCorrectly = process.waitFor(1, TimeUnit.SECONDS);

        assertThat(terminatedCorrectly)
                .describedAs("should have terminated quickly, not timed out")
                .isTrue();
        assertThat(process.exitValue())
                .describedAs("unexpected error - is the 'date' command installed?")
                .isZero();
    }

    @Test
    public void tryOutCallToShell() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder()
                // UTC date as seconds since 1970
                .command("date", "-u", "+%s")
                // write stderr to stdout
                .redirectErrorStream(true);
        long expectedUtcTimeMillis = Instant.now(Clock.systemUTC()).toEpochMilli();

        Process process = processBuilder.start();

        assertNormalTermination(process);
        long actualUtcTimeMillis = getUtcMillisFromUtcSecondsOutput(process);
        assertThat(actualUtcTimeMillis).isCloseTo(expectedUtcTimeMillis, Offset.offset(1000L));
    }

    @Test
    public void tryOutCallToShellWithSideEffect() throws Exception {
        File tempFolder = Files.newTemporaryFolder();
        assertThat(tempFolder)
                .describedAs("precondition failed, folder was not created - check test setup")
                .exists();

        Process process = new ProcessBuilder()
                .command("rmdir", tempFolder.getAbsolutePath())
                .start();
        assertNormalTermination(process);

        assertThat(tempFolder).doesNotExist();
    }

    @Test
    public void tryOutCallToShellWithFaultyParams() throws Exception {
        Process process = new ProcessBuilder().command("date", "--unsupportedParameter").start();

        boolean terminatedCorrectly = process.waitFor(1, TimeUnit.SECONDS);

        assertThat(terminatedCorrectly)
                .describedAs("should have terminated quickly, not timed out")
                .isTrue();
        assertThat(process.exitValue())
                .describedAs("expected error exit")
                .isNotZero();
    }

    @Test
    public void tryOutCallToShellWithNonexistentCommand() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder().command("nonexistentcommand");

        // interesting behavior, good that we tried this in a learning test first
        assertThatThrownBy(() -> processBuilder.start())
                .describedAs("should fail, as the command should not exist")
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Cannot run program");
    }

    private void assertNormalTermination(Process process) throws InterruptedException {
        boolean terminatedCorrectly = process.waitFor(1, TimeUnit.SECONDS);

        assertThat(terminatedCorrectly)
                .describedAs("should have terminated quickly, not timed out")
                .isTrue();
        assertThat(process.exitValue())
                .describedAs("unexpected error - is the command installed?")
                .isZero();
    }

    private long getUtcMillisFromUtcSecondsOutput(Process process) throws IOException {
        String output = IOUtils.readLines(process.getInputStream(), StandardCharsets.UTF_8).stream()
                .collect(Collectors.joining(""));
        String sanitizedOutput = output.replaceAll("[\n\r]", "");
        assertThatCode(() -> parseLong(sanitizedOutput))
                .describedAs("command's output was likely not an integer number, please check")
                .doesNotThrowAnyException();
        long utcTimeMillis = parseLong(sanitizedOutput) * 1000L;
        return utcTimeMillis;
    }
}

