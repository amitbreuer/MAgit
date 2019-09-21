package exceptions;

public class ThereIsRBPointingToThisCommitException extends Exception {
    private String RBName;
    private String RBCommitSha1;

    public ThereIsRBPointingToThisCommitException(String RBName, String commitSha1) {
        this.RBName =RBName;
        this.RBCommitSha1 = commitSha1;
    }

    public String getRBName() {
        return RBName;
    }

    public String getRBCommitSha1() {
        return RBCommitSha1;
    }
}
