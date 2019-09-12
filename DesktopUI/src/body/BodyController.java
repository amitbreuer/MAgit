package body;

import app.AppController;
import body.commitNode.CommitNode;
import body.commitNode.CommitTreeLayout;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.Model;
import com.fxgraph.graph.PannableCanvas;
import engine.Branch;
import engine.Commit;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javafx.fxml.FXML;

public class BodyController {
    @FXML
    private ScrollPane scrollPane;

    public SimpleBooleanProperty repositoryUpdated;
    private AppController mainController;

    @FXML
    private void initialize() {
        repositoryUpdated = new SimpleBooleanProperty();

/*        noAvailableRepository.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                showCommitTree();
            }
        });*/
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void showCommitTree() {
        Graph tree = new Graph();
        createCommitNodes(tree);
        PannableCanvas canvas = tree.getCanvas();
        scrollPane.setContent(canvas);
    }

    private void createCommitNodes(Graph tree) {
        Model model = tree.getModel();
        tree.beginUpdate();

        Map<String, CommitNode> commitNodesMap = new HashMap<>();
        Map<String, Integer> sha1ToYCoordinate = new HashMap<>();
        Map<String, Integer> sha1ToXCoordinate = new HashMap<>();

        List<Branch> branches = mainController.GetBranches();
        Map<String, Commit> commitsMap = mainController.GetAllCommitsMap();
        Set<Edge> edges = new HashSet<>();

        Branch headBranch = mainController.GetHeadBranch();

        calculateYCoordinate(commitsMap, commitNodesMap, sha1ToYCoordinate);

        calculateXCoordinate(branches, headBranch, sha1ToXCoordinate, commitsMap);

        //set edges and branches labels
        for (Branch branch : branches) {
            Commit branchLastCommit = branch.getLastCommit();
            if (branchLastCommit != null) {
                CommitNode branchLastCommitNode = commitNodesMap.get(branchLastCommit.getSha1());
                branchLastCommitNode.AddPointedBranch(branch.getName());
                connectCommitNodesEdges(branchLastCommit, commitsMap, commitNodesMap, edges);
            }
        }

        //adding components to tree's model
        for (Map.Entry<String, CommitNode> entry : commitNodesMap.entrySet()) {
            model.addCell(entry.getValue());
        }
        for (Edge edge : edges) {
            model.addEdge(edge);
        }

        tree.endUpdate();
        tree.layout(new CommitTreeLayout(sha1ToXCoordinate, sha1ToYCoordinate));
    }

    private void calculateYCoordinate(Map<String, Commit> commitsMap, Map<String, CommitNode> commitNodesMap, Map<String, Integer> sha1ToYcoordinate) {
        int yCoordinate = 10;
        List<CommitNode> commitNodesList = new ArrayList<>();
        for (Map.Entry<String, Commit> entry : commitsMap.entrySet()) {
            CommitNode commitNode = new CommitNode(entry.getValue(), this);
            commitNodesList.add(commitNode);
            commitNodesMap.put(commitNode.getSha1(), commitNode);
        }

        Collections.sort(commitNodesList, new Comparator<CommitNode>() {
            @Override
            public int compare(CommitNode o1, CommitNode o2) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = formatter.parse(o1.getDateCreated());
                    date2 = formatter.parse(o2.getDateCreated());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date2.compareTo(date1);
            }
        });

        for (CommitNode commitNode : commitNodesList) {
            sha1ToYcoordinate.put(commitNode.getSha1(), yCoordinate);
            yCoordinate += 30;
        }
    }

    private void calculateXCoordinate(List<Branch> branches, Branch headBranch, Map<String, Integer> sha1ToXCoordinate, Map<String, Commit> commitsMap) {
        int xCoordinate = 10;
        int numOfBrancheNodes;
        Commit currentCommit = headBranch.getLastCommit();
        String prevCommitSha1;

        while (currentCommit != null) {
            sha1ToXCoordinate.put(currentCommit.getSha1(), xCoordinate);
            prevCommitSha1 = currentCommit.getPrevCommitSha1();
            if (prevCommitSha1 == null) {
                break;
            }
            currentCommit = commitsMap.get(prevCommitSha1);
        }
        xCoordinate += 30;

        for (Branch branch : branches) {
            if (!branch.equals(headBranch)) {
                numOfBrancheNodes = 0;
                currentCommit = branch.getLastCommit();
                while (currentCommit != null) {
                    if (!sha1ToXCoordinate.containsKey(currentCommit.getSha1())) {
                        sha1ToXCoordinate.put(currentCommit.getSha1(), xCoordinate);
                        numOfBrancheNodes++;
                    }
                    prevCommitSha1 = currentCommit.getPrevCommitSha1();
                    if (prevCommitSha1 == null) {
                        break;
                    }
                    currentCommit = commitsMap.get(prevCommitSha1);
                }
                if(numOfBrancheNodes > 0){
                    xCoordinate += 30;
                }
            }
        }
    }

    private void connectCommitNodesEdges(Commit commit, Map<String, Commit> commitsMap, Map<String, CommitNode> commitsNodeMap, Set<Edge> edges) {
        String branchParentSha1 = commit.getPrevCommitSha1();
        String mergedParentSha1 = commit.getSecondPrecedingSha1();
        String commitSha1 = commit.getSha1();
        CommitNode currentCommitNode = commitsNodeMap.get(commitSha1);
        CommitNode branchParentCommitNode;
        CommitNode mergedParentCommitNode;
        Commit branchParentCommit;
        Commit mergedParentCommit;

        if (!branchParentSha1.equals("")) {
            branchParentCommit = commitsMap.get(branchParentSha1);
            branchParentCommitNode = commitsNodeMap.get(branchParentSha1);
            edges.add(new Edge(currentCommitNode, branchParentCommitNode));
            connectCommitNodesEdges(branchParentCommit, commitsMap, commitsNodeMap, edges);
        }
        if (!mergedParentSha1.equals("")) {
            mergedParentCommit = commitsMap.get(mergedParentSha1);
            mergedParentCommitNode = commitsNodeMap.get(mergedParentSha1);
            edges.add(new Edge(currentCommitNode, mergedParentCommitNode));
            connectCommitNodesEdges(mergedParentCommit, commitsMap, commitsNodeMap, edges);
        }
    }

    public void ShowDelta(String commit1Sha1, String commit2Sha1) {
        mainController.GetDeltaBetweenTwoCommits(commit1Sha1, commit2Sha1);
    }

    public void ShowFilesOfCommit(String commitSha1) {
        mainController.ShowSingleCommitFilesTree(commitSha1);
    }

    public void ShowCommitInfo(String commitSha1) {
        mainController.ShowCommitInfo(commitSha1);
    }

    public void Clear() {
        scrollPane.setContent(new Label(""));
    }

    public void CreateNewBranchForCommit(String commitSha1) {
        mainController.CreateNewBranchForCommit(commitSha1);
    }

    public void RestHeadBranch(String commitSha1) {
        mainController.ResetHead(commitSha1);
    }

    public void MergeBranchWithHead(String branchName) {
        mainController.Merge(branchName);
    }

    public void DeleteBranchFromCommit(String branchName) {
        mainController.DeleteBranchFromCommit(branchName);
    }
}
