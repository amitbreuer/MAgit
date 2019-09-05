package body.commitNode;

import body.BodyController;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;

public class CommitNodeController {
    @FXML private Label commitDateCreatedLabel;
    @FXML private Label messageLabel;
    @FXML private Label committerLabel;
    @FXML private Label sha1Label;
    @FXML private Circle CommitCircle;

    private BodyController mainController;

    @FXML
    public void initiazlie(){
        System.out.println("BLA");
    }

    public void setMainController(BodyController mainController) {
        this.mainController = mainController;
    }

    public void setCommitDateCreated(String dateCreated) {
        commitDateCreatedLabel.setText(dateCreated);
        commitDateCreatedLabel.setTooltip(new Tooltip(dateCreated));
    }

    public void setCommitter(String committerName) {
        committerLabel.setText(committerName);
        committerLabel.setTooltip(new Tooltip(committerName));
    }

    public void setCommitMessage(String commitMessage) {
        messageLabel.setText(commitMessage);
        messageLabel.setTooltip(new Tooltip(commitMessage));
    }

    public void setSha1(String sha1) {
        sha1Label.setText(sha1);
        sha1Label.setTooltip(new Tooltip(sha1));
    }

    public int getCircleRadius() {
        return (int)CommitCircle.getRadius();
    }

    @FXML
    public void showContextMenu(ContextMenuEvent contextMenuEvent) {
        ContextMenu commitContextMenu = new ContextMenu();
        MenuItem branchParentDelta = new MenuItem();
        branchParentDelta.setOnAction((x)-> System.out.println("BlaBla"));
        commitContextMenu.getItems().add(branchParentDelta);
        //commitContextMenu.show();
    }
}
