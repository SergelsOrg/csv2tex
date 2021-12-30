package org.example.csv2tex.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.csv2tex.globalstate.GlobalState;

import java.io.IOException;

public class Csv2TexApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Csv2TexApplication.class.getResource("csv2tex-view.fxml"));
        fxmlLoader.setResources(GlobalState.getInstance().getTranslations());

        VBox loadedParent = fxmlLoader.load();

        Scene scene = new Scene(loadedParent, 700, 300);
        stage.setTitle("Pick files for rendering!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}