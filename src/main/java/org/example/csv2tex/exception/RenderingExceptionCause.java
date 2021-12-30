package org.example.csv2tex.exception;

import org.example.csv2tex.globalstate.GlobalState;

public enum RenderingExceptionCause {

    UNEXPECTED("exception.rendering.unexpected"),
    SHELL_COMMAND_FAILED("exception.rendering.shell_command_failed"),
    NO_DATA("exception.rendering.no_data"),
    ;

    private final String messageKey;

    RenderingExceptionCause(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage() {
        return GlobalState.getInstance().getTranslations().getString(messageKey);
    }
}
