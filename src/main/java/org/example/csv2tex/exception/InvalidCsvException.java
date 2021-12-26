package org.example.csv2tex.exception;


public class InvalidCsvException extends RuntimeException {

    private final InvalidCsvCause cause;

    public InvalidCsvException(InvalidCsvCause cause) {
        super(cause.getMessageTemplate());
        this.cause = cause;
    }

    public InvalidCsvException(InvalidCsvCause cause, String placeHolderValue) {
        super(InvalidCsvCause.getMessageWithPlaceholderValue(cause, placeHolderValue));
        this.cause = cause;
    }

    public InvalidCsvCause getErrorCode() {
        return cause;
    }
}
