package org.example.csv2tex.exception;

import org.example.csv2tex.globalstate.GlobalState;

public enum InvalidCsvCause {

    /**
     * If there are not even enough columns to cover the student's base data
     */
    TOO_FEW_COLUMNS("exception.csv.too_few_columns"),

    /**
     * CSV has more than just base data, but no level column is defined.
     */
    HEADER_NO_LEVEL_DEFINED("exception.csv.header_no_level_column_defined"),

    HEADER_COMPETENCY_INCOMPLETE_DEFINITION("exception.csv.header_competency_incomplete_definition"),

    /**
     * The header row is shorter than all content rows.
     */
    HEADER_SHORTER_THAN_ALL_CONTENT("exception.csv.header_shorter_than_all_content"),

    /**
     * The header row is longer than all content rows.
     */
    HEADER_LONGER_THAN_ALL_CONTENT("exception.csv.header_longer_than_all_content"),

    /**
     * The header row is shorter than most content rows (heuristic: at least 10 or at least 20%).
     * Placeholder: Content row numbers
     */
    HEADER_SHORTER_THAN_CONTENT("exception.csv.header_shorter_than_content"),

    /**
     * The header row is longer than most content rows (heuristic: at least 10 or at least 20%).
     * Placeholder: Content row numbers
     */
    HEADER_LONGER_THAN_CONTENT("exception.csv.header_longer_than_content"),

    /**
     * One or more content rows are longer than the header row.
     * Placeholder: Content row numbers
     */
    CONTENT_ROW_LONGER_THAN_HEADER("exception.csv.content_row_longer_than_header"),

    /**
     * One or more content rows are shorter than the header row.
     * Placeholder: Content row numbers
     */
    CONTENT_ROW_SHORTER_THAN_HEADER("exception.csv.content_row_shorter_than_header"),
    ;

    private static final String PLACEHOLDER = "{}";
    private final String messageKey;

    InvalidCsvCause(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageTemplate() {
        return GlobalState.getInstance().getTranslations().getString(messageKey);
    }

    public static String getMessageWithPlaceholderValue(InvalidCsvCause causeMessage, String placeHolderValue) {
        return causeMessage.getMessageTemplate().replace(PLACEHOLDER, placeHolderValue);
    }
}
