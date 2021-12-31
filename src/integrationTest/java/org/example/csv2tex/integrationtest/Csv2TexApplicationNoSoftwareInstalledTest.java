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

