package engine.users;

public class RepositoryData {

    private String name;
    private String activeBranchName;
    Integer  numberOfBranches;
    private String lastCommitDate;
    private String lastCommitMessage;

    public void setName(String name) {
        this.name = name;
    }

    public void setActiveBranchName(String activeBranchName) {
        this.activeBranchName = activeBranchName;
    }

    public void setNumberOfBranches(Integer numberOfBranches) {
        this.numberOfBranches = numberOfBranches;
    }

    public void setLastCommitDate(String lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    public void setLastCommitMessage(String lastCommitMessage) {
        this.lastCommitMessage = lastCommitMessage;
    }

    public String getName() {
        return name;
    }

    public String getActiveBranchName() {
        return activeBranchName;
    }

    public Integer getNumberOfBranches() {
        return numberOfBranches;
    }

    public String getLastCommitDate() {
        return lastCommitDate;
    }

    public String getLastCommitMessage() {
        return lastCommitMessage;
    }

    public RepositoryData(String name, String activeBranchName, Integer numberOfBranches, String lastCommitDate, String lastCommitMessage){
        this.name = name;
        this.activeBranchName = activeBranchName;
        this.numberOfBranches = numberOfBranches;
        this.lastCommitDate = lastCommitDate;
        this.lastCommitMessage = lastCommitMessage;
    }
}
