package org.example.csv2tex.exception;

public enum RenderingExceptionCause {

    UNEXPECTED("An unexpected exception occurred"),
    NO_DATA("The given CSV file contains no data"),
    ;

    private final String message;

    RenderingExceptionCause(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
