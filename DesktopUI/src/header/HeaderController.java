package header;

import app.AppController;
import header.common.PopupWindowController;
import header.subComponents.updateUsername.UpdateUsernameController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    Label currentRepositoryLabel;

    private SimpleStringProperty username;
    private SimpleStringProperty currentRepository;
    private AppController mainController;
    private PopupWindowController popupWindowController;
    private Scene popupWindowScene;


    @FXML
    private void initialize() {
        username = new SimpleStringProperty();
        usernameLabel.textProperty().bind(username);
        currentRepository = new SimpleStringProperty();
        currentRepositoryLabel.textProperty().bind(currentRepository);
        URL url = getClass().getResource("/header/common/popupWindow.fxml");
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
    public void loadRepositoryFromXmlButtonAction(ActionEvent actionEvent) {
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
}
