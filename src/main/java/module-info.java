module org.example.csv2tex {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.io;

    // ! Do not commit this commented-in: It causes issues with integration tests for some reason
//    requires filechooser;
//    requires commons.csv;
//    requires org.apache.commons.lang3;

    opens org.example.csv2tex.csv;
    opens org.example.csv2tex.data;
    opens org.example.csv2tex.exception;
    opens org.example.csv2tex.placeholders;
    opens org.example.csv2tex.rendering;
    opens org.example.csv2tex.shellout;
    opens org.example.csv2tex.ui;

    exports org.example.csv2tex.csv;
    exports org.example.csv2tex.data;
    exports org.example.csv2tex.exception;
    exports org.example.csv2tex.placeholders;
    exports org.example.csv2tex.rendering;
    exports org.example.csv2tex.shellout;
    exports org.example.csv2tex.ui;
}