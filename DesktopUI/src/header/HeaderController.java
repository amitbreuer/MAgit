package header;

import app.AppController;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import header.subComponents.errorPopupWindow.ErrorPopupWindowController;
import header.subComponents.popupWindow.PopupWindowController;
import header.subComponents.pathContainsRepositoryWindow.PathContainsRepositoryWindowController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
    MenuItem deleteBranchButton;
    @FXML
    MenuItem checkoutButton;
    @FXML
    MenuItem resetHeadButton;
    @FXML
    Menu commitMenu; // /files?
    @FXML
    Label usernameLabel;
    @FXML
    Label currentUsernameLabel;
    @FXML
    Label repositoryLabel;


    private SimpleStringProperty username;
    private SimpleStringProperty repositoryName;

    private AppController mainController;
    private PopupWindowController popupWindowController;
    private PathContainsRepositoryWindowController pathContainsRepositoryWindowController;
    private ErrorPopupWindowController errorPopupWindowController;

    private Scene popupWindowScene;
    private Scene pathContainsRepositoryWindowScene;
    private Scene errorPopupWindowScene;

    @FXML
    private void initialize() {
        //setting properties
        username = new SimpleStringProperty("Administrator");
        repositoryName = new SimpleStringProperty();

        //binding
        usernameLabel.textProperty().bind(username);
        repositoryLabel.textProperty().bind(repositoryName);


        //setting popupScene
        URL popupWindowUrl = getClass().getResource("/header/subComponents/popupWindow/popupWindow.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(popupWindowUrl);
        try {
            AnchorPane popupRoot = fxmlLoader.load();
            popupWindowScene = new Scene(popupRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        popupWindowController = fxmlLoader.getController();
        popupWindowController.setMainController(this);


        //setting pathContainsRepositoryScene
        URL pathContainsRepositoryUrl = getClass().getResource("/header/subComponents/PathContainsRepositoryWindow/PathContainsRepositoryWindow.fxml");
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
    }


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void updateUsernameButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("Update Username");
        stage.setScene(popupWindowScene);
        stage.show();
    }

    @FXML
    public void newRepositoryButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    public void switchRepositoryButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    public void showAllBranchesButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    public void newBranchButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    public void deleteBranchButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    public void checkoutButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    public void resetHeadButtonAction(ActionEvent actionEvent) {
    }


    public void setUsername(String text) {
        username.setValue(text);
        mainController.setUsername(text);
    }

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
            repositoryName.setValue(mainController.getRepositoryName());

        } catch (XmlRepositoryAlreadyExistsException ex) {
            Stage stage = new Stage();
            stage.setTitle("Xml repository already exists");
            stage.setScene(pathContainsRepositoryWindowScene);
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (XmlPathContainsNonRepositoryObjectsException e) {
            Stage stage = new Stage();
            stage.setTitle("Path is not repository");
            errorPopupWindowController.SetErrorMessage("Error: the Path in the xml file contains files which are not repository");
            stage.setScene(errorPopupWindowScene);
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception ex2) {

            System.out.println(ex2.getMessage());

        }
    }

    public void replaceExistingRepositoryWithXmlRepository() {
        mainController.replaceExistingRepositoryWithXmlRepository();
    }
}


