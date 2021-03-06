package app;

import engine.MagitManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        //load app fxml
        URL mainFXML = getClass().getResource(AppResourcesConstants.MAIN_FXML_PATH);
        loader.setLocation(mainFXML);
        BorderPane root = loader.load();

        //load controllers
        MagitManager magitManager = new MagitManager();
        AppController appController = loader.getController();
        appController.setMagitManager(magitManager);

        primaryStage.setTitle("Magit");
        Scene scene = new Scene(root);

        appController.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
