package app.subComponents.singleConflictWindow;

import app.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class SingleConflictController {
    @FXML
    TextArea oursTextArea;
    @FXML
    TextArea fathersTextArea;
    @FXML
    TextArea theirsTextArea;
    @FXML
    TextArea editTextArea;
    @FXML
    Button chooseOursButton;
    @FXML
    Button chooseFathersButton;
    @FXML
    Button chooseTheirsButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;

    private AppController mainController;
    private SimpleStringProperty mergedContent;
    private SimpleBooleanProperty oursExists;
    private SimpleBooleanProperty theirsExists;
    private SimpleBooleanProperty fathersExists;
    private boolean isNotCancelled; ////////////////////

    @FXML
    private void initialize() {
        mergedContent = new SimpleStringProperty();
        oursExists = new SimpleBooleanProperty();
        theirsExists = new SimpleBooleanProperty();
        fathersExists = new SimpleBooleanProperty();

        oursTextArea.visibleProperty().bind(oursExists);
        chooseOursButton.visibleProperty().bind(oursExists);

        theirsTextArea.visibleProperty().bind(theirsExists);
        chooseTheirsButton.visibleProperty().bind(theirsExists);

        fathersTextArea.visibleProperty().bind(fathersExists);
        chooseFathersButton.visibleProperty().bind(fathersExists);

    }

    public void SetVersions(String oursFileContent, String theirsFileContent, String fathersFileContent) {
        oursExists.setValue(oursFileContent != null);
        theirsExists.setValue(theirsFileContent != null);
        fathersExists.setValue(fathersFileContent != null);

        oursTextArea.textProperty().setValue(oursFileContent);
        theirsTextArea.textProperty().setValue(theirsFileContent);
        fathersTextArea.textProperty().setValue(fathersFileContent);
    }

    @FXML
    public void okButtonAction(ActionEvent actionEvent) {
        mergedContent.setValue(editTextArea.getText());
        closeStage();
    }

    @FXML
    public void cancelButtonAction(ActionEvent actionEvent) {

        closeStage();
    }

    @FXML
    public void chooseOursButtonAction(ActionEvent actionEvent) {
        mergedContent.setValue(oursTextArea.getText());
        closeStage();
    }

    @FXML
    public void chooseFathersButtonAction(ActionEvent actionEvent) {
        mergedContent.setValue(fathersTextArea.getText());
        closeStage();
    }

    @FXML
    public void chooseTheirsButtonAction(ActionEvent actionEvent) {
        mergedContent.setValue(theirsTextArea.getText());
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public String getMergedContent() {
        return mergedContent.get();
    }

    public SimpleStringProperty mergedContentProperty() {
        return mergedContent;
    }
}
