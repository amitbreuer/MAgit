package header.common;

import header.HeaderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PopupWindowController {
    @FXML private TextField textField;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    @FXML private Label label;

    private SimpleStringProperty text;
    private SimpleStringProperty labelProperty;
    private HeaderController mainController;

    @FXML private void initialize(){
        text = new SimpleStringProperty();
        text.bind(textField.textProperty());
    }

    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void okButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
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
