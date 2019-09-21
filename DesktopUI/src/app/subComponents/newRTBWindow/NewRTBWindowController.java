package app.subComponents.newRTBWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class NewRTBWindowController {
    @FXML
    Button yesButton;
    @FXML
    Button noButton;
    @FXML
    Label branchNameLabel;

    private boolean createRTBAndCheckout;

    public void yesButtonAction(ActionEvent actionEvent) {
        createRTBAndCheckout = true;
        closeStage();
    }

    public void noButtonAction(ActionEvent actionEvent) {
        createRTBAndCheckout = false;
        closeStage();
    }

    private void closeStage(){
        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }

    public void setBranchNameLabel(String branchNameLabel) {
        this.branchNameLabel.setText(branchNameLabel);
    }

    public boolean CreateRTBAndCheckout() {
        return createRTBAndCheckout;
    }

}
