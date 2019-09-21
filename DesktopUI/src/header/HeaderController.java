package header;

import app.AppController;
import engine.Branch;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import header.binds.BranchNameBind;
import header.binds.IsHeadBranchBind;
import header.subComponents.ClickableMenu;
import header.subComponents.createEmptyRepositoryWindow.CreateEmptyRepositoryWindowController;
import header.subComponents.newBranchSelectionWindow.NewBranchSelectionWindowController;
import app.subComponents.newRTBWindow.NewRTBWindowController;
import header.subComponents.textPopupWindow.TextPopupWindowController;
import header.subComponents.pathContainsRepositoryWindow.PathContainsRepositoryWindowController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HeaderController {
    @FXML
    VBox topVBOX;
    @FXML
    MenuBar topMenuBar;
    @FXML
    Menu repositoryMenu;
    @FXML
    MenuItem newRepositoryButton;
    @FXML
    MenuItem switchRepositoryButton;
    @FXML
    MenuItem updateUsernameButton;
    @FXML
    MenuItem loadViaXMLButton;
    @FXML
    Menu branchesMenu;
    @FXML
    MenuItem newBranchButton;
    @FXML
    Label usernameLabel;
    @FXML
    Label repositoryNameLabel;
    @FXML
    Label repositoryPathLabel;
    @FXML
    Label activeBranchLabel;
    @FXML
    MenuItem cloneButton;
    @FXML
    MenuItem fetchButton;
    @FXML
    MenuItem pullButton;
    @FXML
    MenuItem pushButton;

    private SimpleBooleanProperty noAvailableRepository;
    private SimpleBooleanProperty noCommitsInRepository;
    private SimpleStringProperty headBranchName;
    private SimpleStringProperty username;
    private SimpleStringProperty currentRepository;
    private SimpleStringProperty repositoryPath;
    private SimpleBooleanProperty isTrackingRemoteRepository;
    private AppController mainController;

    private TextPopupWindowController popupWindowController;
    private PathContainsRepositoryWindowController pathContainsRepositoryWindowController;
    private NewBranchSelectionWindowController newBranchSelectionWindowController;
    private CreateEmptyRepositoryWindowController createEmptyRepositoryWindowController;

    private Scene popupWindowScene;
    private Scene pathContainsRepositoryWindowScene;
    private Scene newBranchSelectionWindowScene;
    private Scene createEmptyRepositryWindowScene;

    private Map<String, Menu> currentBranchesMenus;

    @FXML
    private void initialize() {
        //init properties
        noCommitsInRepository = new SimpleBooleanProperty(Boolean.TRUE);
        noAvailableRepository = new SimpleBooleanProperty(Boolean.TRUE);
        headBranchName = new SimpleStringProperty();
        username = new SimpleStringProperty();
        currentRepository = new SimpleStringProperty();
        repositoryPath = new SimpleStringProperty();
        currentBranchesMenus = new HashMap<>();
        isTrackingRemoteRepository = new SimpleBooleanProperty(Boolean.FALSE);

        //bindings
        newBranchButton.disableProperty().bind(noCommitsInRepository);
        branchesMenu.disableProperty().bind(noAvailableRepository);
        usernameLabel.textProperty().bind(username);
        username.setValue("Administrator");
        repositoryNameLabel.textProperty().bind(currentRepository);
        repositoryPathLabel.textProperty().bind(repositoryPath);
        activeBranchLabel.textProperty().bind(headBranchName);
        fetchButton.disableProperty().bind(isTrackingRemoteRepository.not());
        pullButton.disableProperty().bind(isTrackingRemoteRepository.not());
        pushButton.disableProperty().bind(isTrackingRemoteRepository.not());

        //set relevant windows controllers and scenes
        setTextPopupWindow();
        setPathContainsRepositoryWindow();
        setNewBranchWindow();
        setCreateEmptyRepositoryWindow();

        //adding dynamic buttons
        addCommitClickableMenu();
        addWCStatusClickableMenu();
    }

    private void setCreateEmptyRepositoryWindow() {
        URL url = getClass().getResource(HeaderResourcesConstants.CREATE_NEW_REPOSITORY_WINDOW_FXML_PATH);
        FXMLLoader fxmlLoader = new FXMLLoader(url);

        try {
            AnchorPane emptyRepositoryRoot = fxmlLoader.load();
            createEmptyRepositryWindowScene = new Scene(emptyRepositoryRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        createEmptyRepositoryWindowController = fxmlLoader.getController();
        createEmptyRepositoryWindowController.setMainController(this);
    }

    private void setTextPopupWindow() {
        URL url = getClass().getResource(HeaderResourcesConstants.TEXT_POPUP_WINDOW_FXML_PATH);

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        try {
            AnchorPane popupRoot = fxmlLoader.load();
            popupWindowScene = new Scene(popupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        popupWindowController = fxmlLoader.getController();
        popupWindowController.setMainController(this);
    }

    private void setPathContainsRepositoryWindow() {
        FXMLLoader fxmlLoader;
        URL pathContainsRepositoryUrl = getClass().getResource(HeaderResourcesConstants.CONTAINS_REPOSITORY_WINDOW_FXML_PATH);
        fxmlLoader = new FXMLLoader(pathContainsRepositoryUrl);
        try {
            AnchorPane pathContainsRepositoryRoot = fxmlLoader.load();
            pathContainsRepositoryWindowScene = new Scene(pathContainsRepositoryRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pathContainsRepositoryWindowController = fxmlLoader.getController();
        pathContainsRepositoryWindowController.setMainController(this);

    }

    private void setNewBranchWindow() {
        FXMLLoader fxmlLoader;
        URL newBranchWindow = getClass().getResource(HeaderResourcesConstants.NEW_BRANCH_WINDOW_FXML_PATH);
        fxmlLoader = new FXMLLoader(newBranchWindow);
        try {
            AnchorPane branchSelectionRoot = fxmlLoader.load();
            newBranchSelectionWindowScene = new Scene(branchSelectionRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        newBranchSelectionWindowController = fxmlLoader.getController();
        newBranchSelectionWindowController.setMainController(this);
    }

    private void addWCStatusClickableMenu() {
        ClickableMenu wcStatusClickableMenu = new ClickableMenu("WC Status", null);
        wcStatusClickableMenu.disableProperty().bind(noAvailableRepository);
        wcStatusClickableMenu.setOnAction(event -> mainController.ShowWCStatus());
        topMenuBar.getMenus().add(wcStatusClickableMenu);
    }

    private void addCommitClickableMenu() {
        Image greenVImage = new Image("/resources/green-v-icon.png");
        ClickableMenu commitClickableMenu = new ClickableMenu("Commit", greenVImage);
        commitClickableMenu.disableProperty().bind(noAvailableRepository);
        commitClickableMenu.setOnAction(event -> Commit());
        topMenuBar.getMenus().add(commitClickableMenu);
    }

    //on actions
    @FXML
    public void updateUsernameButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        SetPopupWindowAndStage(stage, "Update Username", "Enter Username:");
        stage.showAndWait();

        if (popupWindowController.isActionNotCanelled()) {
            username.setValue(popupWindowController.getText());
            mainController.setUsername(username);
        }
    }

    public void SetPopupWindowAndStage(Stage stage, String stageTitle, String label) {
        stage.setTitle(stageTitle);
        popupWindowController.setLabel(label);
        popupWindowController.ClearTextField();
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(160);
        stage.setMinWidth(240);
        stage.setMaxHeight(300);
        stage.setMaxWidth(450);
    }

    @FXML
    public void newRepositoryButtonAction(ActionEvent actionEvent) {
        String repositoryPath;
        String repositoryName;
        showNewRepositoryDialog();

        if (createEmptyRepositoryWindowController.isActionNotCanelled()) {
            repositoryName = createEmptyRepositoryWindowController.getRepositoryName();
            repositoryPath = createEmptyRepositoryWindowController.getRepositoryPath();
            createNewRepository(repositoryPath, repositoryName);
            isTrackingRemoteRepository.setValue(Boolean.FALSE);
            noCommitsInRepository.setValue(Boolean.TRUE);
        }
    }

    private void showNewRepositoryDialog() {
        Stage stage = new Stage();
        stage.setTitle("Create new repository");
        stage.setScene(createEmptyRepositryWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(280);
        stage.setMinWidth(480);
        stage.setMaxHeight(350);
        stage.setMaxWidth(550);
        stage.showAndWait();
    }

    @FXML
    public void switchRepositoryButtonAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository");
        File file = directoryChooser.showDialog(new Stage());
        if (file != null) {
            mainController.SwitchRepository(file.getPath());
            currentRepository.setValue(mainController.getRepositoryName());
            repositoryPath.setValue(mainController.getRepositoryPath());
            noAvailableRepository.setValue(Boolean.FALSE);
            ClearBranchesMenu();
            UpdateBranches();
            isTrackingRemoteRepository.setValue(mainController.isTrackingRemoteRepository());
            noCommitsInRepository.setValue(!mainController.IsRepositoryConatinsCommits());
        }
    }

    public void ClearBranchesMenu() {
        for (Map.Entry<String, Menu> branchMenu : currentBranchesMenus.entrySet()) {
            String branchName = branchMenu.getKey();
            branchesMenu.getItems().remove(currentBranchesMenus.get(branchName));
        }
        currentBranchesMenus.clear();
    }

    @FXML
    public void newBranchButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("Create new branch");
        newBranchSelectionWindowController.ClearTextField();
        stage.setScene(newBranchSelectionWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(280);
        stage.setMinWidth(480);
        stage.setMaxHeight(350);
        stage.setMaxWidth(550);
        stage.showAndWait();
    }

    @FXML
    public void loadViaXMLButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        Stage fileChooserStage = new Stage();
        fileChooserStage.initModality(Modality.APPLICATION_MODAL);
        File selectedFile = fileChooser.showOpenDialog(fileChooserStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();

        mainController.loadRepositoryFromXml(absolutePath);
        noCommitsInRepository.setValue(!mainController.IsRepositoryConatinsCommits());

    }

    @FXML
    public void CreateNewBranch(String branchName, boolean checkout, boolean pointToHeadCommit, String otherCommitSha1) {
        mainController.createNewBranch(branchName, checkout, pointToHeadCommit, otherCommitSha1);
        UpdateBranches();
    }


    private void updateHeadBranch() {
        String newHeadName = mainController.getMagitManager().GetCurrentRepository().getHeadBranch().getName();
        headBranchName.setValue(newHeadName);
    }

    public void replaceExistingRepositoryWithXmlRepository() {
        mainController.replaceExistingRepositoryWithXmlRepository();
        currentRepository.setValue(mainController.getRepositoryName());
        noAvailableRepository.setValue(Boolean.FALSE);
        noCommitsInRepository.setValue(!mainController.IsRepositoryConatinsCommits());

        ClearBranchesMenu();
        UpdateBranches();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void UpdateBranches() {
        Map<String, Branch> branches = mainController.GetBranches();
        for (Map.Entry<String, Branch> entry : branches.entrySet()) {
            if (!currentBranchesMenus.containsKey(entry.getValue().getName())) {
                AddBranchToBranches(entry.getValue().getName(), entry.getValue().getIsRB());
            }
        }
        updateHeadBranch();
    }

    public void AddBranchToBranches(String branchName, boolean isRemote) {
        Menu addedBranch = createSingleBranchMenu(branchName, isRemote);
        branchesMenu.getItems().add(addedBranch);
        currentBranchesMenus.put(branchName, addedBranch);
    }

    private Menu createSingleBranchMenu(String branchName, boolean isRemote) {
        IsHeadBranchBind isHeadBranch = new IsHeadBranchBind(branchName, headBranchName);
        Menu newMenu = new Menu();
        newMenu.textProperty().bind(new BranchNameBind(branchName, isHeadBranch));

        MenuItem delete = new MenuItem();
        delete.setText("Delete");
        Image deleteImage = new Image("/resources/trash-icon.png");
        delete.setGraphic(new ImageView(deleteImage));
        delete.disableProperty().bind(isHeadBranch);
        //delete.disableProperty().setValue(isRemote);
        delete.visibleProperty().bind(isHeadBranch.not());
        delete.setOnAction((x) -> deleteBranch(branchName));

        MenuItem checkout = new MenuItem();
        checkout.setText("Checkout");
        checkout.disableProperty().bind(isHeadBranch);
        checkout.visibleProperty().bind(isHeadBranch.not());
        checkout.setOnAction(isRemote ? (x) -> createRTBForRB(branchName) : (x) -> checkout(branchName));

        MenuItem reset = new MenuItem();
        reset.setText("Reset");
        Image resetImage = new Image("/resources/undo-arrow.png");
        reset.setGraphic(new ImageView(resetImage));
        reset.disableProperty().bind(isHeadBranch.not());
        //reset.disableProperty().setValue(isRemote);
        reset.visibleProperty().bind(isHeadBranch);
        reset.setOnAction((x) -> resetHead());

        MenuItem merge = new MenuItem();
        merge.setText("Merge into Head");
        Image mergeImage = new Image("/resources/merge-icon.png");
        merge.setGraphic(new ImageView(mergeImage));
        merge.disableProperty().bind(isHeadBranch);
        //merge.disableProperty().setValue(isRemote);
        merge.visibleProperty().bind(isHeadBranch.not());
        merge.setOnAction((x) -> merge(branchName));

        newMenu.getItems().add(checkout);
        newMenu.getItems().add(delete);
        newMenu.getItems().add(reset);
        newMenu.getItems().add(merge);
        return newMenu;
    }

    private void createRTBForRB(String RBName) {
        mainController.CreateRTB(RBName);
    }

    private void resetHead() {
        Stage stage = new Stage();
        SetPopupWindowAndStage(stage, "Reset Head", "Enter sha1 of commit:");
        stage.showAndWait();

        if (popupWindowController.isActionNotCanelled()) {
            mainController.ResetHead(popupWindowController.getText());
        }
    }

    private void checkout(String branchName) {
        mainController.Checkout(branchName);
        updateHeadBranch();
    }

    private void deleteBranch(String branchName) {
        mainController.DeleteBranch(branchName);
        DeleteBranchFromBranchesMenu(branchName);
    }

    public void DeleteBranchFromBranchesMenu(String branchName) {
        branchesMenu.getItems().remove(currentBranchesMenus.get(branchName));
        currentBranchesMenus.remove(branchName);
        UpdateBranches();
    }

    private void merge(String branchName) {
        mainController.Merge(branchName);
    }

    public void Commit() {
        String message = GetCommitMessage();
        if (message != null) {
            mainController.Commit(message);
        }
    }

    public String GetCommitMessage() {
        String message = null;
        Stage stage = new Stage();
        stage.setTitle("Commit Message");
        popupWindowController.setLabel("Enter message of commit:");
        popupWindowController.ClearTextField();
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        if (popupWindowController.isActionNotCanelled()) {
            message = popupWindowController.getText();
        }
        return message;
    }

    public String GetNewBranchName() {
        String branchName = null;
        Stage stage = new Stage();
        stage.setTitle("New Branch");
        popupWindowController.setLabel("Enter name for new branch:");
        popupWindowController.ClearTextField();
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        if (popupWindowController.isActionNotCanelled()) {
            branchName = popupWindowController.getText();
        }
        return branchName;
    }

    public void createNewRepository(String repositoryPath, String repositoryName) {
        mainController.createNewRepository(repositoryPath, repositoryName);
        currentRepository.setValue(repositoryName);
        this.repositoryPath.setValue(mainController.getRepositoryPath());
        noAvailableRepository.setValue(Boolean.FALSE);
        ClearBranchesMenu();
        UpdateBranches();
    }

    public void cloneButtonAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Repository To Clone");
        File RRPath = directoryChooser.showDialog(new Stage());
        showNewRepositoryDialog();
        if (RRPath != null && createEmptyRepositoryWindowController.isActionNotCanelled()) {
            mainController.Clone(RRPath.getPath(), createEmptyRepositoryWindowController.getRepositoryPath(),
                    createEmptyRepositoryWindowController.getRepositoryName());
        }
        currentRepository.setValue(mainController.getRepositoryName());
        repositoryPath.setValue(mainController.getRepositoryPath());
        noAvailableRepository.setValue(Boolean.FALSE);
        ClearBranchesMenu();
        UpdateBranches();
    }

    public void fetchButtonAction(ActionEvent actionEvent) {
        mainController.Fetch();
        UpdateBranches();
    }

    public void pullButtonAction(ActionEvent actionEvent) {
        mainController.Pull();
    }

    public void pushButtonAction(ActionEvent actionEvent) {
        mainController.Push();
    }

    public void defaultSkinButtonAction(ActionEvent actionEvent) {
        mainController.changeToDefaultSkin();
    }

    public void lightBlueSkinButtonAction(ActionEvent actionEvent) {
        mainController.changeToLightBlueSkin();
    }

    public void lightOrangeSkinButtonAction(ActionEvent actionEvent) {
        mainController.changeToLightOrangeSkin();
    }

    public void AddListenersToCssPathProperty(SimpleStringProperty cssFilePathProperty) {
        cssFilePathProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                createEmptyRepositryWindowScene.getStylesheets().clear();
                newBranchSelectionWindowScene.getStylesheets().clear();
                pathContainsRepositoryWindowScene.getStylesheets().clear();
                popupWindowScene.getStylesheets().clear();

                if (!newValue.equals("")) {
                    String newCssFilePath = getClass().getResource(cssFilePathProperty.getValue()).toExternalForm();
                    createEmptyRepositryWindowScene.getStylesheets().add(newCssFilePath);
                    newBranchSelectionWindowScene.getStylesheets().add(newCssFilePath);
                    pathContainsRepositoryWindowScene.getStylesheets().add(newCssFilePath);
                    popupWindowScene.getStylesheets().add(newCssFilePath);
                }
            }
        });
    }

    public void ShowPathContainsRepositoryWindow() {
        Stage stage = new Stage();
        stage.setTitle("Xml repository already exists");
        stage.setScene(pathContainsRepositoryWindowScene);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(370);
        stage.setMinHeight(240);
        stage.setMaxWidth(470);
        stage.setMaxHeight(340);
        stage.showAndWait();
    }

    public void SetCurrentRepository(String repositoryName) {
        currentRepository.setValue(repositoryName);
    }

    public void SetNoAvailableRepository(Boolean aFalse) {
        noAvailableRepository.setValue(aFalse);
    }

    public void SetRepositoryPath(String repositoryPath) {
        this.repositoryPath.setValue(repositoryPath);
    }

    public void setNoCommitsInRepositoryProperty(Boolean noCommitsInRepository) {
        this.noCommitsInRepository.setValue(noCommitsInRepository);
    }
}
