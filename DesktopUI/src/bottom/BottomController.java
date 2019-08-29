package bottom;

import app.AppController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class BottomController {
    @FXML
    AnchorPane anchorPane;
    @FXML
    Label messageLabel;

    private AppController mainController;
    private SimpleStringProperty message;

    @FXML
    private void initialize() {
        message = new SimpleStringProperty();
        messageLabel.textProperty().bind(message);
        message.setValue("Please set repository");
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
}
