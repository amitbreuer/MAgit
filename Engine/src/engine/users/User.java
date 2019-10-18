package engine.users;

import engine.Commit;
import engine.MagitManager;
import engine.Repository;
import engine.users.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private List<RepositoryData> repositoriesDatas;
    private MagitManager magitManager;

    public User(String username) {
        this.username = username;
        this.repositoriesDatas = new ArrayList<>();
        this.magitManager = new MagitManager();
    }

    public String getUsername() {
        return username;
    }

    public List<RepositoryData> getRepositoriesDatas() {
        return repositoriesDatas;
    }

    public MagitManager getMagitManager() {
        return magitManager;
    }

    public void CreateRepositoryDataForNewRepository(String repositoryName) {
        Integer numberOfBranches;
        String activeBranchName = null;
        String lastCommitDate = null;
        String lastCommitMessage = null;
        String lastCommitSha1;
        String repositoryPath = Constants.usersDirectoryPath + File.separator + username + File.separator + repositoryName;
        String repositoryBranchesPath = repositoryPath + File.separator + ".magit" + File.separator + "branches";

        numberOfBranches = new File(repositoryBranchesPath).listFiles().length;

        try {
            activeBranchName = MagitManager.convertTextFileToString(repositoryBranchesPath + File.separator + "HEAD.txt");
            lastCommitSha1 = MagitManager.convertTextFileToString(repositoryBranchesPath + File.separator + activeBranchName + ".txt");
            Commit lastCommit = MagitManager.createCommitFromObjectFile(lastCommitSha1, repositoryPath + File.separator + ".magit" + File.separator + "objects");
            lastCommitDate = lastCommit.getDateCreated();
            lastCommitMessage = lastCommit.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        repositoriesDatas.add(new RepositoryData(repositoryName, activeBranchName, numberOfBranches, lastCommitDate, lastCommitMessage));
    }
}

