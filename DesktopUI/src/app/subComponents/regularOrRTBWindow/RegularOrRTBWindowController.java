package app.subComponents.regularOrRTBWindow;

import app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegularOrRTBWindowController {
    @FXML
    RadioButton createRTBRadioButton;
    @FXML
    RadioButton regularBranchRadioButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;
    @FXML
    TextField branchNameTextField;

    private AppController mainController;
    private String RBName;
    private String commitSha1;

    @FXML
    private void initialize(){
        branchNameTextField.disableProperty().bind(regularBranchRadioButton.selectedProperty().not());
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;        
    }

    public void okButtonAction(ActionEvent actionEvent) {
        if(createRTBRadioButton.isSelected()){
            mainController.CreateRTBWithoutCheckout(RBName);
        } else {
            mainController.CreateNewRegularBranch(branchNameTextField.getText(),false,false,commitSha1);
        }
        closeStage();
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        closeStage();
    }

    public void setRBName(String rbName) {
        this.RBName = rbName;
    }

    public void setCommitSha1(String commitSha1) {
        this.commitSha1 = commitSha1;
    }

    private void closeStage(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
