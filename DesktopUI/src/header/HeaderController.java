package header;

import app.AppController;
import engine.Branch;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import header.binds.BranchNameBind;
import header.binds.IsHeadBrancheBind;
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
    MenuItem showAllBranchesButton;
    @FXML
    MenuItem newBranchButton;
    @FXML
    MenuItem checkoutButton;
    @FXML
    MenuItem resetHeadButton;
    @FXML
    Label usernameLabel;
    @FXML
    Label repositoryLabel;

    private SimpleBooleanProperty noAvailableRepository;
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
        noAvailableRepository = new SimpleBooleanProperty(Boolean.TRUE);
        branchesMenu.disableProperty().bind(noAvailableRepository);
        headBranchName = new SimpleStringProperty();
        username = new SimpleStringProperty();
        usernameLabel.textProperty().bind(username);
        username.setValue("Administrator");
        currentRepository = new SimpleStringProperty();
        repositoryLabel.textProperty().bind(currentRepository);
        currentBranchesMenus = new HashMap<>();
        URL url = getClass().getResource("/header/subComponents/textPopupWindow/textPopupWindow.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        try {
            AnchorPane popupRoot = fxmlLoader.load();
            popupWindowScene = new Scene(popupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        popupWindowController = fxmlLoader.getController();
        popupWindowController.setMainController(this);


        //setting pathContainsRepositoryScene
        URL pathContainsRepositoryUrl = getClass().getResource("/header/subComponents/pathContainsRepositoryWindow/PathContainsRepositoryWindow.fxml");
        fxmlLoader = new FXMLLoader(pathContainsRepositoryUrl);
        try {
            AnchorPane pathContainsRepositoryRoot = fxmlLoader.load();
            pathContainsRepositoryWindowScene = new Scene(pathContainsRepositoryRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pathContainsRepositoryWindowController = fxmlLoader.getController();
        pathContainsRepositoryWindowController.setMainController(this);


        //setting errorPopup Scene
        URL errorPopup = getClass().getResource("/header/subComponents/errorPopupWindow/errorPopupWindow.fxml");
        fxmlLoader = new FXMLLoader(errorPopup);
        try {
            AnchorPane errorPopupRoot = fxmlLoader.load();
            errorPopupWindowScene = new Scene(errorPopupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorPopupWindowController = fxmlLoader.getController();

        //setting new branch window Scene
        URL newBranchWindow = getClass().getResource("/header/subComponents/newBranchSelectionWindow/newBranchSelectionWindow.fxml");
        fxmlLoader = new FXMLLoader(newBranchWindow);
        try {
            AnchorPane branchSelectionRoot = fxmlLoader.load();
            newBranchSelectionWindowScene = new Scene(branchSelectionRoot);
        } catch (IOException e) {
        }
        newBranchSelectionWindowController = fxmlLoader.getController();

        //adding commit clickable menu
        ClickableMenu commitClickableMenu = new ClickableMenu("Commit");
        commitClickableMenu.setOnAction(event -> {
            Stage stage = new Stage();
            stage.setTitle("Commit Message");
            popupWindowController.setLabel("Enter message of commit:");
            stage.setScene(popupWindowScene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            try {
                mainController.Commit(popupWindowController.getText());
            } catch (Exception ex) {
                stage.setTitle("Error");
                errorPopupWindowController.SetErrorMessage(ex.getMessage());
                stage.setScene(errorPopupWindowScene);
                stage.show();
            }
        });
        topMenuBar.getMenus().add(commitClickableMenu);

        //adding WC status clickable menu
        ClickableMenu wcStatusClickableMenu = new ClickableMenu("WC Status");
        wcStatusClickableMenu.setOnAction(event -> {
            try {
                mainController.ShowStatus();
            } catch (IOException e) {
                Stage stage = new Stage();
                stage.setTitle("Error");
                errorPopupWindowController.SetErrorMessage(e.getMessage());
                stage.setScene(errorPopupWindowScene);
                stage.show();
            }
        });
        topMenuBar.getMenus().add(wcStatusClickableMenu);
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
            String repoFullPath = new String(f.getPath());
            repoFullPath += File.separator + popupWindowController.getText();

            currentRepository.setValue(repoFullPath);
            try {
                mainController.createNewRepository(currentRepository);
                noAvailableRepository.setValue(Boolean.FALSE);
                UpdateBranches();
            } catch (Exception e) {
                stage.setTitle("Error");
                errorPopupWindowController.SetErrorMessage("This repository already exists");
                stage.setScene(errorPopupWindowScene);
                stage.show();
            }
        }
    }

    @FXML
    public void switchRepositoryButtonAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository");
        File file = directoryChooser.showDialog(new Stage());
        try {
            mainController.SwitchRepository(file.getPath().toString());
            currentRepository.setValue(mainController.getRepositoryName());
            noAvailableRepository.setValue(Boolean.FALSE);
            UpdateBranches();
        } catch (Exception ex) {
            Stage stage = new Stage();
            stage.setTitle("Error");
            errorPopupWindowController.SetErrorMessage(ex.getMessage());
            stage.setScene(errorPopupWindowScene);
            stage.show();
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
            UpdateBranches();

        } catch (XmlRepositoryAlreadyExistsException ex) {
            Stage stage = new Stage();
            stage.setTitle("Xml repository already exists");
            stage.setScene(pathContainsRepositoryWindowScene);
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (XmlPathContainsNonRepositoryObjectsException e) {
            Stage stage = new Stage();
            stage.setTitle("Error");
            errorPopupWindowController.SetErrorMessage("The Path in the xml file contains files which are not repository");
            stage.setScene(errorPopupWindowScene);
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

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
            Stage stage = new Stage();
            stage.setTitle("Error");
            errorPopupWindowController.SetErrorMessage(e.getMessage());
            stage.setScene(errorPopupWindowScene);
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
    }

    public void UpdateBranches() {
        List<Branch> branches = mainController.getMagitManager().GetCurrentRepository().getBranches();
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
        IsHeadBrancheBind isHeadBranch = new IsHeadBrancheBind(branchName, headBranchName);
        Menu newMenu = new Menu();
        newMenu.textProperty().bind(new BranchNameBind(branchName, isHeadBranch));


        MenuItem delete = new MenuItem();
        delete.setText("Delete");
        delete.disableProperty().bind(isHeadBranch);
        delete.setOnAction((x) -> {
            deleteBranch(branchName);
        });

        MenuItem checkout = new MenuItem();
        checkout.setText("Checkout");
        checkout.disableProperty().bind(isHeadBranch);
        checkout.setOnAction((x) -> checkout(branchName));

        newMenu.getItems().add(checkout);
        newMenu.getItems().add(delete);
        return newMenu;
    }

    private void checkout(String branchName) {
        mainController.Checkout(branchName);
        updateHeadBranch();
    }

    private void deleteBranch(String branchName) {
        try {
            mainController.DeleteBranch(branchName);
            branchesMenu.getItems().remove(currentBranchesMenus.get(branchName));
            currentBranchesMenus.remove(branchName);
        } catch (Exception ex) {
            Stage stage = new Stage();
            stage.setTitle("Error");
            errorPopupWindowController.SetErrorMessage(ex.getMessage());
            stage.setScene(errorPopupWindowScene);
            stage.show();
        }

    }
}
