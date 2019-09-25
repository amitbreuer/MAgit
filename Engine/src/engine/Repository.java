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
    private Map<String, Commit> recentlyUsedCommits;
    private Map<String, Branch> branches;
    private Branch headBranch;
    private Path remoteRepositoryPath;
    private String remoteRepositoryname;

    public Repository(String path) {
        this.path = Paths.get(path);
        this.recentlyUsedCommits = new HashMap<>();
        this.branches = new HashMap<>();
    }

    public void setRemoteRepositoryname(String remoteRepositoryname) {
        this.remoteRepositoryname = remoteRepositoryname;
    }

    public String getRemoteRepositoryname() {
        return remoteRepositoryname;
    }

    public void setRemoteRepositoryPath(Path remoteRepositoryPath) {
        this.remoteRepositoryPath = remoteRepositoryPath;
    }

    public Path getRemoteRepositoryPath() {
        return remoteRepositoryPath;
    }

    public Branch getHeadBranch() {
        return headBranch;
    }

    public Map<String, Branch> getBranches() {
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
        return branches.containsKey(name);
    }

    public Branch FindBranchByName(String branchName) {
        return branches.get(branchName);
    }

    public String GetBranchesDirPath() {
        return this.getPath().toString() + File.separator + ".magit" + File.separator + "branches";
    }

    public String GetObjectsDirPath() {
        return this.getPath().toString() + File.separator + ".magit" + File.separator + "objects";
    }

    public void setBranches(Map<String, Branch> branches) {
        this.branches = branches;
    }

    public String GetRemoteRepositoryObjectsDirPath(){
        if(!this.remoteRepositoryname.equals("") && remoteRepositoryname != null){
            return this.remoteRepositoryPath + File.separator + ".magit" + File.separator + "objects";
        } else {
            return null;
        }
    }

    public String GetRemoteRepositoryBranchesDirPath(){
        if(!this.remoteRepositoryname.equals("") && remoteRepositoryname != null){
            return this.remoteRepositoryPath + File.separator + ".magit" + File.separator + "branches";
        } else {
            return null;
        }
    }
}
