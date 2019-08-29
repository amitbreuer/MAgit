package app;

import bottom.BottomController;
import engine.*;
import header.HeaderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;

public class AppController {
    @FXML
    BorderPane borderPane;
    @FXML
    ScrollPane rightComponent;
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

    private MagitManager magitManager;

    @FXML
    public void initialize() {
        FXMLLoader loader = new FXMLLoader();
        URL headerUrl = getClass().getResource(AppResourcesConstants.HEADER_FXML_PATH);
        loader.setLocation(headerUrl);
        headerComponentController.setMainController(this);

        URL bottomUrl = getClass().getResource(AppResourcesConstants.BOTTOM_FXML_PATH);
        loader.setLocation(bottomUrl);
        bottomComponentController.setMainController(this);
    }

    public void setMagitManager(MagitManager magitManager) {
        this.magitManager = magitManager;
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

    public void DeleteBranch(String branchName) throws Exception {
        magitManager.DeleteBranch(branchName);
        bottomComponentController.setMessage("The branch " + branchName + " was deleted");
    }

    public void Commit(String message) throws Exception {
        magitManager.ExecuteCommit(message);
        bottomComponentController.setMessage("Commit was executed successfully");
    }

    public void ShowStatus() throws IOException {
        rightComponent.setContent(new Label(magitManager.GetStatus()));
    }

    public MagitManager getMagitManager() {
        return magitManager;
    }

    public void Checkout(String branchName) {
        try {
            magitManager.CheckOut(branchName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void ShowSingleCommitFilesTree(String commitSha1) throws IOException {
        Commit commit = magitManager.createCommitFromObjectFile(commitSha1);
        String repositoryName = magitManager.getRepositoryName();
        Image folderImage = new Image("/app/resources/blue-folder-icon.png");
        Image textImage = new Image("/app/resources/file-text-icon.png");

        TreeItem<String> root = new TreeItem<>(repositoryName,new ImageView(folderImage));
        root.setExpanded(Boolean.TRUE);
        createFilesTreeFromFolder(root, commit.getMainFolder(),folderImage,textImage);

        TreeView<String> tree = new TreeView<>(root);
        rightComponent.setContent(tree);

    }

    private void createFilesTreeFromFolder(TreeItem<String> root, Folder mainFolder,Image folderImage,Image textImage) {
        for (Folder.ComponentData fc : mainFolder.getComponents()) {
            TreeItem<String> newItem;
            if (fc.getFolderComponent() instanceof Folder) {
                newItem = new TreeItem<>(fc.getName(),new ImageView(folderImage));
                createFilesTreeFromFolder(newItem, (Folder) fc.getFolderComponent(),folderImage,textImage);
            } else {
                newItem = new TreeItem<>(fc.getName(),new ImageView(textImage));
            }
            root.getChildren().add(newItem);
        }
    }

    public void resetHead(String commitSha1) throws Exception {
        magitManager.ResetHeadBranch(commitSha1);
    }
}
