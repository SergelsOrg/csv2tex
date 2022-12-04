module org.example.csv2tex.integrationtest {
    requires org.example.csv2tex;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires net.raumzeitfalle.fxfilechooser;
    requires commons.csv;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;

    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires org.testfx.junit5;
    requires org.testfx;

    exports org.example.csv2tex.integrationtest;
    opens org.example.csv2tex.integrationtest;
}