package org.example.csv2tex.shellout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

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
    public void runShellCommand_inDefaultDirectory_hasSamePwdAsJava() throws Exception {
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
    public void runShellCommand_inTmpDirectory_hasDifferentPwdThanJava() throws Exception {
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

}