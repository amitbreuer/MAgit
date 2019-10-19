package engine.users;

import engine.Commit;
import engine.MagitManager;
import engine.users.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final Map<String,User> usersMap;

    public UserManager() {
        usersMap = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        User newUser = new User(username);
        usersMap.put(username,newUser);
        new File("c:" + File.separator + "magit-ex3" + File.separator + username).mkdir();
    }

    public synchronized void removeUser(String username) {
        usersMap.remove(username);
    }

    public synchronized Map<String,User> getUsers() {
        return Collections.unmodifiableMap(usersMap);
    }

    public boolean isUserExists(String username) {
        return usersMap.containsKey(username);
    }

    private SingleUserData getUserData(String userName) {
        SingleUserData userData = new SingleUserData(userName);
        File userDirectory = new File(Constants.usersDirectoryPath + File.separator + userName);
        for (File file : userDirectory.listFiles()) {
            addRepositoryDirectoryToUserData(userData, file, userName);
        }
        return userData;
    }

    private void addRepositoryDirectoryToUserData(SingleUserData userData, File directoryFile, String userName) {
        RepositoryData repositoryData;
        String name;
        Integer numberOfBranches;
        String activeBranchName = null;
        String lastCommitDate = null;
        String lastCommitMessage = null;
        String lastCommitSha1;
        String repositoryPath = Constants.usersDirectoryPath + File.separator + userName + File.separator + directoryFile.getName();
        String repositoryBranchesPath = repositoryPath + File.separator + ".magit" + File.separator + "branches";

        name = directoryFile.getName();
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

        repositoryData = new RepositoryData(name, activeBranchName, numberOfBranches, lastCommitDate, lastCommitMessage);
        userData.AddRepositoryDataToRepositorysDataList(repositoryData);
    }

   /* public AllUsersData GetAllUsersData(String currentUserName) {
        AllUsersData allUsersData = new AllUsersData();
        SingleUserData userDataToAdd;
        File usersDirectory = new File(Constants.usersDirectoryPath);

        for (File file : usersDirectory.listFiles()) {
            userDataToAdd = getUserData(file.getName());
            if (userDataToAdd.getUserName().equals(currentUserName)) {
                allUsersData.setCurrentUserData(userDataToAdd);
            } else {
                allUsersData.AddOtherUserData(userDataToAdd);
            }
        }
        return allUsersData;
    }*/

   /* public SingleUserData GetCurrentUserData(String currentUserName) {
        SingleUserData currentUserData;
        String currentUserDirectoryPath = Constants.usersDirectoryPath + File.separator + currentUserName;
        if (!Files.exists(Paths.get(currentUserDirectoryPath))) {
            return null;
        }
        else {
            currentUserData = getUserData(currentUserName);
            return currentUserData;
        }
    }*/

    /*public List<SingleUserData> GetOtherUsersData(String currentUserName) {
        List<SingleUserData> otherUsersData = new ArrayList<>();

        SingleUserData userDataToAdd;
        File usersDirectory = new File(Constants.usersDirectoryPath);

        for (File file : usersDirectory.listFiles()) {
            userDataToAdd = getUserData(file.getName());
            if (!file.getName().equals(currentUserName)) {
                otherUsersData.add(userDataToAdd);
            }
        }
        return otherUsersData;
    }
*/
    public User getUser(String username) {
        return usersMap.get(username);
    }
}
