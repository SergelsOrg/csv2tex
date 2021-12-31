package org.example.csv2tex.integrationtest;

import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import org.example.csv2tex.ui.Csv2TexApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class Csv2TexApplicationNoSoftwareInstalledTest {

    @BeforeAll
    public static void setupForHeadlessTesting() {
        HeadlessTestsHelper.setupForHeadlessTesting();
    }

    @BeforeEach
    public void setup(FxRobot robot) throws Exception {
        ApplicationTest.launch(Csv2TexApplication.class);
    }

    @Test
    @Tag("toolsNotInstalled")
    public void onStart_withUninstalledSoftware_showsError(FxRobot robot) {
        // directly do the assert
        DialogPane dialogPane = assertAndLookUpAlert(robot);
        assertThat(dialogPane.getHeaderText()).contains("missing software");
        assertThat(dialogPane.getContentText()).contains("install the packages");
        assertThat(dialogPane.getContentText()).contains("texlive");
        assertThat(dialogPane.getContentText()).contains("texinfo");
        assertThat(dialogPane.getContentText()).contains("pdfunite");
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

