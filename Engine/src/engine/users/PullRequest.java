package engine.users;

public class PullRequest {
    public enum Status {
        OPEN,
        CLOSED,
        REJECTED
    }

    private String creator;
    private String targetBranch;
    private String baseBranch;
    private String dateCreated;
    private Status status;

    public PullRequest(String creator, String targetBranch, String baseBranch, String dateCreated, Status status) {
        this.creator = creator;
        this.targetBranch = targetBranch;
        this.baseBranch = baseBranch;
        this.dateCreated = dateCreated;
        this.status = status;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public String getBaseBranch() {
        return baseBranch;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
