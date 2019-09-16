package header.subComponents.textPopupWindow;

import header.HeaderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.script.Bindings;

public class TextPopupWindowController {
    @FXML private TextField textField;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    @FXML private Label label;

    private SimpleStringProperty text;
    private HeaderController mainController;
    private boolean actionNotCanelled;

    @FXML private void initialize(){
        text = new SimpleStringProperty();
        text.bind(textField.textProperty());
        okButton.disableProperty().bind(textField.textProperty().isEmpty());
    }

    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void okButtonAction(ActionEvent actionEvent) {
        actionNotCanelled = true;
        closeStage();
    }

    @FXML
    public void cancelButtonAction(ActionEvent actionEvent) {
        actionNotCanelled = false;
        closeStage();
    }

    private void closeStage(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public String getText() {
        return textField.getText();
    }

    public SimpleStringProperty textProperty() {
        return text;
    }

    public void setLabel(String text) {
        label.textProperty().setValue(text);
    }

    public void ClearTextField(){
        textField.clear();
    }

    public boolean isActionNotCanelled() {
        return actionNotCanelled;
    }
}
