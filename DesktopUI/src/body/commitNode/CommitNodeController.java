package body.commitNode;

import body.BodyController;
import body.binds.ParentIsNullBind;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class CommitNodeController {
    @FXML private Label commitDateCreatedLabel;
    @FXML private Label messageLabel;
    @FXML private Label committerLabel;
    @FXML private Label sha1Label;
    @FXML private Circle CommitCircle;

    private SimpleStringProperty branchParentSha1;
    private SimpleStringProperty mergeParentSha1;
    private BodyController mainController;

    @FXML
    public void initialize(){
        branchParentSha1 = new SimpleStringProperty("");
        mergeParentSha1 = new SimpleStringProperty("");
        setCommitNodeContextMenu();
    }

    private void setCommitNodeContextMenu() {
        ContextMenu commitContextMenu = new ContextMenu();

        MenuItem showCommitFiles = new MenuItem("Show Files Of Commit");
        showCommitFiles.setOnAction((x)->mainController.ShowFilesOfCommit(sha1Label.getText()));

        MenuItem branchParentDelta = new MenuItem("Show branch parent Delta");
        branchParentDelta.setOnAction((x)-> mainController.ShowDelta(sha1Label.getText(),branchParentSha1.getValue()));
        branchParentDelta.disableProperty().bind(new ParentIsNullBind(branchParentSha1));

        MenuItem mergeParentDelta = new MenuItem("Show merge parent Delta");
        mergeParentDelta.setOnAction((x)-> mainController.ShowDelta(sha1Label.getText(),mergeParentSha1.getValue()));
        mergeParentDelta.disableProperty().bind(new ParentIsNullBind(mergeParentSha1));

        MenuItem bonus = new MenuItem("bonus");
        commitContextMenu.getItems().addAll(showCommitFiles,branchParentDelta,mergeParentDelta,new SeparatorMenuItem(),bonus);
        CommitCircle.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                commitContextMenu.show(CommitCircle,event.getScreenX(),event.getScreenY());
            }
        });
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

    public void setBranchParentSha1(String branchParentSha1) {
        this.branchParentSha1.setValue(branchParentSha1);
    }

    public void setMergeParentSha1(String mergeParentSha1) {
        this.mergeParentSha1.setValue(mergeParentSha1);
    }
}
