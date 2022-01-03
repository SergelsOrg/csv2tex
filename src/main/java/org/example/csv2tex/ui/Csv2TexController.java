package org.example.csv2tex.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Window;
import net.raumzeitfalle.fx.filechooser.FXFileChooserStage;
import net.raumzeitfalle.fx.filechooser.PathFilter;
import net.raumzeitfalle.fx.filechooser.locations.Locations;
import org.example.csv2tex.exception.RenderingException;
import org.example.csv2tex.globalstate.GlobalState;
import org.example.csv2tex.rendering.SchoolReportsRenderer;
import org.example.csv2tex.shellout.ErrorMessage;
import org.example.csv2tex.shellout.ShellCommandsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.joining;
import static net.raumzeitfalle.fx.filechooser.Skin.MODENA;

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

    // called by JAVAFX using reflection
    public void initialize() {
        LOGGER.info("Initializing controller");
        showWarningIfSoftwareIsMissing();
    }

    private void showWarningIfSoftwareIsMissing() {
        List<ErrorMessage> missingShellCommandsErrors = new ShellCommandsUtil().ensureCommandsExist();
        if (!missingShellCommandsErrors.isEmpty()) {
            String title = GlobalState.getInstance().getTranslations().getString("exception.shellcommands.missing_software");
            String message = missingShellCommandsErrors.stream()
                    .map(ErrorMessage::getMessage)
                    .collect(joining("\n"));
            LOGGER.warn(message);
            showErrorMessage(title, message);
        }
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
            chooser = FXFileChooserStage.create(MODENA, filter);
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
            ResourceBundle translations = GlobalState.getInstance().getTranslations();
            renderPdfButton.setText(translations.getString("renderPdfButtonText"));
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
        showErrorMessage("Error", text);
    }

    private void showErrorMessage(String title, String text) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(title);
        errorAlert.setContentText(text);
        errorAlert.show();
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
                GlobalState.getInstance().setLocale(Locale.ENGLISH);
                updateUiTranslations();
                break;
            case "languageSelectDe":
                GlobalState.getInstance().setLocale(Locale.GERMAN);
                updateUiTranslations();
                break;
            default:
                showErrorMessage(
                        "Unknown language: " + selectedLanguageButtonId
                );
        }
    }

    private void updateUiTranslations() {
        ResourceBundle translations = GlobalState.getInstance().getTranslations();
        // I tried my best using property bindings instead of this manual update, but did not succeed - it seems this
        // can be done easily when creating the UI in program, but it's different when using FXML, and none of the
        // information from the stuff I found worked out, e.g. https://stackoverflow.com/a/19826636/1143126
        openCsvButton.setText(translations.getString("openCsvButtonText"));
        openTexButton.setText(translations.getString("openTexButtonText"));
        renderPdfButton.setText(translations.getString("renderPdfButtonText"));
    }

}