package header.subComponents.popupWindow;

import header.HeaderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PopupWindowController {
    @FXML private TextField textField;
    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private HeaderController mainController;


    @FXML private void initialize(){
    }


    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void okButtonAction(ActionEvent actionEvent) {
        mainController.setUsername(textField.getText());
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
