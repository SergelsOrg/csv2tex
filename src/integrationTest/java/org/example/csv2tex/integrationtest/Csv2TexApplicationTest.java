package org.example.csv2tex.integrationtest;

import javafx.scene.control.*;
import org.example.csv2tex.ui.Csv2TexApplication;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class Csv2TexApplicationTest {

    private static final String RESOURCES_DIR_RELATIVE_PATH = "./src/integrationTest/resources/";
    private static final String DUMMY_CSV_FILE_NAME = "student_data_example_one_competency.csv";
    private static final String DUMMY_TEX_FILE_NAME = "valid_tex.tex";

    private Button openCsvButton;
    private Label csvFileLabel;
    private Button openTexButton;
    private Label texFileLabel;
    private Button renderPdfButton;
    private RadioButton languageSelectDe;
    private RadioButton languageSelectEn;

    // headless testing: no UI will pop up, this should work on CI
    @BeforeAll
    public static void setupForHeadlessTesting() {
        // https://circleci.com/docs/2.0/env-vars/#built-in-environment-variables
        String isRunningOnCi = System.getProperty("CI");
        if ("true".equalsIgnoreCase(isRunningOnCi)) {
            System.setProperty("monocle.platform", "Headless");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("embedded", "monocle");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");

            // System.setProperty("prism.text", "t2k");
            System.setProperty("prism.text", "native");

            System.setProperty("java.awt.headless", "true");
        }
    }

    @BeforeEach
    public void setup(FxRobot robot) throws Exception {
        ApplicationTest.launch(Csv2TexApplication.class);

        putDummyFilesIntoUserHome();
        setUiToEnglish(robot);
    }

    // FXFileChooser always has the user's home as the very first pre-selected item in the search path.
    // As I found it hard to select a different directory, I work around the issue nad put a file into the user home
    private void putDummyFilesIntoUserHome() throws IOException {
        Path userHome = Paths.get(System.getProperty("user.home"));

        File dummyCsv = new File(RESOURCES_DIR_RELATIVE_PATH + DUMMY_CSV_FILE_NAME);
        Path targetCsv = userHome.resolve(DUMMY_CSV_FILE_NAME);
        if (!targetCsv.toFile().exists()) {
            try (FileInputStream input = new FileInputStream(dummyCsv)) {
                Files.copy(input, targetCsv);
            }
        }

        File dummyTex = new File(RESOURCES_DIR_RELATIVE_PATH + DUMMY_TEX_FILE_NAME);
        Path targetTex = userHome.resolve(DUMMY_TEX_FILE_NAME);
        if (!targetTex.toFile().exists()) {
            try (FileInputStream input = new FileInputStream(dummyTex)) {
                Files.copy(input, targetTex);
            }
        }
    }

    private void setUiToEnglish(FxRobot robot) {
        lookUpUiNodes(robot);
        robot.clickOn(languageSelectDe);
        robot.clickOn(languageSelectEn);
    }

    @AfterEach
    public void closeAllAlertWindows(FxRobot robot) {
        Set<Button> buttons = robot.lookup(".button").queryAllAs(Button.class);
        Optional<Button> okButton = buttons.stream()
                .filter(b -> "OK".equals(b.getText()))
                .findFirst();
        okButton.ifPresent(robot::clickOn);
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    public void testInitialUiTexts(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);

        // assert
        assertThat(openCsvButton).hasText("Pick a CSV file!");
        assertThat(csvFileLabel).hasText("[CSV file]");
        assertThat(openTexButton).hasText("Pick a TEX file!");
        assertThat(texFileLabel).hasText("[TEX file]");
        assertThat(renderPdfButton).hasText("Render PDFs!");
    }

    @Test
    public void testThatUiValuesChangeOnLanguageSelection(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);

        // act
        robot.clickOn(languageSelectDe);

        // assert
        assertThat(openCsvButton).hasText("Wählen Sie eine CSV-Datei!");
        assertThat(openTexButton).hasText("Wählen Sie eine TEX-Datei!");
        assertThat(renderPdfButton).hasText("PDFs erstellen!");
    }

    @Test
    public void testThatUiValuesChangeBackRightAfterRendering(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);

        // act
        robot.clickOn(languageSelectDe);
        robot.clickOn(renderPdfButton);
        closeAllAlertWindows(robot);

        // assert
        assertThat(openCsvButton).hasText("Wählen Sie eine CSV-Datei!");
        assertThat(openTexButton).hasText("Wählen Sie eine TEX-Datei!");
        assertThat(renderPdfButton).hasText("PDFs erstellen!");
    }

    @Test
    public void testThatUiValuesChangeBackOnLanguageSelection(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);

        // act
        robot.clickOn(languageSelectDe);
        robot.clickOn(languageSelectEn);

        // assert
        assertThat(openCsvButton).hasText("Pick a CSV file!");
        assertThat(openTexButton).hasText("Pick a TEX file!");
        assertThat(renderPdfButton).hasText("Render PDFs!");
    }


    @Test
    public void testCsvFileSelection(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);

        // act
        robot.clickOn(openCsvButton);
        selectFirstMatchingFileInUserHome(robot);

        // assert
        assertThat(openCsvButton).hasText("Pick a CSV file!");
        assertThat(csvFileLabel).hasText(containsString(DUMMY_CSV_FILE_NAME));
        assertThat(csvFileLabel.getTooltip().getText()).contains(DUMMY_CSV_FILE_NAME);
    }

    @Test
    public void testTexFileSelection(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);

        // act
        robot.clickOn(openTexButton);
        selectFirstMatchingFileInUserHome(robot);

        // assert
        assertThat(openTexButton).hasText("Pick a TEX file!");
        assertThat(texFileLabel).hasText(containsString(DUMMY_TEX_FILE_NAME));
        assertThat(texFileLabel.getTooltip().getText()).contains(DUMMY_TEX_FILE_NAME);
    }

    @Test
    public void onRenderPdfButtonClick_onMissingCsv_showsAlert(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);
        robot.clickOn(openTexButton);
        // no CSV selected
        selectFirstMatchingFileInUserHome(robot);


        // act
        robot.clickOn(renderPdfButton);

        // assert
        // lookup Alert (it's a dialog), then do assertions
        // https://stackoverflow.com/a/59152238/1143126
        DialogPane dialogPane = assertAndLookUpAlert(robot);
        assertThat(dialogPane.getHeaderText()).isEqualTo("Error");
        assertThat(dialogPane.getContentText()).contains("does not exist");
        assertThat(dialogPane.getContentText()).contains("CSV");
    }

    @Test
    public void onRenderPdfButtonClick_onMissingTex_showsAlert(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);
        // no TEX selected
        robot.clickOn(openCsvButton);
        selectFirstMatchingFileInUserHome(robot);

        // act
        robot.clickOn(renderPdfButton);

        // assert
        // lookup Alert (it's a dialog), then do assertions
        // https://stackoverflow.com/a/59152238/1143126
        DialogPane dialogPane = assertAndLookUpAlert(robot);
        assertThat(dialogPane.getHeaderText()).isEqualTo("Error");
        assertThat(dialogPane.getContentText()).contains("does not exist");
        assertThat(dialogPane.getContentText()).contains("TEX");
    }

    @Test
    public void onRenderPdfButtonClick_withValidFiles_showsNoError(FxRobot robot) {
        // arrange
        lookUpUiNodes(robot);
        robot.clickOn(openCsvButton);
        selectFirstMatchingFileInUserHome(robot);
        robot.clickOn(openTexButton);
        selectFirstMatchingFileInUserHome(robot);

        // act
        robot.clickOn(renderPdfButton);

        // assert
        DialogPane dialogPane = assertAndLookUpAlert(robot);
        assertThat(dialogPane.getHeaderText())
                .describedAs("no alert dialog should be shown")
                .isEqualTo("Done");
    }

    private void lookUpUiNodes(FxRobot robot) {
        openCsvButton = robot.lookup("#openCsvButton").queryButton();
        csvFileLabel = robot.lookup("#csvFileLabel").queryAs(Label.class);
        openTexButton = robot.lookup("#openTexButton").queryButton();
        texFileLabel = robot.lookup("#texFileLabel").queryAs(Label.class);
        renderPdfButton = robot.lookup("#renderPdfButton").queryButton();
        languageSelectDe = robot.lookup("#languageSelectDe").queryAs(RadioButton.class);
        languageSelectEn = robot.lookup("#languageSelectEn").queryAs(RadioButton.class);
    }

    /**
     * With a FXFileChooserDialog open, select a file.
     * <p/>
     * Although FXFileChooser is already much better than plain JavaFX, I still had to work a round a lot of issues.
     * <p/>
     * Find referenced UI elements in filechooser-0.0.6.jar!/net/raumzeitfalle/fx/filechooser/FileChooserView.fxml
     */
    private void selectFirstMatchingFileInUserHome(FxRobot robot) {
        // cannot edit this directly, as it is bound
//        TextField selectedFile = robot.lookup("#selectedFile").queryAs(TextField.class);
//        selectedFile.setEditable(true);
//        selectedFile.setText(dummyCsv.getAbsolutePath());

        // cannot choose the test files directory
        // I think I need to work around this... https://stackoverflow.com/a/40476164/1143126
//        SplitMenuButton chooseDirectory = robot.lookup("#chooser").queryAs(SplitMenuButton.class);
//        robot.clickOn(chooseDirectory);
//        List<MenuItem> items = chooseDirectory.getItems();
//        MenuItem lastItem = chooseDirectory.getItems().get(items.size() - 1);

        chooseFirstInListOfFiles(robot);

        pressOkButton(robot);
    }

    private void chooseFirstInListOfFiles(FxRobot robot) {
        ListView<?> listOfFiles = robot.lookup("#listOfFiles").queryListView();
        // I cannot do this programmatically, it seems
//        listOfFiles.getSelectionModel().selectFirst();
//        listOfFiles.refresh();
//        robot.clickOn(listOfFiles);

        moveMouseToUpperLeftPlusOffset(robot, listOfFiles, 15);
        robot.clickOn(MouseButton.PRIMARY);
    }

    private void pressOkButton(FxRobot robot) {
        Set<Node> allOkButtons = robot.lookup("#okButton").queryAll();
        assertThat(allOkButtons).hasSize(1);
        Node okButton = allOkButtons.iterator().next();
        assertThat(okButton).isEnabled();
        robot.clickOn(okButton);
    }

    private void moveMouseToUpperLeftPlusOffset(FxRobot robot, Node node, int offset) {
        Point2D nodeUpperLeft = node.localToScene(0, 0);
        Scene scene = node.getScene();
        Window window = scene.getWindow();
        robot.moveTo(new Point2D(
                nodeUpperLeft.getX() + scene.getX() + window.getX() + offset,
                nodeUpperLeft.getY() + scene.getY() + window.getY() + offset
        ));
    }

    private DialogPane assertAndLookUpAlert(FxRobot robot) {
        Node dialogPaneNode = robot.lookup(".dialog-pane").query();
        assertThat(dialogPaneNode)
                .describedAs("an alert message should be shown")
                .isNotNull()
                .isInstanceOf(DialogPane.class);
        return (DialogPane) dialogPaneNode;
    }
}

