package header;

import app.AppController;
import engine.Branch;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import header.binds.BranchNameBind;
import header.binds.IsHeadBranchBind;
import header.subComponents.ClickableMenu;
import header.subComponents.errorPopupWindow.ErrorPopupWindowController;
import header.subComponents.newBranchSelectionWindow.NewBranchSelectionWindowController;
import header.subComponents.textPopupWindow.TextPopupWindowController;
import header.subComponents.pathContainsRepositoryWindow.PathContainsRepositoryWindowController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.List;
import java.util.Map;

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
    Label repositoryLabel;

    public SimpleBooleanProperty noAvailableRepository;
    private SimpleStringProperty headBranchName;
    private SimpleStringProperty username;
    private SimpleStringProperty currentRepository;
    private AppController mainController;
    private TextPopupWindowController popupWindowController;
    private PathContainsRepositoryWindowController pathContainsRepositoryWindowController;
    private ErrorPopupWindowController errorPopupWindowController;
    private NewBranchSelectionWindowController newBranchSelectionWindowController;

    private Scene popupWindowScene;
    private Scene pathContainsRepositoryWindowScene;
    private Scene errorPopupWindowScene;
    private Scene newBranchSelectionWindowScene;
    private Map<String, Menu> currentBranchesMenus;

    @FXML
    private void initialize() {
        //init properties
        noAvailableRepository = new SimpleBooleanProperty(Boolean.TRUE);
        headBranchName = new SimpleStringProperty();
        username = new SimpleStringProperty();
        currentRepository = new SimpleStringProperty();
        currentBranchesMenus = new HashMap<>();

        //bindings
        branchesMenu.disableProperty().bind(noAvailableRepository);
        usernameLabel.textProperty().bind(username);
        username.setValue("Administrator");
        repositoryLabel.textProperty().bind(currentRepository);

        //set relevant windows controllers and scenes
        setTextPopupWindow();
        setPathContainsRepositoryWindow();
        setErrorPopupWindow();
        setNewBranchWindow();

        //adding dynamic buttons
        addCommitClickableMenu();
        addWCStatusClickableMenu();
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

    private void setErrorPopupWindow() {
        FXMLLoader fxmlLoader;
        URL errorPopup = getClass().getResource(HeaderResourcesConstants.ERROR_POPUP_WINDOW_FXML_PATH);
        fxmlLoader = new FXMLLoader(errorPopup);
        try {
            AnchorPane errorPopupRoot = fxmlLoader.load();
            errorPopupWindowScene = new Scene(errorPopupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorPopupWindowController = fxmlLoader.getController();

    }

    private void setNewBranchWindow() {
        FXMLLoader fxmlLoader;
        URL newBranchWindow = getClass().getResource(HeaderResourcesConstants.NEW_BRANCH_WINDOW_FXML_PATH);
        fxmlLoader = new FXMLLoader(newBranchWindow);
        try {
            AnchorPane branchSelectionRoot = fxmlLoader.load();
            newBranchSelectionWindowScene = new Scene(branchSelectionRoot);
        } catch (IOException e) {
        }
        newBranchSelectionWindowController = fxmlLoader.getController();
        newBranchSelectionWindowController.setMainController(this);
    }

    private void addWCStatusClickableMenu() {
        ClickableMenu wcStatusClickableMenu = new ClickableMenu("WC Status");
        wcStatusClickableMenu.disableProperty().bind(noAvailableRepository);
        wcStatusClickableMenu.setOnAction(event -> {
            try {
                mainController.ShowStatus();
            } catch (IOException e) {
                ShowErrorWindow(e.getMessage());
            }
        });
        topMenuBar.getMenus().add(wcStatusClickableMenu);
    }

    private void addCommitClickableMenu() {
        ClickableMenu commitClickableMenu = new ClickableMenu("Commit");
        commitClickableMenu.disableProperty().bind(noAvailableRepository);
        commitClickableMenu.setOnAction(event -> Commit());
        topMenuBar.getMenus().add(commitClickableMenu);
    }

    //on actions
    @FXML
    public void updateUsernameButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("Update Username");
        popupWindowController.setLabel("Enter Username:");
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        username.setValue(popupWindowController.textProperty().getValue());
        mainController.setUsername(username);
    }

    @FXML
    public void newRepositoryButtonAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select location for repository");
        File f = directoryChooser.showDialog(new Stage());
        if (f != null) {
            Stage stage = new Stage();
            stage.setTitle("New Repository's Name:");
            popupWindowController.setLabel("Enter name of repository:");
            stage.setScene(popupWindowScene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            String repoFullPath = f.getPath();
            repoFullPath += popupWindowController.getText();
            currentRepository.setValue(repoFullPath);
            try {
                mainController.createNewRepository(currentRepository.getValue());
                noAvailableRepository.setValue(Boolean.FALSE);
                UpdateBranches();
            } catch (Exception e) {
                ShowErrorWindow("This repository already exists");
            }
        }
    }

    @FXML
    public void switchRepositoryButtonAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository");
        File file = directoryChooser.showDialog(new Stage());
        try {
            mainController.SwitchRepository(file.getPath());
            currentRepository.setValue(mainController.getRepositoryName());
            noAvailableRepository.setValue(Boolean.FALSE);
            UpdateBranches();
        } catch (Exception e) {
            ShowErrorWindow(e.getMessage());
        }

    }

    @FXML
    public void newBranchButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("Create new branch");
        stage.setScene(newBranchSelectionWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
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
        try {
            mainController.loadRepositoryFromXml(absolutePath);
            currentRepository.setValue(mainController.getRepositoryName());
            noAvailableRepository.setValue(Boolean.FALSE);
            //UpdateBranches();
        } catch (XmlRepositoryAlreadyExistsException ex) {
            Stage stage = new Stage();
            stage.setTitle("Xml repository already exists");
            stage.setScene(pathContainsRepositoryWindowScene);
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (XmlPathContainsNonRepositoryObjectsException e) {
            ShowErrorWindow("The Path in the xml file contains files which are not repository");
            return;
        } catch (Exception ex2) {
        }

        UpdateBranches();
    }

    //other Methods
    private void updateHeadBranch() {
        String newHeadName = mainController.getMagitManager().GetCurrentRepository().getHeadBranch().getName();
        headBranchName.setValue(newHeadName);
    }

    public void replaceExistingRepositoryWithXmlRepository() {
        mainController.replaceExistingRepositoryWithXmlRepository();
        currentRepository.setValue(mainController.getRepositoryName());
        noAvailableRepository.setValue(Boolean.FALSE);
        UpdateBranches();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void CreateNewBranch(String branchName, boolean checkout) {
        try {
            mainController.createNewBranch(branchName, checkout);
            addBranchToBranches(branchName);
        } catch (Exception e) {
            ShowErrorWindow(e.getMessage());
        }
        UpdateBranches();
    }

    public void UpdateBranches() {
        List<Branch> branches = mainController.GetBranches();
        for (Branch br : branches) {
            if (!currentBranchesMenus.containsKey(br.getName())) {
                addBranchToBranches(br.getName());
            }
        }
        updateHeadBranch();

    }

    private void addBranchToBranches(String branchName) {
        Menu addedBranch = createSingleBranchMenu(branchName);
        branchesMenu.getItems().add(addedBranch);
        currentBranchesMenus.put(branchName, addedBranch);
    }

    private Menu createSingleBranchMenu(String branchName) {
        IsHeadBranchBind isHeadBranch = new IsHeadBranchBind(branchName, headBranchName);
        Menu newMenu = new Menu();
        newMenu.textProperty().bind(new BranchNameBind(branchName, isHeadBranch));


        MenuItem delete = new MenuItem();
        delete.setText("Delete");
        delete.disableProperty().bind(isHeadBranch);
        delete.visibleProperty().bind(isHeadBranch.not());
        delete.setOnAction((x) -> deleteBranch(branchName));

        MenuItem checkout = new MenuItem();
        checkout.setText("Checkout");
        checkout.disableProperty().bind(isHeadBranch);
        checkout.visibleProperty().bind(isHeadBranch.not());
        checkout.setOnAction((x) -> checkout(branchName));

        MenuItem reset = new MenuItem();
        reset.setText("Reset");
        reset.disableProperty().bind(isHeadBranch.not());
        reset.visibleProperty().bind(isHeadBranch);
        reset.setOnAction((x) -> resetHead());

        MenuItem merge = new MenuItem();
        merge.setText("Merge into Head");
        merge.disableProperty().bind(isHeadBranch);
        merge.visibleProperty().bind(isHeadBranch.not());
        merge.setOnAction((x) -> merge(branchName));

        newMenu.getItems().add(checkout);
        newMenu.getItems().add(delete);
        newMenu.getItems().add(reset);
        newMenu.getItems().add(merge);
        return newMenu;
    }

    private void resetHead() {
        Stage stage = new Stage();
        stage.setTitle("Reset Head");
        popupWindowController.setLabel("Enter sha1 of commit:");
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        try {
            mainController.resetHead(popupWindowController.getText());
        } catch (Exception e) {
            ShowErrorWindow(e.getMessage());
        }
    }

    private void checkout(String branchName) {
        try {
            mainController.Checkout(branchName);
        } catch (Exception e) {
            ShowErrorWindow(e.getMessage());
        }
        updateHeadBranch();
    }

    private void deleteBranch(String branchName) {
        try {
            mainController.DeleteBranch(branchName);
            branchesMenu.getItems().remove(currentBranchesMenus.get(branchName));
            currentBranchesMenus.remove(branchName);
        } catch (Exception e) {
            ShowErrorWindow(e.getMessage());
        }
    }

    private void merge(String branchName){ ////////////////////
        try {
            mainController.Merge(branchName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Commit() {
        try {
            mainController.Commit(GetCommitMessage());
        } catch (Exception e) {
            ShowErrorWindow(e.getMessage());
        }
    }

    public void ShowErrorWindow(String errorMessage){
        Stage stage = new Stage();
        stage.setTitle("Error");
        errorPopupWindowController.SetErrorMessage(errorMessage);
        stage.setScene(errorPopupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public String GetCommitMessage() {
        Stage stage = new Stage();
        stage.setTitle("Commit Message");
        popupWindowController.setLabel("Enter message of commit:");
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return popupWindowController.getText();
    }
}