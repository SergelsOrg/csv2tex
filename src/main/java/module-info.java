module org.example.csv2tex {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    // without it, CSVFormat.Builder.setHeader will not compile somehow
    requires java.sql;

    requires com.google.common;

    requires net.raumzeitfalle.fxfilechooser;
    requires javafx.swing;
    
    requires org.slf4j;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.csv;

    opens org.example.csv2tex.csv;
    opens org.example.csv2tex.data;
    opens org.example.csv2tex.exception;
    opens org.example.csv2tex.globalstate;
    opens org.example.csv2tex.placeholders;
    opens org.example.csv2tex.rendering;
    opens org.example.csv2tex.shellout;
    opens org.example.csv2tex.ui;

    exports org.example.csv2tex.csv;
    exports org.example.csv2tex.data;
    exports org.example.csv2tex.exception;
    exports org.example.csv2tex.globalstate;
    exports org.example.csv2tex.placeholders;
    exports org.example.csv2tex.rendering;
    exports org.example.csv2tex.shellout;
    exports org.example.csv2tex.ui;
}