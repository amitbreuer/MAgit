package header.subComponents.errorPopupWindow;

import header.HeaderController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ErrorPopupWindowController {
    @FXML private Button okButton;
    @FXML private Label errorLable;

    private HeaderController mainController;

    public void SetErrorMessage(String message) {
        errorLable.textProperty().setValue(message);
    }

    @FXML private void initialize(){
    }

    @FXML
    public void okButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage)okButton.getScene().getWindow();
        stage.close();
    }
}
