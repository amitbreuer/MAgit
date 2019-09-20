package header.subComponents.createEmptyRepositoryWindow;

import header.HeaderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class CreateEmptyRepositoryWindowController {

    @FXML
    AnchorPane createEmptyrepositoryAnchorPane;
    @FXML
    TextField repositoryNameTextField;
    @FXML
    TextField repositoryPathTextField;
    @FXML
    TextField mainFolderNameTextFiled;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;
    @FXML
    Button directorySelectionButton;

    private HeaderController mainController;
    private SimpleStringProperty repositoryLocationProperty;
    private boolean actionNotCanelled;

    public String getMainFolderName() {
        return mainFolderNameTextFiled.getText();
    }

    @FXML
    private void initialize() {
        repositoryLocationProperty = new SimpleStringProperty();
        okButton.disableProperty().bind(repositoryNameTextField.textProperty().isEmpty()
                .or(repositoryLocationProperty.isEmpty())
                .or(mainFolderNameTextFiled.textProperty().isEmpty()));
                repositoryPathTextField.textProperty().bind(repositoryLocationProperty);
    }

    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    public void directorySelectionButtonAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select location for repository");
        File f = directoryChooser.showDialog(new Stage());
        if (f != null) {
            repositoryLocationProperty.setValue(f.getPath());
        }
    }

    public void okButtonAction(ActionEvent actionEvent) {
        actionNotCanelled = true;
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        actionNotCanelled = false;
        closeStage();
    }

    public boolean isActionNotCanelled() {
        return actionNotCanelled;
    }

    public String getRepositoryPath() {
        return repositoryLocationProperty.get()+File.separator+mainFolderNameTextFiled.getText();
    }

    public String getRepositoryName(){
        return repositoryNameTextField.getText();
    }
}
