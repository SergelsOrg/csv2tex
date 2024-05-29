module org.example.csv2tex.integrationtest {
    requires org.example.csv2tex;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires net.raumzeitfalle.fxfilechooser;
    requires javafx.swing;

    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.csv;

    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires org.testfx.junit5;
    requires org.testfx;

    exports org.example.csv2tex.integrationtest;
    opens org.example.csv2tex.integrationtest;
}