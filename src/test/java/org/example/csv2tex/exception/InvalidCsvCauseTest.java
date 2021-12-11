package org.example.csv2tex.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.csv2tex.exception.InvalidCsvCause.*;

class InvalidCsvCauseTest {
    @Test
    public void getMessageWithPlaceholderValue_replacesPlaceholdersIfPresent() {
        assertThat(InvalidCsvCause.getMessageWithPlaceholderValue(HEADER_NO_LEVEL_DEFINED, "1,2,3"))
                .doesNotContain("1,2,3");
        assertThat(InvalidCsvCause.getMessageWithPlaceholderValue(HEADER_SHORTER_THAN_CONTENT, "1,2,3"))
                .contains("1,2,3");
        assertThat(InvalidCsvCause.getMessageWithPlaceholderValue(CONTENT_ROW_LONGER_THAN_HEADER, "1,2,3"))
                .contains("1,2,3");
    }
}