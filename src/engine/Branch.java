package engine;

public class Branch {

    private String name;
    private Commit lastCommit;

    public Branch(String name, Commit lastCommit) {
        this.name = name;
        this.lastCommit = lastCommit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastCommit(Commit lastCommit) {
        this.lastCommit = lastCommit;
    }

    public Commit getLastCommit() {
        return lastCommit;
    }

    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Branch name: ");
        sb.append(this.name);
        sb.append("\r\n");

        if(this.getLastCommit()!=null) {
            sb.append("Commit's sha1: ");
            sb.append(this.getLastCommit().Sha1Commit());
            sb.append("\r\n");
            sb.append("Commit's message: ");
            sb.append(this.getLastCommit().getMessage());
        }
        else {
            sb.append("this branch does not contain commit");
        }
        return sb.toString();
    }
}
