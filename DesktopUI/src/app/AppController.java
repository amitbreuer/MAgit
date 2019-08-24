package app;

import engine.MagitManager;
import header.HeaderController;
import javafx.beans.property.SimpleStringProperty;
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
    public void initialize() {
        FXMLLoader loader = new FXMLLoader();
        URL headerUrl = getClass().getResource(MagitResourcesConstants.HEADER_FXML_PATH);
        loader.setLocation(headerUrl);
        headerComponentController.setMainController(this);
    }

    public void setMagitManager(MagitManager magitManager) {
        this.magitManager = magitManager;
    }

    public void setHeaderComponentController(HeaderController headerComponentController) {
        this.headerComponentController = headerComponentController;
    }

    public void setUsername(SimpleStringProperty text) {
        magitManager.SetUsername(text.getValue());
    }

    public void createNewRepository(SimpleStringProperty currentRepository) throws Exception {
        magitManager.CreateEmptyRepository(currentRepository.getValue());
    }

    public void loadRepositoryFromXml(String absolutePath) throws Exception {
            magitManager.ValidateAndLoadXMLRepository(absolutePath);
    }

    public String getRepositoryName() {
        return magitManager.getRepositoryName();
    }

    public void replaceExistingRepositoryWithXmlRepository() {
        try {
            magitManager.createRepositoryFromMagitRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
