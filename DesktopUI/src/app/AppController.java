package app;

import app.subComponents.OpenChangesWindowController;
import body.BodyController;
import bottom.BottomController;
import engine.*;
import header.HeaderController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class AppController {
    @FXML
    BorderPane borderPane;
    @FXML
    ScrollPane rightComponent;
    @FXML
    private ScrollPane headerComponent;
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private ScrollPane bodyComponent;
    @FXML
    private BodyController bodyComponentController;
    @FXML
    AnchorPane bottomComponent;
    @FXML
    private BottomController bottomComponentController;

    private MagitManager magitManager;
    private SimpleBooleanProperty noAvailableRepository;
    private OpenChangesWindowController openChangesWindowController;
    private Scene openChangesWindowScene;

    @FXML
    public void initialize() {
        headerComponentController.setMainController(this);
        bodyComponentController.setMainController(this);
        bottomComponentController.setMainController(this);
        setOpenChangesWindow();
    //    noAvailableRepository = new SimpleBooleanProperty();
//        noAvailableRepository.bind(headerComponentController.noAvailableRepository);
//        noAvailableRepository.bind(bodyComponentController.noAvailableRepository);
    }

    private void setOpenChangesWindow() {
        URL url = getClass().getResource(AppResourcesConstants.OPEN_CHANGES_WINDOW_FXML_PATH);

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        try {
            AnchorPane popupRoot = fxmlLoader.load();
            openChangesWindowScene = new Scene(popupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        openChangesWindowController = fxmlLoader.getController();
        openChangesWindowController.setMainController(this);
    }

    public void setMagitManager(MagitManager magitManager) {
        this.magitManager = magitManager;
    }

    public void setUsername(SimpleStringProperty text) {
        magitManager.SetUsername(text.getValue());
    }

    public void createNewRepository(String repositoryPath) throws Exception {
        magitManager.CreateEmptyRepository(repositoryPath);
        bottomComponentController.setMessage("Created and Switched to " + repositoryPath);
        ShowCommitTree();
    }

    public void loadRepositoryFromXml(String absolutePath) throws Exception {
        magitManager.ValidateAndLoadXMLRepository(absolutePath);
        bottomComponentController.setMessage("Repository loaded from XML");
        ShowCommitTree();
    }

    public String getRepositoryName() {
        return magitManager.getRepositoryName();
    }

    public void replaceExistingRepositoryWithXmlRepository() {
        try {
            magitManager.createRepositoryFromMagitRepository();
            bottomComponentController.setMessage("Repository loaded from XML");
            ShowCommitTree();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SwitchRepository(String repositoryPath) throws Exception {
        magitManager.SwitchRepository(repositoryPath);
        bottomComponentController.setMessage("Switched to " + repositoryPath);
        ShowCommitTree();
    }

    public void createNewBranch(String branchName, boolean checkout) throws Exception {
        magitManager.CreateNewBranch(branchName, checkout);
        bottomComponentController.setMessage("The branch " + branchName + " was created");
        ShowCommitTree();
    }

    public void DeleteBranch(String branchName) throws Exception {
        magitManager.DeleteBranch(branchName);
        bottomComponentController.setMessage("The branch " + branchName + " was deleted");
        ShowCommitTree();
    }

    public void Commit(String message) throws Exception {
        magitManager.ExecuteCommit(message, null);
        bottomComponentController.setMessage("Commit was executed successfully");
        ShowCommitTree();
    }

    public void ShowStatus() throws IOException {
        rightComponent.setContent(new Label(magitManager.GetStatus()));
    }

    public MagitManager getMagitManager() {
        return magitManager;
    }

    public void Checkout(String branchName) throws Exception {
        if(magitManager.thereAreUncommittedChanges()){
            showOpenChangesWindow();
            if(openChangesWindowController.getCommitCheckBox().isSelected()){
                Commit(GetCommitsMessage());
            }
        }
        magitManager.CheckOut(branchName);
        ShowCommitTree();
    }

    private void showOpenChangesWindow(){
        Stage stage = new Stage();
        stage.setTitle("Open Changes");
        stage.setScene(openChangesWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void ShowSingleCommitFilesTree(String commitSha1) throws IOException {
        Commit commit = magitManager.createCommitFromObjectFile(commitSha1);
        String repositoryName = magitManager.getRepositoryName();
        Image folderImage = new Image("/app/resources/blue-folder-icon.png");
        Image textImage = new Image("/app/resources/file-text-icon.png");

        TreeItem<String> root = new TreeItem<>(repositoryName, new ImageView(folderImage));
        root.setExpanded(Boolean.TRUE);
        createFilesTreeFromFolder(root, commit.getMainFolder(), folderImage, textImage);

        TreeView<String> tree = new TreeView<>(root);
        rightComponent.setContent(tree);

    }

    private void createFilesTreeFromFolder(TreeItem<String> root, Folder mainFolder, Image folderImage, Image textImage) {
        for (Folder.ComponentData fc : mainFolder.getComponents()) {
            TreeItem<String> newItem;
            if (fc.getFolderComponent() instanceof Folder) {
                newItem = new TreeItem<>(fc.getName(), new ImageView(folderImage));
                createFilesTreeFromFolder(newItem, (Folder) fc.getFolderComponent(), folderImage, textImage);
            } else {
                newItem = new TreeItem<>(fc.getName(), new ImageView(textImage));
            }
            root.getChildren().add(newItem);
        }
    }

    public void resetHead(String commitSha1) throws Exception {
        if(magitManager.thereAreUncommittedChanges()){
            showOpenChangesWindow();
            if(openChangesWindowController.getCommitCheckBox().isSelected()){
                Commit(GetCommitsMessage());
            }
        }
        magitManager.ResetHeadBranch(commitSha1);
        ShowCommitTree();
    }

    public void ResolveConflicts(Conflicts conflicts) {

        List<ConflictComponent> conflictComponentList = conflicts.getConflictFiles();
        for (ConflictComponent cc : conflictComponentList) {
            resolveSingleConflict(cc);
        }
        //Commit();
    }

    private void resolveSingleConflict(ConflictComponent cc) {
        //creating 4 windows - checking which is null
    }

    public void Merge(String branchName) throws Exception {
        if(magitManager.thereAreUncommittedChanges()){
            throw new Exception("Merge failed. There are open changes.");
        }
        Conflicts conflics = new Conflicts();
        Folder mergedFolder = magitManager.CreateMergedFolderAndFindConflicts(branchName,conflics);
        ResolveConflicts(conflics);
        magitManager.CommitMerge(mergedFolder,GetCommitsMessage(),branchName);
        bottomComponentController.setMessage("Merge was done successfully");
        ShowCommitTree();
    }

    public Map<String, Commit> GetAllCommitsMap() { /////// check if every commit is put to the recently used list
        return magitManager.GetAllCommitsMap();
    }

    public List<Branch> GetBranches() {
        return magitManager.GetCurrentRepository().getBranches();
    }

    public String GetCommitsMessage() {
        return headerComponentController.GetCommitMessage();
    }

    public void ShowCommitTree() {
        bodyComponentController.showCommitTree();
    }

    public Branch GetHeadBranch() {
        return magitManager.GetCurrentRepository().getHeadBranch();
    }

    public void GetDeltaBetweenTwoCommits(String commit1Sha1, String commit2Sha1) {
        Delta delta;
        try {
            delta = magitManager.GetDeltaBetweenTwoCommitSha1s(commit1Sha1,commit2Sha1);
            rightComponent.setContent(new Label(delta.toString()));
        } catch (IOException e) {
            e.printStackTrace(); // error message
        }
    }
}
