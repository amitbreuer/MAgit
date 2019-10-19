package engine.users;

public class CommitData {
    private String sha1;
    private String message;
    private String creator;
    private String dateCreated;

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
}
