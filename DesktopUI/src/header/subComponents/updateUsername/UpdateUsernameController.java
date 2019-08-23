package header.subComponents.updateUsername;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;

import java.awt.*;

public class UpdateUsernameController {

    @FXML AnchorPane updateusernamewindow;
    @FXML Label enterUsernameLabel;
    @FXML TextField usernameTextField;
    @FXML Button okButton;

    private SimpleStringProperty username;

    public UpdateUsernameController() {
        this.username = new SimpleStringProperty();
    }

    @FXML
    private void initialize() {
        //okButton.disableProperty().bind(usernameTextField);

    }

    @FXML
    public void OKButtonAction(ActionEvent actionEvent) {

    }
}
