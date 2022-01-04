package org.example.csv2tex.placeholders;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NoopPlaceholderReplacerTest {
    private final NoopPlaceholderReplacer sut = new NoopPlaceholderReplacer();

    @Test
    public void loadTexTemplate() throws Exception {
        String texTemplate = "src/test/resources/shellout/page1.tex";
        String texFileContent = sut.loadTexTemplate(texTemplate);

        assertThat(texFileContent).isEqualTo("\\documentclass[11pt,a4paper]{article}\n" +
                "\n" +
                "\\begin{document}\n" +
                "page1\n" +
                "\\end{document}");
    }
}
