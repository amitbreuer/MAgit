package engine.users;

import engine.Commit;
import engine.MagitManager;
import engine.Repository;
import engine.users.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private List<RepositoryData> repositoriesData;
    private MagitManager magitManager;
    private List<String> messages = new ArrayList<>();
    private static final Object addMessageLock = new Object();



    public User(String username) {
        this.username = username;
        this.repositoriesData = new ArrayList<>();
        this.magitManager = new MagitManager();
    }

    public String getUsername() {
        return username;
    }

    public List<RepositoryData> getRepositoriesData() {
        return repositoriesData;
    }

    public MagitManager getMagitManager() {
        return magitManager;
    }

    public void AddMessage(String message) {
        synchronized (addMessageLock) {
            this.messages.add(message);
        }
    }


    public void ClearMessages() {
        this.messages.clear();
    }

    public void CreateRepositoryDataForNewRepository(String repositoryName) {
        Integer numberOfBranches = 0;
        String activeBranchName = null;
        String lastCommitDate = null;
        String lastCommitMessage = null;
        String lastCommitSha1;
        String repositoryPath = Constants.usersDirectoryPath + File.separator + username + File.separator + repositoryName;
        String repositoryBranchesPath = repositoryPath + File.separator + ".magit" + File.separator + "branches";

        File[] repositoryBranchesFiles = new File(repositoryBranchesPath).listFiles();
        numberOfBranches = repositoryBranchesFiles.length - 1;

        for (File file : repositoryBranchesFiles) {
            if (file.isDirectory()){
                numberOfBranches+= file.listFiles().length-1;
                break;
            }
        }



        Commit lastCommit = magitManager.GetLastCommitOfRepository();
        activeBranchName = magitManager.GetHeadBranchName();
        lastCommitDate = lastCommit.getDateCreated();
        lastCommitMessage = lastCommit.getMessage();


        repositoriesData.add(new RepositoryData(repositoryName, activeBranchName, numberOfBranches, lastCommitDate, lastCommitMessage));
    }

    public int getMessagesVersion() {
        return messages.size();
    }

    public List<String> getMessages(int fromIndex) {
        return messages.subList(fromIndex, messages.size());
    }
}

