package org.example.csv2tex.shellout;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.csv2tex.shellout.ErrorMessage.PDF_UNITE_NOT_INSTALLED;
import static org.example.csv2tex.shellout.ErrorMessage.TEX_LIVE_NOT_INSTALLED;


class ShellCommandsUtilTest {

    private ShellCommandsUtil sut = new ShellCommandsUtil();

    @Test
    @Tag("toolsInstalled")
    public void ensureCommandsExist() {
        assertThat(sut.ensureCommandsExist())
                .describedAs("should not return any error codes on a system where all utilities are installed")
                .isEmpty();
    }

    @Test
    @Tag("toolsNotInstalled")
    public void ensureCommandsExist_withMissingCommands() {
        assertThat(sut.ensureCommandsExist())
                .describedAs("should return some error codes on a system where some utilities are missing")
                .containsExactly(PDF_UNITE_NOT_INSTALLED, TEX_LIVE_NOT_INSTALLED);
    }

    @Test
    public void commandExitsSuccessfully_withSuccessfulCommand() {
        assertThat(sut.doesCommandExitSuccessfully("date")).isTrue();
    }

    @Test
    public void commandExitsSuccessfully_withNonexistentCommand() {
        assertThat(sut.doesCommandExitSuccessfully("nonexistentcommand")).isFalse();
    }

    @Test
    public void commandExitsSuccessfully_withFaultyParameters() {
        assertThat(sut.doesCommandExitSuccessfully("date", "--unsupportedParameter")).isFalse();
    }

}