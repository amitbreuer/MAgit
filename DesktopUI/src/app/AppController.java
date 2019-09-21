package app;

import app.subComponents.conflictsWindow.ConflictsWindowController;
import app.subComponents.newRTBWindow.NewRTBWindowController;
import app.subComponents.openChangesWindow.OpenChangesWindowController;
import app.subComponents.singleConflictWindow.SingleConflictController;
import app.subComponents.errorPopupWindow.ErrorPopupWindowController;
import body.BodyController;
import engine.*;
import exceptions.ActiveBranchContainsMergedBranchException;
import exceptions.ThereISRBWithTheSpecifiedNameException;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import header.HeaderController;
import javafx.application.Platform;
import header.HeaderResourcesConstants;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import left.LeftController;
import right.RightController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Consumer;

public class AppController {
    @FXML
    BorderPane borderPane;
    @FXML
    private ScrollPane headerComponent;
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private ScrollPane bodyComponent;
    @FXML
    private BodyController bodyComponentController;
//    @FXML
//    AnchorPane bottomComponent;
//    @FXML
//    private BottomController bottomComponentController;
    @FXML
    private SplitPane leftComponent;
    @FXML
    private LeftController leftComponentController;
    @FXML
    private SplitPane rightComponent;
    @FXML
    private RightController rightComponentController;

    private MagitManager magitManager;
    private SimpleBooleanProperty noAvailableRepository;
    private Scene openChangesWindowScene;
    private Scene errorPopupWindowScene;
    private Scene conflictsWindowScene;
    private Scene newRTBWindowScene;
    private NewRTBWindowController newRTBWindowController;
    private OpenChangesWindowController openChangesWindowController;
    private ErrorPopupWindowController errorPopupWindowController;
    private ConflictsWindowController conflictsWindowController;
    private SimpleStringProperty cssFilePathProperty;
    private Stage primaryStage;


    @FXML
    public void initialize() {
        cssFilePathProperty = new SimpleStringProperty();
        headerComponentController.setMainController(this);
        bodyComponentController.setMainController(this);
        //bottomComponentController.setMainController(this);
        leftComponentController.setMainController(this);
        rightComponentController.setMainController(this);
        setOpenChangesWindow();
        setErrorPopupWindow();
        setConflictsWindow();
        setNewRTBWindow();
        //    noAvailableRepository = new SimpleBooleanProperty();
//        noAvailableRepository.bind(headerComponentController.noAvailableRepository);
//        noAvailableRepository.bind(bodyComponentController.noAvailableRepository);
        //    noAvailableRepository = new SimpleBooleanProperty();
        //    noAvailableRepository.bind(headerComponentController.noAvailableRepository);
        //    noAvailableRepository.bind(bodyComponentController.noAvailableRepository);
        headerComponentController.AddListenersToCssPathProperty(cssFilePathProperty);
        AddListenersToCssPathProperty(cssFilePathProperty);

    }

