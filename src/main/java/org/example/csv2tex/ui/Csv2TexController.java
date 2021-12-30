package org.example.csv2tex.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Window;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.Skin;
import net.raumzeitfalle.fx.filechooser.locations.Locations;
import org.example.csv2tex.exception.RenderingException;
import org.example.csv2tex.rendering.SchoolReportsRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class Csv2TexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Csv2TexController.class);

    @FXML
    private Node mainLayout;

    @FXML
    private Button openCsvButton;
    @FXML
    private Button openTexButton;
    @FXML
    private Button renderPdfButton;

    @FXML
    private Label csvFileLabel;

    @FXML
    private Label texFileLabel;

    @FXML
    public ToggleGroup language;

    private File texFile;
    private File csvFile;
    private Locale selectedLanguage = Locale.ENGLISH;

    // called by JAVAFX using reflection
    public void initialize() {
        LOGGER.info("Initializing controller");
    }

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
        LOGGER.warn("An exception occurred: " + e.toString(), e);
    }

    private void logErrorMessage(Exception e) {
        LOGGER.warn("An exception occurred", e);
    }

    private void showErrorMessage(RenderingException e) {
        showErrorMessage(
                "An exception occurred - " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'"
        );
    }

    public void updateLanguage(ActionEvent actionEvent) {
        String selectedLanguageButtonId = ((Node) actionEvent.getSource()).getId();
        switch (selectedLanguageButtonId) {
            case "languageSelectEn":
                selectedLanguage = Locale.ENGLISH;
                LOGGER.info("Language changed to English");
                languageChangedTo(selectedLanguage);
                break;
            case "languageSelectDe":
                selectedLanguage = Locale.GERMAN;
                LOGGER.info("Language changed to German");
                languageChangedTo(selectedLanguage);
                break;
            default:
                showErrorMessage(
                        "Unknown language: " + selectedLanguageButtonId
                );
        }
    }

    private void languageChangedTo(Locale selectedLanguage) {

        ResourceBundle bundle = ResourceBundle.getBundle("translations", selectedLanguage);

        // I tried my best using property bindings instead of this manual update, but did not succeed - it seems this
        // can be done easily when creating the UI in program, but it's different when using FXML, and none of the
        // information from the stuff I found worked out, e.g. https://stackoverflow.com/a/19826636/1143126
        openCsvButton.setText(bundle.getString("openCsvButtonText"));
        openTexButton.setText(bundle.getString("openTexButtonText"));
        renderPdfButton.setText(bundle.getString("renderPdfButtonText"));
    }

}