package org.example.csv2tex.shellout;

import org.example.csv2tex.globalstate.GlobalState;

public enum ErrorMessage {
    // pdfunite not installed
    PDF_UNITE_NOT_INSTALLED("exception.shellcommands.missing_software.pdfunite"),

    // texi2pdf and / or tex not installed
    TEX_LIVE_NOT_INSTALLED("exception.shellcommands.missing_software.tex"),

    // tex packages not installed
    TEX_PACKAGES_NOT_INSTALLED("exception.shellcommands.missing_software.tex_packages"),
    ;

    private final String messageKey;

    ErrorMessage(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage() {
        return GlobalState.getInstance().getTranslations().getString(messageKey);
    }

}
