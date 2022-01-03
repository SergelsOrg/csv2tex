package org.example.csv2tex.integrationtest;

/**
 * Headless testing: No UI will pop up, this is what we need for the tests to work on CI
 */
public class HeadlessTestsHelper {
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
}
