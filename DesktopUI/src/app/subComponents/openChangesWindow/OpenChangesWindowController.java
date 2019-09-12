package app.subComponents.openChangesWindow;

import app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class OpenChangesWindowController {
    @FXML
    AnchorPane anchorPane;
    @FXML
    Button okButton;
    @FXML
    CheckBox commitCheckBox;

    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public CheckBox getCommitCheckBox() {
        return commitCheckBox;
    }

    public void okButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
