package org.example.csv2tex.shellout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import java.io.File;
import java.net.URL;

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
        assertThat(sut.doesCommandExitSuccessfully("texi2pdf", "src/test/resources/shellout/page1.tex")).isTrue();
        assertThat(sut.doesCommandExitSuccessfully("texi2pdf", "src/test/resources/shellout/page2.tex")).isTrue();
        // TODO check Path?
        File outFile1 = new File("page1.pdf");
        assertThat(outFile1)
            .describedAs("file not found in classpath: page1.pdf")
            .isNotNull();
        File outFile2 = new File("page2.pdf");
        assertThat(outFile2)
            .describedAs("file not found in classpath: page2.pdf")
            .isNotNull();
    }

    @Test
    public void pdfUniteExitsSuccessfully() {
        assertThat(sut.doesCommandExitSuccessfully("pdfunite", "page1.pdf", "page2.pdf", "/tmp/pages.pdf")).isTrue();
        // TODO check Path?
        File outFile = new File("/tmp/pages.pdf");
        assertThat(outFile)
                .describedAs("file not found in classpath: pages.pdf")
                .isNotNull();
    }
}