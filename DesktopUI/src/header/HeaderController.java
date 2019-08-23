package header;

import app.AppController;
import header.common.PopupWindowController;
import header.subComponents.updateUsername.UpdateUsernameController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    private SimpleStringProperty username;
    private AppController mainController;
    private PopupWindowController popupWindowController;
    private Scene popupWindowScene;
    private AnchorPane popupRoot;

    @FXML
    private void initialize() {
        username = new SimpleStringProperty();
        usernameLabel.textProperty().bind(username);
        URL url = getClass().getResource("/header/common/popupWindow.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        try {
            popupRoot = fxmlLoader.load();
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


    public void setUsername(String text) {
        username.setValue(text);
        mainController.setUsername(text);
    }
}
