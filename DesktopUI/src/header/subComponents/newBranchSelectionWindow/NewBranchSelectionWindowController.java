package header.subComponents.newBranchSelectionWindow;

import header.HeaderController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import java.awt.*;

public class NewBranchSelectionWindowController {

    @FXML
    Button cancelButton;
    @FXML
    Button okButton;
    @FXML
    RadioButton checkoutNewBranchRadioButton;
    @FXML
    TextField textField;

    private HeaderController mainController;

    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    public void okButtonAction(ActionEvent actionEvent) {
        mainController.CreateNewBranch(textField.getText(),checkoutNewBranchRadioButton.isSelected() == Boolean.TRUE);
        closeStage();
    }

    private void closeStage(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        closeStage();
    }
}
