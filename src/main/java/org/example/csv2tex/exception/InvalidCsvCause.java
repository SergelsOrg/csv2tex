package org.example.csv2tex.exception;

public enum InvalidCsvCause {

    /**
     * CSV has more than just base data, but no level column is defined.
     */
    HEADER_NO_LEVEL_DEFINED("The header row defines no level column."),

    /**
     * The header row is shorter than all content rows.
     */
    HEADER_SHORTER_THAN_ALL_CONTENT("The header row is shorter than all content rows."),

    /**
     * The header row is longer than all content rows.
     */
    HEADER_LONGER_THAN_ALL_CONTENT("The header row is longer than all content rows."),

    /**
     * The header row is shorter than most content rows (heuristic: at least 10 or at least 20%).
     * Placeholder: Content row numbers
     */
    HEADER_SHORTER_THAN_CONTENT("The header row is shorter than many content rows: Content row(s) {}."),

    /**
     * The header row is longer than most content rows (heuristic: at least 10 or at least 20%).
     * Placeholder: Content row numbers
     */
    HEADER_LONGER_THAN_CONTENT("The header row is longer than many content rows: Content row(s) {}."),

    /**
     * One or more content rows are longer than the header row.
     * Placeholder: Content row numbers
     */
    CONTENT_ROW_LONGER_THAN_HEADER("One or more content rows are longer than the header row: Content row(s) {}."),
    /**
     * One or more content rows are shorter than the header row.
     * Placeholder: Content row numbers
     */
    CONTENT_ROW_SHORTER_THAN_HEADER("One or more content rows are shorter than the header row: Content row(s) {}."),
    ;

    private final String messageTemplate;

    InvalidCsvCause(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public static String getMessageWithPlaceholderValue(InvalidCsvCause causeMessage, String placeHolderValue) {
        return causeMessage.messageTemplate.replace("{}", placeHolderValue);
    }
}
