package org.example.csv2tex.exception;


public class InvalidCsvException extends RuntimeException {

    public InvalidCsvException(InvalidCsvCause cause) {
        super(cause.getMessageTemplate());
    }

    public InvalidCsvException(InvalidCsvCause cause, String placeHolderValue) {
        super(InvalidCsvCause.getMessageWithPlaceholderValue(cause, placeHolderValue));
    }
}
