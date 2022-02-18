package org.example.csv2tex.rendering;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;


class MoveFilesForRenderingHelperTest {

    @TempDir
    private File tempDir;

    private MoveFilesForRenderingHelper sut = new MoveFilesForRenderingHelper();

    @Test
    public void moveAllTexFilesInSameFolderToTemporaryDirectory() {
        File sourceDir = new File("src/test/resources/rendering/movefilestest");

        sut.moveAllTexFilesInSameFolderToTemporaryDirectory(sourceDir, Path.of(tempDir.toURI()));

        File[] actualFiles = tempDir.listFiles();
        assertThat(actualFiles).isNotNull();
        List<String> fileNames = Arrays.stream(actualFiles).map(File::getName).collect(toList());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualFiles).hasSize(12);
            softly.assertThat(fileNames)
                    .describedAs("should not copy subdirectories")
                    .doesNotContain("unrelated_file_in_subdirectory.tex");
            softly.assertThat(fileNames)
                    .describedAs("should not copy non-image, non-tex files")
                    .doesNotContain("unrelated_file.docx")
                    .doesNotContain("unrelated_file.pdf")
                    .doesNotContain("unrelated_file.txt");
            softly.assertThat(fileNames)
                    .describedAs("should move image files")
                    .contains("file.bmp")
                    .contains("file.jpeg")
                    .contains("file.jpg")
                    .contains("file.png")
                    .contains("file.svg");
            softly.assertThat(fileNames)
                    .describedAs("should match image files case-insensitively")
                    .contains("file.BMP")
                    .contains("file.JPEG")
                    .contains("file.JPG")
                    .contains("file.PNG")
                    .contains("file.SVG");
            softly.assertThat(fileNames)
                    .describedAs("should move tex files")
                    .contains("file.tex");
            softly.assertThat(fileNames)
                    .describedAs("should match tex files case-insensitively")
                    .contains("file.TEX");
        });
    }
}