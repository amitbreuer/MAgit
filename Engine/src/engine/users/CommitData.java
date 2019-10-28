package engine.users;

import java.util.List;

public class CommitData {
    private String sha1;
    private String message;
    private String creator;
    private String dateCreated;
    private List<String> pointingBranches;

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setPointingBranches(List<String> pointingBranches) {
        this.pointingBranches = pointingBranches;
    }
}
