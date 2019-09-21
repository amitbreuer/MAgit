package header.subComponents.newBranchSelectionWindow;

import header.HeaderController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class NewBranchSelectionWindowController {

    @FXML
    CheckBox checkoutNewBranchCheckBox;
    @FXML
    Button cancelButton;
    @FXML
    Button okButton;
    @FXML
    RadioButton headBranchCommitButton;
    @FXML
    RadioButton otherCommitButton;
    @FXML
    TextField branchNameTextField;
    @FXML
    TextField otherCommitSha1TextField;

    private HeaderController mainController;

    @FXML
    private void initialize(){
        otherCommitSha1TextField.visibleProperty().bind(otherCommitButton.selectedProperty());
    }

    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    public void okButtonAction(ActionEvent actionEvent) {
        mainController.CreateNewBranch(branchNameTextField.getText(),checkoutNewBranchCheckBox.isSelected(),
                headBranchCommitButton.isSelected(),otherCommitSha1TextField.getText());
        closeStage();
    }

    private void closeStage(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        closeStage();
    }

    public void ClearTextField() {
        branchNameTextField.clear();
    }
}
