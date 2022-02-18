package org.example.csv2tex.rendering;

import com.google.common.io.PatternFilenameFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MoveFilesForRenderingHelper {

    public void moveAllTexFilesInSameFolderToTemporaryDirectory(File sourceFolderWithRenderingFiles, Path targetFolder) {
        File[] texFiles = sourceFolderWithRenderingFiles.listFiles(new PatternFilenameFilter("(?i)^.*\\.(tex|png|jpg|jpeg|bmp|svg)$"));
        if (texFiles != null) {
            for (File fileToMove : texFiles) {
                try {
                    Files.copy(fileToMove.toPath(), targetFolder.resolve(fileToMove.getName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
