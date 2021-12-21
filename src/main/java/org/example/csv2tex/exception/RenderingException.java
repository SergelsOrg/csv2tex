package org.example.csv2tex.exception;

import static org.example.csv2tex.exception.RenderingExceptionCause.UNEXPECTED;

public class RenderingException extends RuntimeException {

    private final RenderingExceptionCause cause;

    public RenderingException(Throwable t) {
        this(UNEXPECTED, t);
    }

    public RenderingException(RenderingExceptionCause cause, Throwable t) {
        super(cause.getMessage() + " '" + t.getMessage() + "'", t);
        this.cause = cause;
    }

    public RenderingException(RenderingExceptionCause cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    public RenderingExceptionCause getErrorCode() {
        return cause;
    }
}
