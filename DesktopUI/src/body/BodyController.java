package body;

import app.AppController;
import body.commitNode.CommitNode;
import body.commitNode.CommitNodeController;
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
        Map<String, Integer> sha1ToYcoordinate = new HashMap<>();
        Map<String, Integer> sha1ToXCoordinate = new HashMap<>();

        List<Branch> branches = mainController.GetBranches();
        Map<String,Commit> commitsMap = mainController.GetAllCommitsMap();
        Set<Edge> edges = new HashSet<>();

        Branch headBranch = mainController.GetHeadBranch();

        calculateYCoordinate(commitsMap,commitNodesMap,sha1ToYcoordinate);

        //set edges
        for (Branch branch : branches) {
            Commit branchLastCommit;
            branchLastCommit = branch.getLastCommit();
            connectCommitNodesEdges(branchLastCommit, commitsMap, commitNodesMap, edges);
        }

        calculateXCoordinate(branches, headBranch, sha1ToXCoordinate, commitsMap);

        //adding components to tree's model
        for (Map.Entry<String, CommitNode> entry : commitNodesMap.entrySet()) {
            model.addCell(entry.getValue());
        }
        for (Edge edge : edges) {
            model.addEdge(edge);
        }

        tree.endUpdate();
        tree.layout(new CommitTreeLayout(sha1ToXCoordinate, sha1ToYcoordinate));
    }

    private void calculateYCoordinate(Map<String, Commit> commitsMap, Map<String, CommitNode> commitNodesMap, Map<String, Integer> sha1ToYcoordinate) {
        int yCoordinate = 10;
        List<CommitNode> commitNodesList = new ArrayList<>();
        for(Map.Entry<String, Commit> entry : commitsMap.entrySet()){
            CommitNode commitNode = new CommitNode(entry.getValue());
            commitNodesList.add(commitNode);
            commitNodesMap.put(commitNode.getSha1(),commitNode);
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

        for(CommitNode commitNode : commitNodesList){
            sha1ToYcoordinate.put(commitNode.getSha1(),yCoordinate);
            yCoordinate+=30;
        }
    }

    private void calculateXCoordinate(List<Branch> branches, Branch headBranch, Map<String, Integer> sha1ToXCoordinate, Map<String, Commit> commitsMap) {
        int xCoordinate = 10;
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
            if(!branch.equals(headBranch)){
                currentCommit = branch.getLastCommit();
                while (currentCommit != null) {
                    if (!sha1ToXCoordinate.containsKey(currentCommit.getSha1())) {
                        sha1ToXCoordinate.put(currentCommit.getSha1(), xCoordinate);
                    }
                    prevCommitSha1 = currentCommit.getPrevCommitSha1();
                    if (prevCommitSha1 == null) {
                        break;
                    }
                    currentCommit = commitsMap.get(prevCommitSha1);
                }
                xCoordinate += 30;
            }
        }
    }

/*
    private void calculateXCoordinate(CommitNode rootCommit, Integer xCoordinate, Map<String, Integer> sha1ToXCoordinate, Map<String, CommitNode> commitNodesMap) {
        sha1ToXCoordinate.put(rootCommit.getSha1(), xCoordinate);
        String branchChildSha1;
        String mergeChildSha1;
        branchChildSha1 = rootCommit.getBranchChildSha1();
        mergeChildSha1 = rootCommit.getMergeChildSha1();
        CommitNode branchChildNode;
        CommitNode mergeChildNode;

        if (branchChildSha1 != null) {
            branchChildNode = commitNodesMap.get(branchChildSha1);
            calculateXCoordinate(branchChildNode, xCoordinate, sha1ToXCoordinate, commitNodesMap);
        }
        if (mergeChildSha1 != null) {
            mergeChildNode = commitNodesMap.get(mergeChildSha1);
            calculateXCoordinate(mergeChildNode, xCoordinate + 30, sha1ToXCoordinate, commitNodesMap);
        }
    }
*/

//    private Commit findCommitTreeRoot(Map<String, Commit> commitsMap) {
//        Commit CommitToReturn = null;
//        for (Map.Entry<String, Commit> entry : commitsMap.entrySet()) {
//            if (entry.getValue().getPrevCommitSha1() == null && entry.getValue().getSecondPrecedingSha1() == null) {
//                CommitToReturn = entry.getValue();
//            }
//        }
//        return CommitToReturn;
//    }

    private void connectCommitNodesEdges(Commit commit, Map<String, Commit> commitsMap, Map<String, CommitNode> commitsNodeMap, Set<Edge> edges) {
        String branchParentSha1 = commit.getPrevCommitSha1();
        String mergedParentSha1 = commit.getSecondPrecedingSha1();
        String commitSha1 = commit.getSha1();
        CommitNode currentCommitNode = commitsNodeMap.get(commitSha1);
        CommitNode branchParentCommitNode;
        CommitNode mergedParentCommitNode;
        Commit branchParentCommit;
        Commit mergedParentCommit;

        if (branchParentSha1 != null) {
            branchParentCommit = commitsMap.get(branchParentSha1);
            branchParentCommitNode = commitsNodeMap.get(branchParentSha1);
            edges.add(new Edge(currentCommitNode, branchParentCommitNode));
            connectCommitNodesEdges(branchParentCommit, commitsMap, commitsNodeMap, edges);
        }
        if (mergedParentSha1 != null) {
            mergedParentCommit = commitsMap.get(mergedParentSha1);
            mergedParentCommitNode = commitsNodeMap.get(mergedParentSha1);
            edges.add(new Edge(currentCommitNode, mergedParentCommitNode));
            connectCommitNodesEdges(mergedParentCommit, commitsMap, commitsNodeMap, edges);
        }
    }
}
