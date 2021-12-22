package org.example.csv2tex.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.Skin;
import net.raumzeitfalle.fx.filechooser.locations.Locations;
import org.example.csv2tex.exception.RenderingException;
import org.example.csv2tex.rendering.SchoolReportsRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Csv2TexController {

    private static final Logger logger = LoggerFactory.getLogger(Csv2TexController.class);

    @FXML
    private Node mainLayout;
    @FXML
    private Label csvFileLabel;
    @FXML
    private Label texFileLabel;

    private File texFile;
    private File csvFile;


    @FXML
    public void onOpenTexButtonClick(ActionEvent ignored) {
        FXFileChooserStage texChooser = getTexChooser();
        Optional<Path> selectedFile = texChooser.showOpenDialog(getWindow());
        updateTexFile(selectedFile);
    }

    private FXFileChooserStage getTexChooser() {
        return getFileChooser("TeX file", "tex", "Pick a TeX file");
    }

    private void updateTexFile(Optional<Path> tempTexFilePath) {
        tempTexFilePath.ifPresent(path -> {
            texFile = path.toFile();
            texFileLabel.setText(texFile.getAbsolutePath());
            texFileLabel.setTooltip(new Tooltip(texFile.getAbsolutePath()));
        });
    }

    @FXML
    public void onOpenCsvButtonClick(ActionEvent ignored) {
        FXFileChooserStage csvChooser = getCsvChooser();
        Optional<Path> selectedFile = csvChooser.showOpenDialog(getWindow());
        updateCsvFile(selectedFile);
    }

    private FXFileChooserStage getCsvChooser() {
        return getFileChooser("CSV file", "csv", "Pick a CSV file");
    }


    private FXFileChooserStage getFileChooser(String filterLabel, String fileExtension, String dialogTitle) {
        PathFilter filter = PathFilter.forFileExtension(filterLabel, fileExtension);
        FXFileChooserStage chooser;
        try {
            chooser = FXFileChooserStage.create(Skin.MODENA, filter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        chooser.setTitle(dialogTitle);
        chooser.addLocations(List.of(
                Locations.at(Path.of("."))
        ));
        return chooser;
    }

    private Window getWindow() {
        return mainLayout.getScene().getWindow();
    }

    private void updateCsvFile(Optional<Path> tempCsvFilePath) {
        tempCsvFilePath.ifPresent(path -> {
            csvFile = path.toFile();
            csvFileLabel.setText(csvFile.getAbsolutePath());
            csvFileLabel.setTooltip(new Tooltip(csvFile.getAbsolutePath()));
        });
    }

    @FXML
    public void onRenderPdfButtonClick(ActionEvent buttonEvent) {
        Button button = (Button) buttonEvent.getSource();
        button.setDisable(true);
        try {
            if (!validateCsvFile()) {
                return;
            }
            if (!validateTexFile()) {
                return;
            }
            button.setText("Transforming...");
            transformToPdf();
        } catch (RenderingException e) {
            logErrorMessage(e);
            showErrorMessage(e);
        } catch (Exception e) {
            logErrorMessage(e);
            showErrorMessage(
                    "An exception occurred - " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'"
            );
        } finally {
            button.setDisable(false);
            button.setText("Render PDFs!");
        }
    }


    /**
     * @return true iff existing file
     */
    private boolean validateCsvFile() {
        if (csvFile == null || !csvFile.exists()) {
            showErrorMessage("CSV file not selected or does not exist!");
            return false;
        }
        return true;
    }


    /**
     * @return true iff existing file
     */
    private boolean validateTexFile() {
        if (texFile == null || !texFile.exists()) {
            showErrorMessage("TEX file not selected or does not exist!");
            return false;
        }
        return true;
    }

    private void transformToPdf() {
        SchoolReportsRenderer renderer = new SchoolReportsRenderer();
        Path result = renderer.renderSchoolReportsForGivenFiles(csvFile, texFile);
        showInfoMessage("Done", "PDF generated: '" + result.toAbsolutePath() + "'");
    }

    private Alert showInfoMessage(String headerText, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(text);
        alert.showAndWait();
        return alert;
    }

    private void showErrorMessage(String text) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error");
        errorAlert.setContentText(text);
        errorAlert.showAndWait();
    }

    private void logErrorMessage(RenderingException e) {
        logger.warn("An exception occurred: " + e.toString(), e);
    }

    private void logErrorMessage(Exception e) {
        logger.warn("An exception occurred", e);
    }

    private void showErrorMessage(RenderingException e) {
        showErrorMessage(
                "An exception occurred - " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'"
        );
    }
}