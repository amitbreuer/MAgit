package body.commitNode;

import body.BodyController;
import body.binds.ParentIsNullBind;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Set;

public class CommitNodeController {
    @FXML
    Label commitDateCreatedLabel;
    @FXML
    Label messageLabel;
    @FXML
    Label committerLabel;
    @FXML
    Label sha1Label;
    @FXML
    Circle CommitCircle;
    @FXML
    AnchorPane branchesLabels;

    private SimpleStringProperty branchParentSha1;
    private SimpleStringProperty mergeParentSha1;
    private SimpleBooleanProperty hasPointedBranches;
    private BodyController mainController;
    private Set<String> pointedBranches;

    @FXML
    public void initialize() {
        branchParentSha1 = new SimpleStringProperty("");
        mergeParentSha1 = new SimpleStringProperty("");
        hasPointedBranches = new SimpleBooleanProperty();
        setCommitNodeContextMenu();
    }

    private void setCommitNodeContextMenu() {
        ContextMenu commitContextMenu = new ContextMenu();

        MenuItem showCommitFiles = new MenuItem("Show Files Of Commit");
        showCommitFiles.setOnAction(e -> mainController.ShowFilesOfCommit(sha1Label.getText()));

        MenuItem branchParentDelta = new MenuItem("Show branch parent Delta");
        branchParentDelta.setOnAction(e -> mainController.ShowDelta(sha1Label.getText(), branchParentSha1.getValue()));
        branchParentDelta.disableProperty().bind(new ParentIsNullBind(branchParentSha1));

        MenuItem mergeParentDelta = new MenuItem("Show merge parent Delta");
        mergeParentDelta.setOnAction(e -> mainController.ShowDelta(sha1Label.getText(), mergeParentSha1.getValue()));
        mergeParentDelta.disableProperty().bind(new ParentIsNullBind(mergeParentSha1));

        MenuItem newBranch = new MenuItem("Create New Branch");
        newBranch.setOnAction(e -> mainController.CreateNewBranchForCommit(sha1Label.getText()));

        MenuItem resetHeadBranch = new MenuItem(("Reset Head Branch"));
        resetHeadBranch.setOnAction(e -> mainController.RestHeadBranch(sha1Label.getText()));

        Menu mergeWithHead = new Menu(("Merge With Head Branch"));
//        mergeWithHead.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//
//            }
//        });
        mergeWithHead.setOnAction(e->{
            for (String branchName : pointedBranches) {
                MenuItem branchItem = new MenuItem(branchName);
                branchItem.setOnAction(x -> mainController.MergeBranchWithHead(branchName));
                mergeWithHead.getItems().add(branchItem);
            }
            mergeWithHead.show();
        });
        mergeWithHead.disableProperty().bind(hasPointedBranches.not());

        Menu deleteBranch = new Menu("Delete Branch");
        deleteBranch.setOnAction(e->{
            for (String branchName : pointedBranches) {
                MenuItem branchItem = new MenuItem(branchName);
                branchItem.setOnAction(x -> mainController.DeleteBranchFromCommit(branchName));
                deleteBranch.getItems().add(branchItem);
            }
            deleteBranch.show();
        });
        deleteBranch.disableProperty().bind(hasPointedBranches.not());

        commitContextMenu.getItems().addAll(showCommitFiles, branchParentDelta, mergeParentDelta,
                new SeparatorMenuItem(), newBranch, resetHeadBranch, mergeWithHead, deleteBranch);
        CommitCircle.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                commitContextMenu.show(CommitCircle, event.getScreenX(), event.getScreenY());
                mainController.ShowCommitInfo(sha1Label.getText());
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
        return (int) CommitCircle.getRadius();
    }

    public void setBranchParentSha1(String branchParentSha1) {
        this.branchParentSha1.setValue(branchParentSha1);
    }

    public void setMergeParentSha1(String mergeParentSha1) {
        this.mergeParentSha1.setValue(mergeParentSha1);
    }

    public void setPointedBranches(Set<String> pointedBranches) {
        this.pointedBranches = pointedBranches;
        for (String branchName : pointedBranches) {
            Label branchLabel = new Label(branchName);
            branchLabel.setStyle("-fx-background-color: yellow;");
            branchesLabels.getChildren().add(branchLabel);
        }
        hasPointedBranches.setValue(!pointedBranches.isEmpty());
    }
}
