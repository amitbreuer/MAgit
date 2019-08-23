package engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {
    private Path path;
    private Map<String,Commit> recentlyUsedCommits;
    private List<Branch> branches;
    private Branch headBranch;


    public Repository(String path) {
        this.path = Paths.get(path);
        this.recentlyUsedCommits = new HashMap<>();
        this.branches = new ArrayList<>();
    }

    public Branch getHeadBranch() {
        return headBranch;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setHeadBranch(Branch headBranch) {
        this.headBranch = headBranch;
    }

    public Map<String, Commit> getRecentlyUsedCommits() {
        return recentlyUsedCommits;
    }

    public Path getPath() {
        return path;
    }


    public void addCommitToActiveBranch(Commit commitToAdd) {
        this.headBranch.setLastCommit(commitToAdd);
    }

    String getHeadBranchNameFromBranchesDir() throws IOException {
        String headBranchPath = this.path.toString() + "/.magit/branches/HEAD.txt";
        File headFile = new File(headBranchPath);
        String headBranchName = MagitManager.convertTextFileToString(headFile.getPath());
        return headBranchName;
    }

     boolean branchExistsInList(String name) {
        boolean branchExists = false;
        for (Branch b : branches) {
            if (b.getName().equals(name)){
                branchExists = true;
            }
        }
        return branchExists;
    }

    public Branch FindBranchByName(String branchName) {
        Branch branchToFind = null;
        for(Branch branch : branches){
            if(branch.getName().equals(branchName)){
                branchToFind = branch;
                break;
            }
        }

        return branchToFind;
    }

    public String GetBranchesDirPath(){
        return this.getPath().toString() + File.separator + ".magit" + File.separator + "branches";
    }

    public String GetObjectsDirPath(){
        return this.getPath().toString() + File.separator + ".magit" + File.separator + "objects";
    }
}
