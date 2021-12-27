package org.example.csv2tex.exception;

import static org.example.csv2tex.exception.RenderingExceptionCause.UNEXPECTED;

public class RenderingException extends RuntimeException {

    private final RenderingExceptionCause cause;
    private final String additionalMessage;

    public RenderingException(Throwable t) {
        this(UNEXPECTED, t);
    }

    public RenderingException(RenderingExceptionCause cause, Throwable t) {
        super(cause.getMessage() + " '" + t.getMessage() + "'", t);
        this.cause = cause;
        this.additionalMessage = "";
    }

    public RenderingException(RenderingExceptionCause cause, String additionalMessage) {
        super(cause.getMessage());
        this.cause = cause;
        this.additionalMessage = additionalMessage;
    }

    public RenderingException(RenderingExceptionCause cause) {
        super(cause.getMessage());
        this.cause = cause;
        this.additionalMessage = "";
    }

    public RenderingExceptionCause getErrorCode() {
        return cause;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }

    @Override
    public String toString() {
        return "RenderingException{" +
                "cause=" + cause +
                ", additionalMessage='" + additionalMessage + '\'' +
                '}';
    }
}
