package app;

import engine.MagitManager;
import header.HeaderController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class AppController {
    private MagitManager magitManager;
    @FXML private ScrollPane headerComponent;
    @FXML private HeaderController headerComponentController;
    //@FXML private ScrollPane bodyComponent;
    //@FXML private BodyController bodyComponentController;

    @FXML
    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL headerUrl = getClass().getResource(MagitResourcesConstants.HEADER_FXML_PATH);
        loader.setLocation(headerUrl);
        ScrollPane headerComponent = loader.load();
        HeaderController headerController = loader.getController();
        setHeaderComponentController(headerController);
        headerComponentController.setMainController(this);
    }

    public void setMagitManager(MagitManager magitManager) {
        this.magitManager = magitManager;
    }

    public void setHeaderComponentController(HeaderController headerComponentController) {
        this.headerComponentController = headerComponentController;
    }

    public void setUsername(String text) {
        magitManager.SetUsername(text);
    }
}
