package header;

import app.AppController;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import header.subComponents.errorPopupWindow.ErrorPopupWindowController;
import header.subComponents.textPopupWindow.TextPopupWindowController;
import header.subComponents.pathContainsRepositoryWindow.PathContainsRepositoryWindowController;
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
import javafx.beans.binding.Bindings;
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
    Label repositoryLabel;

    private SimpleStringProperty username;
    private SimpleStringProperty currentRepository;
    private AppController mainController;
    private TextPopupWindowController popupWindowController;
    private PathContainsRepositoryWindowController pathContainsRepositoryWindowController;
    private ErrorPopupWindowController errorPopupWindowController;

    private Scene popupWindowScene;

    private Scene pathContainsRepositoryWindowScene;
    private Scene errorPopupWindowScene;

    @FXML
    private void initialize() {
        username = new SimpleStringProperty();
        usernameLabel.textProperty().bind(username);
        currentRepository = new SimpleStringProperty();
        repositoryLabel.textProperty().bind(currentRepository);
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
    }


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void updateUsernameButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("Update Username");
        popupWindowController.setLabel("Enter Username:");
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        username.bind(popupWindowController.textProperty());
        mainController.setUsername(username);
        username.unbind();
    }

    @FXML
    public void newRepositoryButtonAction(ActionEvent actionEvent) throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select location for repository");
        File f = directoryChooser.showDialog(new Stage());
        if(f != null){
            Stage stage = new Stage();
            stage.setTitle("New Repository's Name:");
            popupWindowController.setLabel("Enter Name Of Repository:");
            stage.setScene(popupWindowScene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            currentRepository.bind(Bindings.concat(f.getPath(),File.separator,popupWindowController.getText()));
            mainController.createNewRepository(currentRepository);
            currentRepository.unbind();
        }
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
        currentRepository.setValue(mainController.getRepositoryName());
    }
}
