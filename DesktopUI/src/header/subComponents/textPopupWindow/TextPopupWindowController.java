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

    @FXML private void initialize(){
        text = new SimpleStringProperty();
        text.bind(textField.textProperty());
        //okButton.setDisable(textField.textProperty().isEmpty().getValue().equals(true));
    }

    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void okButtonAction(ActionEvent actionEvent) {
        closeStage();
    }

    @FXML
    public void cancelButtonAction(ActionEvent actionEvent) {
        closeStage();
    }

    private void closeStage(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public String getText() {
        return text.get();
    }

    public SimpleStringProperty textProperty() {
        return text;
    }

    public void setLabel(String text) {
        label.textProperty().setValue(text);
    }
}
