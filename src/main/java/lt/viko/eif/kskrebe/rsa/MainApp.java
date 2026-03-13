package lt.viko.eif.kskrebe.rsa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Pagrindinė JavaFX programos klasė.
 * Paleidžia langą ir užkrauna FXML vaizdą.
 */
public class MainApp extends Application {

    /**
     * JavaFX startinis metodas.
     *
     * @param stage pagrindinis programos langas
     * @throws IOException jei nepavyksta užkrauti FXML failo
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApp.class.getResource("rsa-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        stage.setTitle("RSA Encryption and Decryption System");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Programos paleidimo taškas.
     *
     * @param args komandinės eilutės argumentai
     */
    public static void main(String[] args) {
        launch(args);
    }
}