package app;

import bottom.BottomController;
import engine.MagitManager;
import header.HeaderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class AppController {
    private MagitManager magitManager;
    @FXML
    private ScrollPane headerComponent;
    @FXML
    private HeaderController headerComponentController;
    //@FXML private ScrollPane bodyComponent;
    //@FXML private BodyController bodyComponentController;
    @FXML
    AnchorPane bottomComponent;
    @FXML
    private BottomController bottomComponentController;


    @FXML
    public void initialize() {
        FXMLLoader loader = new FXMLLoader();
        URL headerUrl = getClass().getResource(MagitResourcesConstants.HEADER_FXML_PATH);
        loader.setLocation(headerUrl);
        headerComponentController.setMainController(this);

        URL bottomUrl = getClass().getResource(MagitResourcesConstants.BOTTOM_FXML_PATH);
        loader.setLocation(bottomUrl);
        bottomComponentController.setMainController(this);
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
        bottomComponentController.setMessage("Repository loaded from XML");
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

    public void SwitchRepository(String repositoryPath) throws Exception {
        magitManager.SwitchRepository(repositoryPath);
        bottomComponentController.setMessage("Switched to " + repositoryPath);
    }

    public void createNewBranch(String branchname, boolean checkout) throws Exception {
        magitManager.CreateNewBranch(branchname, checkout);
        bottomComponentController.setMessage("The branch " + branchname + " was created");
    }

    public void ShowAllBranches() {
    //get brnaches data from manager and transer it to body component?
    }

    public void DeleteBranch(String branchName) throws Exception {
        magitManager.DeleteBranch(branchName);
        bottomComponentController.setMessage("The branch" + branchName + " was deleted");
    }

    public void Commit(String message) throws Exception {
        magitManager.ExecuteCommit(message);
        bottomComponentController.setMessage("Commit was executed successfully");
    }

    public void ShowStatus() throws IOException {
        magitManager.GetStatus();
        //At right:
        // magitManager.GetStatus();
    }
}