    private void AddListenersToCssPathProperty(SimpleStringProperty cssFilePathProperty) {
        cssFilePathProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                openChangesWindowScene.getStylesheets().clear();
                errorPopupWindowScene.getStylesheets().clear();
                conflictsWindowScene.getStylesheets().clear();

                if (!newValue.equals("")) {
                    String newCssFilePath = getClass().getResource(cssFilePathProperty.getValue()).toExternalForm();
                    openChangesWindowScene.getStylesheets().add(newCssFilePath);
                    errorPopupWindowScene.getStylesheets().add(newCssFilePath);
                    conflictsWindowScene.getStylesheets().add(newCssFilePath);
                }
            }
        });
    }

    public void changeToDefaultSkin() {
        cssFilePathProperty.setValue("");
        primaryStage.getScene().getStylesheets().clear();
    }

    public void changeToLightBlueSkin() {
        cssFilePathProperty.setValue("/resources/css/lightBlueSkin.css");
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource(cssFilePathProperty.getValue()).toExternalForm());
    }

    public void changeToLightOrangeSkin() {
        cssFilePathProperty.setValue("/resources/css/lightOrangeSkin.css");
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource(cssFilePathProperty.getValue()).toExternalForm());
    }


    private void setConflictsWindow() {
        URL url = getClass().getResource(AppResourcesConstants.CONFLICTS_WINDOW_FXML_PATH);

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        try {
            GridPane popupRoot = fxmlLoader.load();
            conflictsWindowScene = new Scene(popupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        conflictsWindowController = fxmlLoader.getController();
        conflictsWindowController.setMainController(this);
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

    private void setErrorPopupWindow() {
        FXMLLoader fxmlLoader;
        URL errorPopup = getClass().getResource(AppResourcesConstants.ERROR_POPUP_WINDOW_FXML_PATH);
        fxmlLoader = new FXMLLoader(errorPopup);
        try {
            AnchorPane errorPopupRoot = fxmlLoader.load();
            errorPopupWindowScene = new Scene(errorPopupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorPopupWindowController = fxmlLoader.getController();

    }

    private void setNewRTBWindow() {
        FXMLLoader fxmlLoader;
        URL newRTBWindow = getClass().getResource(AppResourcesConstants.NEW_RTB_WINDOW_FXML_PATH);
        fxmlLoader = new FXMLLoader(newRTBWindow);
        try {
            AnchorPane newRTBRoot = fxmlLoader.load();
            newRTBWindowScene = new Scene(newRTBRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        newRTBWindowController = fxmlLoader.getController();
    }

    public void setMagitManager(MagitManager magitManager) {
        this.magitManager = magitManager;
    }

    public MagitManager getMagitManager() {
        return magitManager;
    }

    public void setUsername(SimpleStringProperty text) {
        magitManager.SetUsername(text.getValue());
    }

    public void createNewRepository(String repositoryPath, String repositoryName) {
        try {
            magitManager.CreateEmptyRepository(repositoryPath, repositoryName);
            clearDisplay();
            //bottomComponentController.setMessage("Created and Switched to " + repositoryPath);
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void loadRepositoryFromXml(String absolutePath) {

        Consumer<String> errorConsumer = (str) -> showErrorWindow(str);

        Runnable runIfPathContainsRepository = () -> headerComponentController.ShowPathContainsRepositoryWindow();

        Runnable runIfFinishedProperly = () -> {
            headerComponentController.SetCurrentRepository(getRepositoryName());
            headerComponentController.SetNoAvailableRepository(Boolean.FALSE);
            headerComponentController.SetRepositoryPath(getRepositoryPath());
            headerComponentController.ClearBranchesMenu();
            headerComponentController.UpdateBranches();

            ShowWCStatus();
            showCommitTree();

        };

        magitManager.LoadRepositoryFromXML(absolutePath, errorConsumer, runIfPathContainsRepository, runIfFinishedProperly);

        //bottomComponentController.setMessage("Repository loaded from XML");

    }

    public String getRepositoryName() {
        return magitManager.getRepositoryName();
    }

    public String getRepositoryPath() {
        return magitManager.getRepositoryPath();
    }

    public void replaceExistingRepositoryWithXmlRepository() {
        try {
            magitManager.createRepositoryFromMagitRepository();
            ShowWCStatus();
            showCommitTree();
            //bottomComponentController.setMessage("Repository loaded from XML");
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void SwitchRepository(String repositoryPath) {
        try {
            magitManager.SwitchRepository(repositoryPath);
            rightComponentController.Clear();
            ShowWCStatus();
            showCommitTree();
            //bottomComponentController.setMessage("Switched to " + repositoryPath);
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void createNewBranch(String branchName, boolean checkout, boolean pointToHeadCommit, String otherCommitSha1) {
        try {
            magitManager.CreateNewBranch(branchName, checkout, pointToHeadCommit, otherCommitSha1);
            showCommitTree();
            //bottomComponentController.setMessage("The branch " + branchName + " was created");
        } catch (ThereISRBWithTheSpecifiedNameException e) {
            CreateRTB(branchName);
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void DeleteBranch(String branchName) {
        try {
            magitManager.DeleteBranch(branchName);
            showCommitTree();
            //bottomComponentController.setMessage("The branch " + branchName + " was deleted");
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void Commit(String message) {
        try {
            magitManager.ExecuteCommit(message, null);
            ShowWCStatus();
            showCommitTree();
            //bottomComponentController.setMessage("Commit was executed successfully");
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void ShowWCStatus() {
        try {
            leftComponentController.ShowWCStatus(magitManager.GetWCDelta());
        } catch (IOException e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void Checkout(String branchName) {
        try {
            if (magitManager.thereAreUncommittedChanges()) {
                showOpenChangesWindow();
                if (openChangesWindowController.getCommitCheckBox().isSelected()) {
                    String message = GetCommitsMessage();
                    if (message == null) {
                        return;
                    } else {
                        Commit(message);
                    }
                }
            }
            magitManager.CheckOut(branchName);
            showCommitTree();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    private void showOpenChangesWindow() {
        Stage stage = new Stage();
        stage.setTitle("Open Changes");
        stage.setScene(openChangesWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void ShowSingleCommitFilesTree(String commitSha1) {
        Commit commit = magitManager.CreateCommitFromSha1(commitSha1, magitManager.GetObjectsDirPath());
        String repositoryName = magitManager.getRepositoryName();
        rightComponentController.ShowSingleCommitFilesTree(commit, repositoryName);
    }

    public void ResetHead(String commitSha1) {
        try {
            if (magitManager.thereAreUncommittedChanges()) {
                showOpenChangesWindow();
                if (openChangesWindowController.getCommitCheckBox().isSelected()) {
                    String message = GetCommitsMessage();
                    if (message == null) {
                        return;
                    } else {
                        Commit(message);
                    }
                }
            }
            magitManager.ResetHeadBranch(commitSha1);
            ShowWCStatus();
            showCommitTree();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public Map<String, Commit> GetAllCommitsMap() {
        return magitManager.GetAllCommitsMap();
    }

    public Map<String, Branch> GetBranches() {
        return magitManager.GetCurrentRepository().getBranches();
    }

    private String GetCommitsMessage() {
        return headerComponentController.GetCommitMessage();
    }

    private void showCommitTree() {
        bodyComponentController.showCommitTree();
    }

    public Branch GetHeadBranch() {
        return magitManager.GetCurrentRepository().getHeadBranch();
    }

    public void GetDeltaBetweenTwoCommits(String commit1Sha1, String commit2Sha1) {
        Delta delta;
        try {
            delta = magitManager.GetDeltaBetweenTwoCommitSha1s(commit1Sha1, commit2Sha1);
            leftComponentController.ShowDeltaBetweenTwoCommits(delta);
        } catch (IOException e) {
            e.printStackTrace(); // error message
        }
    }

    public void ShowFileContent(String fileName, String fileContent) {
        leftComponentController.ShowFileContent(fileName, fileContent);
    }

    public void ShowCommitInfo(String commitSha1) {
        Commit commit = magitManager.CreateCommitFromSha1(commitSha1, magitManager.GetObjectsDirPath());
        rightComponentController.ShowCommitInfo(commit.toString());
    }

    public void showErrorWindow(String errorMessage) {
        Stage stage = new Stage();
        stage.setTitle("Error");
        errorPopupWindowController.SetErrorMessage(errorMessage);
        stage.setScene(errorPopupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
    }

    private void clearDisplay() {
        rightComponentController.Clear();
        leftComponentController.Clear();
        bodyComponentController.Clear();
    }

    public void CreateNewBranchForCommit(String commitSha1) {
        String branchName = headerComponentController.GetNewBranchName();
        if (branchName != null) {
            try {
                magitManager.CreateNewBranchForCommit(branchName, commitSha1);
                //headerComponentController.AddBranchToBranches(branchName);
                headerComponentController.UpdateBranches();
                showCommitTree();

            } catch (Exception e) {
                showErrorWindow(e.getMessage());
            }
        }
    }

    public void DeleteBranchFromCommit(String branchName) {
        DeleteBranch(branchName);
        headerComponentController.DeleteBranchFromBranchesMenu(branchName);
    }

    private void ResolveConflicts(Conflicts conflicts) {
        conflictsWindowController.SetConflictsList(conflicts.getConflictFiles());
        Stage stage = new Stage();
        stage.setTitle("Conflicts");
        stage.setScene(conflictsWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(240);
        stage.setMinWidth(261);
        stage.setMaxHeight(320);
        stage.setMaxWidth(261);
        stage.showAndWait();

        magitManager.ImplementConflictsSolutions(conflicts);
    }

    public void Merge(String branchName) {
        try {
            if (magitManager.thereAreUncommittedChanges()) {
                showErrorWindow("Merge failed. There are open changes.");
            }
            Conflicts conflicts = new Conflicts();
            Folder mergedFolder = magitManager.CreateMergedFolderAndFindConflicts(branchName, conflicts);
            if (!conflicts.getConflictFiles().isEmpty()) {
                ResolveConflicts(conflicts);
            }
            magitManager.CommitMerge(mergedFolder, GetCommitsMessage(), branchName);
            ShowWCStatus();
            showCommitTree();
            //bottomComponentController.setMessage("Merge was done successfully");
        } catch (ActiveBranchContainsMergedBranchException e) {
            Stage stage = new Stage();
            stage.setTitle("No Merge Was Done");
            errorPopupWindowController.SetErrorMessage(magitManager.GetHeadBranchName() +
                    " contains all " + branchName + "'s updates");
            stage.setScene(errorPopupWindowScene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
            e.printStackTrace();
        }
    }

    public void Clone(String RRPath, String LRPath, String LRName) {
        magitManager.CloneRepository(RRPath, LRPath, LRName);
        ShowWCStatus();
        showCommitTree();
    }

    public void Fetch() {
        magitManager.Fetch();
        ShowWCStatus();
        showCommitTree();
    }

    public Boolean isTrackingRemoteRepository() {
        return magitManager.isTrackingRemoteRepository();
    }

    public void Pull() {
        try {
            if (magitManager.thereAreUncommittedChanges()) {
                showOpenChangesWindow();
                if (openChangesWindowController.getCommitCheckBox().isSelected()) {
                    String message = GetCommitsMessage();
                    if (message == null) {
                        return;
                    } else {
                        Commit(message);
                    }
                }
            }
            magitManager.Pull();
            ShowWCStatus();
            showCommitTree();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void Push() {
        try {
            magitManager.Push();
            ShowWCStatus();
            showCommitTree();
        } catch (Exception e) {
            showErrorWindow(e.getMessage());
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void CreateRTB(String branchName) {
        Stage stage = new Stage();
        stage.setTitle("Create New RTB");
        StringTokenizer tokenizer = new StringTokenizer(branchName, "\\");
        tokenizer.nextToken();
        String RTBName = tokenizer.nextToken();
        newRTBWindowController.setBranchNameLabel(RTBName);
        stage.setScene(newRTBWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        if (newRTBWindowController.CreateRTBAndCheckout()) {
            try {
                magitManager.CreateRTBForRB(RTBName,true);
            } catch (Exception e) {
                showErrorWindow(e.getMessage());
            }
        }
    }
}
