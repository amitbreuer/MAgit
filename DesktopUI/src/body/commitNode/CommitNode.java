package body.commitNode;

import body.BodyController;
import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import engine.Commit;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommitNode extends AbstractCell {
    private String sha1;
    private String dateCreated;
    private String committer;
    private String message;
    private String branchChildSha1;
    private String mergeChildSha1;
    private CommitNodeController commitNodeController;
    private BodyController bodyController;
    private Set<String> pointedBranches;

    public CommitNode(Commit commit, BodyController bodyController) {
        this.sha1 = commit.getSha1();
        this.dateCreated = commit.getDateCreated();
        this.committer = commit.getCreator();
        this.message = commit.getMessage();
        this.branchChildSha1 = commit.getPrevCommitSha1();
        this.mergeChildSha1 = commit.getSecondPrecedingSha1();
        this.bodyController = bodyController;
        this.pointedBranches = new HashSet<>();
    }

    public String getSha1() {
        return sha1;
    }

    public CommitNodeController getCommitNodeController() {
        return commitNodeController;
    }

    //    public void setBranchChildSha1(String branchChildSha1) {
//        this.branchChildSha1 = branchChildSha1;
//    }
//    public String getBranchChildSha1() {
//        return branchChildSha1;
//    }
//
//    public String getMergeChildSha1() {
//        return mergeChildSha1;
//    }
//
//    public void setMergeChildSha1(String mergeChildSha1) {
//        this.mergeChildSha1 = mergeChildSha1;
//    }

    public String getDateCreated() {
        return dateCreated;
    }

    @Override
    public Region getGraphic(Graph graph) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("commitNode.fxml");
            fxmlLoader.setLocation(url);
            GridPane root = fxmlLoader.load(url.openStream());

            commitNodeController = fxmlLoader.getController();
            commitNodeController.setSha1(sha1);
            commitNodeController.setCommitMessage(message);
            commitNodeController.setCommitter(committer);
            commitNodeController.setCommitDateCreated(dateCreated);
            commitNodeController.setBranchParentSha1(branchChildSha1);
            commitNodeController.setMergeParentSha1(mergeChildSha1);
            commitNodeController.setPointedBranches(pointedBranches);
            commitNodeController.setMainController(bodyController);

            return root;
        } catch (IOException e) {
            return new Label("Error when tried to create graphic node !");
        }
    }

    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(commitNodeController.getCircleRadius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitNode that = (CommitNode) o;

        return dateCreated != null ? dateCreated.equals(that.dateCreated) : that.dateCreated == null;
    }

    @Override
    public int hashCode() {
        return dateCreated != null ? dateCreated.hashCode() : 0;
    }

    public void AddPointedBranch(String branchName) {
        this.pointedBranches.add(branchName);
    }
}
