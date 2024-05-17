package de.hsbochum.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String... args) {
        launch(args);
    }

    public static void zeigeFehlermeldungAn(String meldung) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehlermeldung");
        alert.setContentText(meldung);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("EMU-Anwendung");
        FXMLLoader loader = new FXMLLoader();
        logger.log(Level.INFO, "LOCATION: " + getClass().getResource("/BasisView.fxml"));
        loader.setLocation(getClass().getResource("/BasisView.fxml"));

        BorderPane root = loader.load();
        Scene scene = new Scene(root, 750, 490);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
