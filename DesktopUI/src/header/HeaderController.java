package header;

import app.AppController;
import exceptions.XmlPathContainsNonRepositoryObjectsException;
import exceptions.XmlRepositoryAlreadyExistsException;
import header.subComponents.ClickableMenu;
import header.subComponents.errorPopupWindow.ErrorPopupWindowController;
import header.subComponents.newBranchSelectionWindow.NewBranchSelectionWindowController;
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
    Label usernameLabel;
    @FXML
    Label repositoryLabel;

    private AppController mainController;
    private SimpleStringProperty username;
    private SimpleStringProperty currentRepository;
    private TextPopupWindowController popupWindowController;
    private PathContainsRepositoryWindowController pathContainsRepositoryWindowController;
    private ErrorPopupWindowController errorPopupWindowController;
    private NewBranchSelectionWindowController newBranchSelectionWindowController;

    private Scene popupWindowScene;
    private Scene pathContainsRepositoryWindowScene;
    private Scene errorPopupWindowScene;
    private Scene newBranchSelectionWindowScene;

    @FXML
    private void initialize() {
        username = new SimpleStringProperty();
        usernameLabel.textProperty().bind(username);
        username.setValue("Administrator");
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
            currentRepository.bind(Bindings.concat(f.getPath(),File.separator,popupWindowController.getText()));
            try {
                mainController.createNewRepository(currentRepository);
            } catch (Exception e) {
                stage.setTitle("Error");
                errorPopupWindowController.SetErrorMessage("This repository already exists");
                stage.setScene(errorPopupWindowScene);
                stage.show();
            }
            currentRepository.unbind();
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
        } catch (Exception ex) {
            Stage stage = new Stage();
            stage.setTitle("Error");
            errorPopupWindowController.SetErrorMessage(ex.getMessage());
            stage.setScene(errorPopupWindowScene);
            stage.show();
        }

    }

    @FXML
    public void showAllBranchesButtonAction(ActionEvent actionEvent) {
        mainController.ShowAllBranches();
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
    public void deleteBranchButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        stage.setTitle("Delete branch");
        popupWindowController.setLabel("Enter branch name:");
        stage.setScene(popupWindowScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        try {
            mainController.DeleteBranch(popupWindowController.textProperty().get());
        } catch (Exception ex) {

            Stage errorStage = new Stage();
            stage.setTitle("Error");
            errorPopupWindowController.SetErrorMessage(ex.getMessage());
            errorStage.setScene(errorPopupWindowScene);
            errorStage.initModality(Modality.APPLICATION_MODAL);
            errorStage.showAndWait();
        }

    }

    @FXML
    public void checkoutButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    public void resetHeadButtonAction(ActionEvent actionEvent) {
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
    }

    public void replaceExistingRepositoryWithXmlRepository() {
        mainController.replaceExistingRepositoryWithXmlRepository();
        currentRepository.setValue(mainController.getRepositoryName());
    }

    @FXML
    public void CreateNewBranch(String branchname, boolean checkout) {
        try {
            mainController.createNewBranch(branchname, checkout);
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

    @FXML
    public void commitAction(ActionEvent actionEvent) {
        /*MenuItem commitMenuItem = new MenuItem();
        commitMenuItem.setVisible(Boolean.FALSE);
        commitMenuItem.setOnAction((e) -> {
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
        commitMenu.getItems().add(commitMenuItem);
        commitMenu.showingProperty().addListener(((observable, oldValue, newValue) -> {
            commitMenu.getItems().get(0).fire();
        }));
        System.out.println("Commit Executed successfully");    */
    }
}
