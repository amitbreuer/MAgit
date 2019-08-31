package header.subComponents.pathContainsRepositoryWindow;

import header.HeaderController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PathContainsRepositoryWindowController {
    @FXML
    private RadioButton replaceExistingRepositoryRadioButton;
    @FXML
    private RadioButton keepCurrentReopistoryRadioButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private HeaderController mainController;


    @FXML
    private void initialize() {
    }

    public void setMainController(HeaderController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void okButtonAction(ActionEvent actionEvent) {
        if(replaceExistingRepositoryRadioButton.isSelected()) {
         mainController.replaceExistingRepositoryWithXmlRepository();
        }
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
